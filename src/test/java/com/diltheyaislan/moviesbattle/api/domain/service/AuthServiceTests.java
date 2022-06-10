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
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError;
import com.diltheyaislan.moviesbattle.api.domain.dto.SignUpDTO;
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
	public void givenSignUpDTO_whenSignUpUser_shouldSaveAndReturnsCreatedUser() throws BusinessException {
		
		User expectedUser = user;
	
		when(userRepository.findOneByUsername((String) notNull())).thenReturn(Optional.empty());
		when(userRepository.save((User) notNull())).thenReturn(expectedUser);
		
		SignUpDTO dto = new SignUpDTO();
		dto.setName("John Doe");
		dto.setUsername("johndoe");
		dto.setPassword("XPTO");
		
		User createdUser = authService.signUp(dto);
		
		assertThat(createdUser.getId(), notNullValue());
		assertThat(createdUser.getId().toString(), equalTo(expectedUser.getId().toString()));
		assertThat(createdUser.getName().toString(), equalTo(expectedUser.getName()));
		assertThat(createdUser.getUsername().toString(), equalTo(expectedUser.getUsername()));
		assertThat(createdUser.getCreatedAt(), notNullValue());
		assertThat(createdUser.getUpdatedAt(), notNullValue());
	}
	
	@Test
	public void givenSignUpDTO_whenSignUpUserWithAnExistingUsername_shouldThrowsUserAlreadySignedUpException() throws BusinessException {
		
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
}