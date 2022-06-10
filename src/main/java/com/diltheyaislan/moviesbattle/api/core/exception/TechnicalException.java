package com.diltheyaislan.moviesbattle.api.core.exception;

import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError.Argument;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TechnicalException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private StatusException status;
	private Object[] messageArguments; 
	private Argument[] arguments;
	
	public TechnicalException(String message) {
		super(message);
	}
	
	public TechnicalException(StatusException status, String message) {
		super(message);
		this.status = status;
	}
	
	public TechnicalException(String message, Object... messageArguments) {
		super(message);
		this.messageArguments = messageArguments;
	}
	
	public TechnicalException(StatusException status, String message, Object... messageArguments) {
		super(message);
		this.status = status;
		this.messageArguments = messageArguments;
	}
	
	public TechnicalException(Throwable cause, String message) {
		super(message, cause);
	}
	
	public TechnicalException(Throwable cause, StatusException status, String message) {
		super(message, cause);
		this.status = status;
	}
	
	public TechnicalException(Throwable cause, String message, Object... messageArguments) {
		super(message, cause);
		this.messageArguments = messageArguments;
	}
	
	public TechnicalException(Throwable cause, StatusException status, String message, Object... messageArguments) {
		super(message, cause);
		this.status = status;
		this.messageArguments = messageArguments;
	}
	
	public TechnicalException(String message, Argument... arguments) {
		super(message);
		this.arguments = arguments;
	}
	
	public TechnicalException(StatusException status, String message, Argument... arguments) {
		super(message);
		this.status = status;
		this.arguments = arguments;
	}
	
	public TechnicalException(String message, Object[] messageArguments, Argument... arguments) {
		super(message);
		this.messageArguments = messageArguments;
		this.arguments = arguments;
	}
	
	public TechnicalException(StatusException status, String message, Object[] messageArguments, Argument... arguments) {
		super(message);
		this.status = status;
		this.messageArguments = messageArguments;
		this.arguments = arguments;
	}
}
