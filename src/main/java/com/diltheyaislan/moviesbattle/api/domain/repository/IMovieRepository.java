package com.diltheyaislan.moviesbattle.api.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.diltheyaislan.moviesbattle.api.domain.entity.Movie;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, UUID>  {

	@Query(value = "SELECT * FROM MOVIE ORDER BY RAND() LIMIT 2", nativeQuery = true)
	public List<Movie> rafflePair(); 
}
