package com.diltheyaislan.moviesbattle.api.domain.dto;

import com.diltheyaislan.moviesbattle.api.domain.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserAccessTokenDTO {

	private String accessToken;
	private User user;
}
