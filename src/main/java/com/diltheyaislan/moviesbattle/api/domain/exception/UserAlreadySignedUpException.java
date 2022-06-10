package com.diltheyaislan.moviesbattle.api.domain.exception;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAlreadySignedUpException extends BusinessException {

	private static final long serialVersionUID = 1L;
	private String username;
	
	public UserAlreadySignedUpException(String username) {
		super(
				StatusException.BAD_REQUEST, 
				"message.error.userAlreadySignedUp", 
				CommonError.Argument.of("username", username));
		this.username = username;
	}
}
