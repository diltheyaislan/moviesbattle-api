package com.diltheyaislan.moviesbattle.api.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Movie extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@Column
	private String title;
	
	@Column
	private Double rating;
	
	@Column
	private Integer numberVotes;
	
	@Column
	private Double score;
	
	@Column(updatable = false, nullable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
