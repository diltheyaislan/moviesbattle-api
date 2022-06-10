package com.diltheyaislan.moviesbattle.api.core.exception;

public enum StatusException {
	
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	FORBIDDEN(403),
	NOT_FOUND(404),
	INTERNAL_SERVER_ERROR(500),
	SERVICE_UNAVAILABLE(503);
	
	private final int statusCode;

	private StatusException(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
