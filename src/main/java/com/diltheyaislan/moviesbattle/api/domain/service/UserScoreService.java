package com.diltheyaislan.moviesbattle.api.domain.service;

import java.util.List;

import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.UserScore;
import com.diltheyaislan.moviesbattle.api.domain.repository.IUserScoreRepository;

@Service
public class UserScoreService {
	
	@Autowired
	private IUserScoreRepository userScoreRepository;
	
	public UserScore incrementScore(User user, double score) {
		
		UserScore userScore = userScoreRepository
				.findOneByUser(user)
				.orElse(UserScore.builder().user(user).score(0D).build());
		
		double newScore = userScore.getScore() + score;
		
		userScore.setScore(Precision.round(newScore, 2));
		return userScoreRepository.save(userScore);
	}
	

	public List<UserScore> scoreboard() {
		
		return userScoreRepository.topScore();
	}
}
