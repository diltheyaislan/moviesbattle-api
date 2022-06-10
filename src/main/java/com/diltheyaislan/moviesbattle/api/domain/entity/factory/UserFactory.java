package com.diltheyaislan.moviesbattle.api.domain.entity.factory;

import com.diltheyaislan.moviesbattle.api.domain.dto.SignUpDTO;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;

public class UserFactory {

	public static User create(SignUpDTO dto) {
		return User.builder()
				.name(dto.getName())
				.username(dto.getUsername())
				.password(dto.getPassword())
				.build();
	}
}
