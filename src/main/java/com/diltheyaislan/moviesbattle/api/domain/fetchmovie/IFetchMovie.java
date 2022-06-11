package com.diltheyaislan.moviesbattle.api.domain.fetchmovie;

import java.util.Set;

import com.diltheyaislan.moviesbattle.api.domain.fetchmovie.exception.FetchMovieException;

public interface IFetchMovie {

	public Set<MovieItem> fetch() throws FetchMovieException;
}
