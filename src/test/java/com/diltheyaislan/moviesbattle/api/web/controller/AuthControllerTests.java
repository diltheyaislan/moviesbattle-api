package com.diltheyaislan.moviesbattle.api.web.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.diltheyaislan.moviesbattle.api.MoviesbattleApplication;
import com.diltheyaislan.moviesbattle.api.config.LocaleConfiguration;
import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError.Argument;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.ResponseCommonError;
import com.diltheyaislan.moviesbattle.api.domain.dto.SignUpDTO;
import com.diltheyaislan.moviesbattle.api.domain.dto.UserAccessTokenDTO;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadySignedUpException;
import com.diltheyaislan.moviesbattle.api.domain.repository.UserRepository;
import com.diltheyaislan.moviesbattle.api.domain.service.AuthService;
import com.diltheyaislan.moviesbattle.api.web.request.SignInBodyRequest;
import com.diltheyaislan.moviesbattle.api.web.request.SignUpBodyRequest;

@SpringBootTest(
		webEnvironment = WebEnvironment.RANDOM_PORT,
		classes = { 
				MoviesbattleApplication.class })
@EnableAutoConfiguration(exclude = {
		SecurityAutoConfiguration.class,
		DataSourceAutoConfiguration.class })
@ActiveProfiles({ "test" })
@ExtendWith(SpringExtension.class)
@AutoConfigureWebTestClient
public class AuthControllerTests {

	@Autowired
	private WebTestClient webClient;

	@MockBean
    private AuthService authService;

	@MockBean
    private UserRepository userRepository;
	
	private MessageSource messageSource;
	
	private User user;
	
	@BeforeEach
	public void init() {
		
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	    messageSource.setBasenames(
	    		"org/hibernate/validator/ValidationMessages",
	    		"classpath:/messages/messages",
                "classpath:/messages/validation"
        );
        messageSource.setDefaultEncoding(LocaleConfiguration.ISO_8859_1);
        messageSource.setDefaultLocale(Locale.ENGLISH);
	    this.messageSource = messageSource;
		
	    user = User.builder()
	    		.id(UUID.fromString("c44152d7-591c-4108-b805-43d45b71e284"))
	    		.name("John Doe")
	    		.username("johndoe")
	    		.createdAt(LocalDateTime.of(2022, 3, 29, 10, 30, 45))
	    		.updatedAt(LocalDateTime.of(2022, 3, 29, 10, 30, 45))
	    		.build();
	}
	
	@Test
    public void givenValidRequest_whenSignUp_shouldReturnsCreatedUser() throws BusinessException {

		User expectedUser = user;
		
		when(authService.signUp((SignUpDTO) notNull())).thenReturn(expectedUser);
		
		SignUpDTO bodyRequest = new SignUpDTO();
		bodyRequest.setName("John Doe");
		bodyRequest.setUsername("johndoe");
		bodyRequest.setPassword("XPTO");
		
		webClient
			.post().uri("/auth/signup")
			.bodyValue(bodyRequest)
			.header("Accept-Language", "en")
			.exchange()
	    	.expectStatus()
	    		.isCreated()
	    	.expectBody()
	    		.jsonPath("id").isNotEmpty()
	    		.jsonPath("id").isEqualTo(expectedUser.getId().toString())
	    		.jsonPath("name").isEqualTo(bodyRequest.getName())
	    		.jsonPath("username").isEqualTo(bodyRequest.getUsername());
    }
	
	@Test
    public void givenInvalidRequestWithNullValueForNameAndUsernameAndPassword_whenSignUp_shouldReturnsBadRequestAndResponseErrorWithInvalidAttributes() throws BusinessException {
    	
		SignUpDTO bodyRequest = new SignUpDTO();
		bodyRequest.setName(null);
		bodyRequest.setUsername(null);
		bodyRequest.setPassword(null);
		
		var result 
			= webClient
				.post().uri("/auth/signup")
				.bodyValue(bodyRequest)
				.header("Accept-Language", "en")
				.exchange()
				.expectStatus()
					.isBadRequest()
	    		.expectBody(ResponseCommonError.class)
	    			.returnResult()
	    			.getResponseBody();
		
		Argument nameArg = createArgument("name", "javax.validation.constraints.NotBlank.message");
		Argument usernameArg = createArgument("username", "javax.validation.constraints.NotBlank.message");
		Argument passwordArg = createArgument("password", "javax.validation.constraints.NotBlank.message");
		
		assertTrue(result.getError().getArgs().contains(nameArg));
		assertTrue(result.getError().getArgs().contains(usernameArg));
		assertTrue(result.getError().getArgs().contains(passwordArg));
    }
	
	@Test
    public void givenRequest_whenSignUpWithExistingUsername_shouldReturnsBadRequestAndResponseErrorWithInvalidAttribute() throws BusinessException {
    	
		String existingUsername = "johndoe";
		when(authService.signUp((SignUpDTO) notNull())).thenThrow(new UserAlreadySignedUpException(existingUsername));
		
		SignUpBodyRequest bodyRequest = new SignUpBodyRequest();
		bodyRequest.setName("John Doe");
		bodyRequest.setUsername("johndoe");
		bodyRequest.setPassword("XPTO");
		
		var result 
			= webClient
				.post().uri("/auth/signup")
				.bodyValue(bodyRequest)
				.header("Accept-Language", "en")
				.exchange()
				.expectStatus()
					.isBadRequest()
	    		.expectBody(ResponseCommonError.class)
	    			.returnResult()
	    			.getResponseBody();
		
		Argument usernameArg = Argument.of("username", existingUsername);
		
		assertTrue(result.getError().getArgs().contains(usernameArg));
    }
	
	@Test
    public void givenValidRequest_whenSignIn_shouldReturnsAccessTokenAndAuthenticatedUser() throws BusinessException {

		User expectedUser = user;
		UserAccessTokenDTO userAccessTokenDTOExpected = UserAccessTokenDTO.builder()
				.accessToken("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIyMzJiZTI2NS03NmYxLTRiYTgtYWYxYS01YzQ3NGVkOTNjZjAiLCJpYXQiOjE2NTQ4ODY4NTYsImV4cCI6MTY1NDg5MDQ1Nn0.21aUodbhO4gNZIsGZrN-PE5vLuR_NVYXDV-UAnC7lKMt7e5SKenUGI6XflwCm05c")
				.user(expectedUser)
				.build();
		
		when(authService.signIn((String) notNull(), (String) notNull())).thenReturn(userAccessTokenDTOExpected);
		
		SignInBodyRequest bodyRequest = new SignInBodyRequest();
		bodyRequest.setUsername("johndoe");
		bodyRequest.setPassword("XPTO");
		
		webClient
			.post().uri("/auth/signin")
			.bodyValue(bodyRequest)
			.header("Accept-Language", "en")
			.exchange()
	    	.expectStatus()
	    		.isOk()
	    	.expectBody()
	    		.jsonPath("accessToken").isNotEmpty()
	    		.jsonPath("accessToken").isEqualTo(userAccessTokenDTOExpected.getAccessToken())	
	    		.jsonPath("user.id").isEqualTo(expectedUser.getId().toString())
	    		.jsonPath("user.name").isEqualTo(expectedUser.getName())
	    		.jsonPath("user.username").isEqualTo(bodyRequest.getUsername());
    }
	
	@Test
    public void givenRequest_whenSignInWithInvalidCredentials_shouldReturnsUnauthorizedAndResponseErrorWithBadCredentialsMessage() throws BusinessException {
    	
		String expectedErrorMessage = "Bad credentials";
		
		when(authService.signIn((String) notNull(), (String) notNull())).thenThrow(new BusinessException(
				StatusException.UNAUTHORIZED, "message.error.badCredentials"));
		
		SignInBodyRequest bodyRequest = new SignInBodyRequest();
		bodyRequest.setUsername("wrongUsername");
		bodyRequest.setPassword("wrongPassword");
		
		var result 
			= webClient
				.post().uri("/auth/signin")
				.bodyValue(bodyRequest)
				.header("Accept-Language", "en")
				.exchange()
				.expectStatus()
					.isUnauthorized()
	    		.expectBody(ResponseCommonError.class)
	    			.returnResult()
	    			.getResponseBody();
		
		assertTrue(result.getError().getMessage().equalsIgnoreCase(expectedErrorMessage));
    }
	
	private Argument createArgument(String name, String messageCode) {
		return new Argument(name, messageSource.getMessage(messageCode, null, Locale.ENGLISH));
	}
}
