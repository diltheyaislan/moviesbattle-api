package com.diltheyaislan.moviesbattle.api.web.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessTokenResponse implements Serializable {

private static final long serialVersionUID = 1L;
	
	private String accessToken;
	private UserResponse user;
}
