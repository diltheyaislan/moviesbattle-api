package com.diltheyaislan.moviesbattle.api.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MovieOptionResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
}
