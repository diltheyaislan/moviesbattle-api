package com.diltheyaislan.moviesbattle.api.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diltheyaislan.moviesbattle.api.domain.dto.SignUpDTO;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.factory.UserFactory;
import com.diltheyaislan.moviesbattle.api.domain.exception.UserAlreadySignedUpException;
import com.diltheyaislan.moviesbattle.api.domain.repository.UserRepository;

@Service
public class AuthService {

	@Autowired
	UserRepository userRepository;
	
	public User signUp(SignUpDTO dto) throws UserAlreadySignedUpException {
		

		System.out.println("FALOHU: " + dto.getUsername());
		User user = UserFactory.create(dto);
		validateIfUserAlreadySignedUp(user);
		return userRepository.save(user);
	}
	
	private void validateIfUserAlreadySignedUp(User user) throws UserAlreadySignedUpException {

		Optional<User> optionalUser = userRepository.findOneByUsername(user.getUsername());

		if (optionalUser.isEmpty()) {
			return;
		}
		
		throw new UserAlreadySignedUpException(optionalUser.get().getUsername());
	}
}
