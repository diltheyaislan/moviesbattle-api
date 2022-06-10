package com.diltheyaislan.moviesbattle.api.domain.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError;
import com.diltheyaislan.moviesbattle.api.domain.dto.SignUpDTO;
import com.diltheyaislan.moviesbattle.api.domain.dto.UserAccessTokenDTO;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadySignedUpException;
import com.diltheyaislan.moviesbattle.api.domain.repository.UserRepository;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
		SecurityAutoConfiguration.class,
		DataSourceAutoConfiguration.class })
@ActiveProfiles({ "test" })
@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

	@Autowired
	private AuthService authService;
	
	@MockBean
	private UserRepository userRepository;

	private User user;
	
	@BeforeEach
    public void init() {
		user = new User();
		user.setId(UUID.fromString("c44152d7-591c-4108-b805-43d45b71e284"));
		user.setName("John Doe");
		user.setUsername("johndoe");
		user.setCreatedAt(LocalDateTime.of(2022, 3, 29, 10, 30, 45));
		user.setUpdatedAt(LocalDateTime.of(2022, 3, 29, 10, 30, 45));
	}
	
	@Test
	public void givenSignUpDTO_whenSignUp_shouldSaveAndReturnsCreatedUser() throws BusinessException {
		
		User expectedUser = user;
	
		when(userRepository.findOneByUsername((String) notNull())).thenReturn(Optional.empty());
		when(userRepository.save((User) notNull())).thenReturn(expectedUser);
		
		SignUpDTO dto = new SignUpDTO();
		dto.setName("John Doe");
		dto.setUsername("johndoe");
		dto.setPassword("XPTO");
		
		User userAccessTokenDTO = authService.signUp(dto);
		
		assertThat(userAccessTokenDTO.getId(), notNullValue());
		assertThat(userAccessTokenDTO.getId().toString(), equalTo(expectedUser.getId().toString()));
		assertThat(userAccessTokenDTO.getName().toString(), equalTo(expectedUser.getName()));
		assertThat(userAccessTokenDTO.getUsername().toString(), equalTo(expectedUser.getUsername()));
		assertThat(userAccessTokenDTO.getCreatedAt(), notNullValue());
		assertThat(userAccessTokenDTO.getUpdatedAt(), notNullValue());
	}
	
	@Test
	public void givenSignUpDTO_whenSignUpWithAnExistingUsername_shouldThrowsUserAlreadySignedUpException() throws BusinessException {
		
		User existingUser = user;
		
		when(userRepository.findOneByUsername((String) notNull())).thenReturn(Optional.of(existingUser));
	
		SignUpDTO dto = new SignUpDTO();
		dto.setName("John Doe");
		dto.setUsername("johndoe");
		dto.setPassword("XPTO");
		
		UserAlreadySignedUpException exception = assertThrows(UserAlreadySignedUpException.class, () -> {
			authService.signUp(dto);
		});
		
		String expectedMessage = "message.error.userAlreadySignedUp";
		String exceptionMessage = exception.getMessage();
		CommonError.Argument expectedExceptionArg = CommonError.Argument.of("username", "johndoe");
		CommonError.Argument[] exceptionArgs = exception.getArguments();

		assertTrue(exceptionMessage.equalsIgnoreCase(expectedMessage));
		assertTrue(Arrays.asList(exceptionArgs).contains(expectedExceptionArg));
	}
	
	@Test
	public void givenUsernameAndPassword_whenSignIn_shouldAuthenticatedAndReturnsAuthenticatedUserAndAcessToken() throws BusinessException {
		
		User expectedUser = user;
		expectedUser.setPassword("$2a$10$3dcZA5vYP5brmxLjKS2OquMEwxGA17AIxwzhxkv6MJcLjeCY7Lg7.");
	
		when(userRepository.findOneByUsername((String) notNull())).thenReturn(Optional.of(expectedUser));
		when(userRepository.save((User) notNull())).thenReturn(expectedUser);
		
		String username = "johndoe";
		String password = "XPTO";
		
		UserAccessTokenDTO userAccessTokenDTO = authService.signIn(username, password);

		assertThat(userAccessTokenDTO.getAccessToken(), notNullValue());
		assertThat(userAccessTokenDTO.getUser().getId().toString(), equalTo(expectedUser.getId().toString()));
		assertThat(userAccessTokenDTO.getUser().getName().toString(), equalTo(expectedUser.getName()));
		assertThat(userAccessTokenDTO.getUser().getUsername().toString(), equalTo(expectedUser.getUsername()));
		assertThat(userAccessTokenDTO.getUser().getCreatedAt(), notNullValue());
		assertThat(userAccessTokenDTO.getUser().getUpdatedAt(), notNullValue());
	}
	
	@Test
	public void givenUsernameAndPassword_whenSignInWithInvalidCredentials_shouldBusinessExceptionWithBadCredentialsMessageAndUnauthorizedStatusException() {
		
		User existingUser = user;
		existingUser.setPassword("$2a$10$3dcZA5vYP5brmxLjKS2OquMEwxGA17AIxwzhxkv6MJcLjeCY7Lg7.");
		
		when(userRepository.findOneByUsername((String) notNull())).thenReturn(Optional.of(existingUser));
	
		String username = "wrongUsername";
		String password = "wrongPassword";
		
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			authService.signIn(username, password);
		});
		
		String expectedMessage = "message.error.badCredentials";
		StatusException expectedStatusException = StatusException.UNAUTHORIZED;
		String exceptionMessage = exception.getMessage();
		StatusException exceptionStatusException = exception.getStatus();

		assertTrue(exceptionMessage.equalsIgnoreCase(expectedMessage));
		assertTrue(exceptionStatusException == expectedStatusException);
	}
}