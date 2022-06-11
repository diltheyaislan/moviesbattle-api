package com.diltheyaislan.moviesbattle.api.domain.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

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
@EqualsAndHashCode(callSuper=false, exclude = {"user"})
public class UserScore extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "user_id")
	@Type(type = "uuid-char")
	private UUID userId;
	
	@Column(precision = 11, scale = 2)
	private Double score;
	
	@OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
