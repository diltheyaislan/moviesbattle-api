package com.diltheyaislan.moviesbattle.api.domain.fetchmovie.provider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.diltheyaislan.moviesbattle.api.domain.fetchmovie.IFetchMovie;
import com.diltheyaislan.moviesbattle.api.domain.fetchmovie.MovieItem;
import com.diltheyaislan.moviesbattle.api.domain.fetchmovie.exception.FetchMovieException;

@Component
public class IMDbScrapingSearchMovie implements IFetchMovie {

	private static final Logger logger = LoggerFactory.getLogger(IMDbScrapingSearchMovie.class);
	
	@Value("${app.provider.movies.imbdTopMoviesUrl}")
	private String imbdTopMoviesUrl = "http://www.imdb.com/chart/top";
	 
	@Override
	public Set<MovieItem> fetch() throws FetchMovieException {
		
		try {
			
			Set<MovieItem> movies = new HashSet<>();
			
			Document document = Jsoup.connect(imbdTopMoviesUrl).get();
			for (Element row : document.select("table.chart.full-width tr")) {
				
				String href = row.select(".titleColumn a").attr("href");
				String title = row.select(".titleColumn a").text();
				String ratingText = row.select(".posterColumn").select("span[name=ir]").attr("data-value");
				String numberVotesText = row.select(".posterColumn").select("span[name=nv]").attr("data-value");
				
				String id = extractIdFromUrl(href);
				
				if (StringUtils.hasText(id)) {
					
					BigDecimal rating = new BigDecimal(ratingText);
					Integer numberVotes = Integer.valueOf(numberVotesText);
					BigDecimal score = BigDecimal.valueOf(numberVotes).multiply(rating);
					
					MovieItem movie = MovieItem.builder()
							.id(id)
							.title(title)
							.rating(rating.setScale(2, RoundingMode.HALF_UP).doubleValue())
							.numberVotes(numberVotes)
							.score(score.setScale(2, RoundingMode.HALF_UP).doubleValue())
							.build();
					
					movies.add(movie);
				}				
			}
			
			return movies;
		} catch (IOException ex) {
			logger.error("Failed to search movies via web scraping: {}", ex.getMessage());
			throw new FetchMovieException();
		}
	}

	private String extractIdFromUrl(String url) {
		String[] paths = url.split("/");
		return paths.length > 1 ? paths[2] : paths[0];
	}
}
