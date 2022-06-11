package com.diltheyaislan.moviesbattle.api.web.response;

import java.time.LocalDateTime;
import java.util.UUID;

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
public class GameCreatedResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;
	
	private UUID id;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
