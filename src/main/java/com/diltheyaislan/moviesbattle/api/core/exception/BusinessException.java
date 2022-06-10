package com.diltheyaislan.moviesbattle.api.core.exception;

import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError.Argument;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private StatusException status;
	private Object[] messageArguments; 
	private Argument[] arguments;
	
	public BusinessException(String message) {
		super(message);
	}
	
	public BusinessException(StatusException status, String message) {
		super(message);
		this.status = status;
	}
	
	public BusinessException(String message, Object... messageArguments) {
		super(message);
		this.messageArguments = messageArguments;
	}
	
	public BusinessException(StatusException status, String message, Object... messageArguments) {
		super(message);
		this.status = status;
		this.messageArguments = messageArguments;
	}
	
	public BusinessException(Throwable cause, String message) {
		super(message, cause);
	}
	
	public BusinessException(Throwable cause, StatusException status, String message) {
		super(message, cause);
		this.status = status;
	}
	
	public BusinessException(Throwable cause, String message, Object... messageArguments) {
		super(message, cause);
		this.messageArguments = messageArguments;
	}
	
	public BusinessException(Throwable cause, StatusException status, String message, Object... messageArguments) {
		super(message, cause);
		this.status = status;
		this.messageArguments = messageArguments;
	}
	
	public BusinessException(String message, Argument... arguments) {
		super(message);
		this.arguments = arguments;
	}
	
	public BusinessException(StatusException status, String message, Argument... arguments) {
		super(message);
		this.status = status;
		this.arguments = arguments;
	}
	
	public BusinessException(String message, Object[] messageArguments, Argument... arguments) {
		super(message);
		this.messageArguments = messageArguments;
		this.arguments = arguments;
	}
	
	public BusinessException(StatusException status, String message, Object[] messageArguments, Argument... arguments) {
		super(message);
		this.status = status;
		this.messageArguments = messageArguments;
		this.arguments = arguments;
	}
}
