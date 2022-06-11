package com.diltheyaislan.moviesbattle.api.domain.fetchmovie;

import java.io.Serializable;

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
public class MovieItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String title;
	private Double rating;
	private Integer numberVotes;
	private Double score;
}
