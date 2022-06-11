package com.diltheyaislan.moviesbattle.api.domain.dto;

import com.diltheyaislan.moviesbattle.api.domain.dto.enums.AnswerResult;

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
public class AnswerResultDTO {

	private AnswerResult result;
	private Integer remainingAttempts;
}
