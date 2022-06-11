package com.diltheyaislan.moviesbattle.api.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.ResourceNotFoundException;
import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.domain.dto.SignUpDTO;
import com.diltheyaislan.moviesbattle.api.domain.dto.UserAccessTokenDTO;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.factory.UserFactory;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadySignedUpException;
import com.diltheyaislan.moviesbattle.api.domain.repository.IUserRepository;
import com.diltheyaislan.moviesbattle.api.security.jwt.JwtTokenProvider;

@Service
public class AuthService {

	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	public User signUp(SignUpDTO dto) throws BusinessException {
		
		User user = UserFactory.create(dto);
		validateIfUserAlreadySignedUp(user);
		return userRepository.save(user);
	}
	
	public UserAccessTokenDTO signIn(String username, String password) throws BusinessException {

		String accessToken = authenticate(username, password);
			
		User user = userRepository
				.findOneByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
	
		return new UserAccessTokenDTO(accessToken, user);			
	}
	
	private void validateIfUserAlreadySignedUp(User user) throws UserAlreadySignedUpException {

		Optional<User> optionalUser = userRepository.findOneByUsername(user.getUsername());
		if (!optionalUser.isEmpty()) {
			throw new UserAlreadySignedUpException(optionalUser.get().getUsername());
		}
	}

	private String authenticate(String username, String password) throws BusinessException {
		try {
			Authentication authentication = authenticationManager.authenticate(
		        new UsernamePasswordAuthenticationToken(username, password)
	        );
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String token = tokenProvider.createToken(authentication);
			
			return token;
		} catch (BadCredentialsException ex) {
			throw new BusinessException(
					StatusException.UNAUTHORIZED, "message.error.badCredentials");
		}
	}
}
