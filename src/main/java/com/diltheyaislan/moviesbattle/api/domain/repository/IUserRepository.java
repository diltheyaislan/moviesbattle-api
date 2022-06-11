package com.diltheyaislan.moviesbattle.api.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.diltheyaislan.moviesbattle.api.domain.entity.User;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID>  {

	public Optional<User> findOneByUsername(String username);
}
