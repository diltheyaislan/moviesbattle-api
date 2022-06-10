package com.diltheyaislan.moviesbattle.api.security;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.diltheyaislan.moviesbattle.api.core.exception.ResourceNotFoundException;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
            	
    	User user = userRepository.findOneByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("user: " + username)
        );

        return buildUserPrincipl(user);
    }

    @Transactional
    public UserDetails loadUserById(UUID id) 
    		throws ResourceNotFoundException {
    	
    	User user = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );

        return buildUserPrincipl(user);
    }
    
    private UserPrincipal buildUserPrincipl(User user) {
    	return UserPrincipal.builder()
    			.id(user.getId())
    			.username(user.getUsername())
    			.password(user.getPassword())
    			.build();
    }
}