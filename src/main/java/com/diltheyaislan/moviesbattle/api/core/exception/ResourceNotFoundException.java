package com.diltheyaislan.moviesbattle.api.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourceNotFoundException extends BusinessException {

	private static final long serialVersionUID = 1L;
	
	private String resource;
	private String key;
	private Object value;
	
	public ResourceNotFoundException(String resource, String key, Object value) {
		super(
				StatusException.BAD_REQUEST, 
				"message.error.resourceNotFound", 
				resource, key + "=" + value);
		this.resource = resource;
		this.key = key;
		this.value = value;
	}

}
