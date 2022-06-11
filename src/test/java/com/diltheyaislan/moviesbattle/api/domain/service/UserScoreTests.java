package com.diltheyaislan.moviesbattle.api.domain.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.entity.UserScore;
import com.diltheyaislan.moviesbattle.api.domain.repository.IUserScoreRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserScoreTests {

	@Autowired
	private UserScoreService userScoreService;
	
	@MockBean
	private IUserScoreRepository userScoreRepository;

	private User user;
	private UserScore userScore;
	
	@BeforeEach
    public void init() {
		
		user = User.builder()
				.id(UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec"))
				.build();
		
		userScore = UserScore.builder()
				.user(user)
				.score(0D)
				.build();
	}
	
	@Test
	public void givenUserAndScore_whenIncrementScore_shouldSaveAndReturnsUserScoreUpdated() throws BusinessException {
	
		UserScore expectedUserScore = UserScore.builder().user(user).score(100D).build();
		
		when(userScoreRepository.findOneByUser((User) notNull())).thenReturn(Optional.of(userScore));
		when(userScoreRepository.save((UserScore) notNull())).thenReturn(expectedUserScore);
		
		UUID userId = UUID.fromString("d4f9c7e4-9643-4314-9b41-fe42f24a42ec");
		User user = User.builder().id(userId).build();
		double score = 100D;
		
		UserScore userScore = userScoreService.incrementScore(user, score);
		
		assertThat(userScore.getUser().getId().toString(), equalTo(expectedUserScore.getUser().getId().toString()));
		assertThat(userScore.getScore(), equalTo(expectedUserScore.getScore()));
	}
}