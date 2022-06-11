package com.diltheyaislan.moviesbattle.api.domain.fetchmovie.exception;

import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.core.exception.TechnicalException;

public class FetchMovieException extends TechnicalException {

	private static final long serialVersionUID = 1L;
	
	public FetchMovieException() {
		super(StatusException.SERVICE_UNAVAILABLE, "message.error.failedToFetchMovies");
	}

}
