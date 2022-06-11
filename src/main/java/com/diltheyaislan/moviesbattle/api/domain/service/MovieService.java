package com.diltheyaislan.moviesbattle.api.domain.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.diltheyaislan.moviesbattle.api.domain.entity.Movie;
import com.diltheyaislan.moviesbattle.api.domain.fetchmovie.IFetchMovie;
import com.diltheyaislan.moviesbattle.api.domain.fetchmovie.MovieItem;
import com.diltheyaislan.moviesbattle.api.domain.fetchmovie.exception.FetchMovieException;
import com.diltheyaislan.moviesbattle.api.domain.repository.IMovieRepository;

@Service
public class MovieService {
	
	private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
	
	@Autowired
	private IMovieRepository movieRepository;
	
	@Autowired 
	private IFetchMovie fetchMovie;
	
	@EventListener(ApplicationReadyEvent.class)
	public void refreshMovies() {
		
	    try {
			Set<MovieItem> movieItems = fetchMovie.fetch();
			movieItems.stream()
				.forEach(item -> {
					Movie movie = Movie.builder()
							.id(item.getId())
							.title(item.getTitle())
							.rating(item.getRating())
							.numberVotes(item.getNumberVotes())
							.score(item.getScore())
							.build();
					movieRepository.save(movie);
				});
			logger.info("Movie refreshed. Movies fetched: {}", movieItems.size());
		} catch (FetchMovieException e) {
			logger.error("Failed to fetch movies: {}", e.getLocalizedMessage());
		}
	}
}
