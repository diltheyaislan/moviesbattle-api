package com.diltheyaislan.moviesbattle.api.web.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserAccessTokenResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private String accessToken;
	private UserResponse user;
}
