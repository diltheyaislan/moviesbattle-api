package com.diltheyaislan.moviesbattle.api.core.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCommonError {

	CommonError error;
}
