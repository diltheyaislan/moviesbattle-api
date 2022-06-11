package com.diltheyaislan.moviesbattle.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.diltheyaislan.moviesbattle.api.security.UserDetailsServiceImpl;
import com.diltheyaislan.moviesbattle.api.web.filter.JwtTokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfiguration {
	
	@Bean
    public JwtTokenAuthenticationFilter tokenAuthenticationFilter() {
        return new JwtTokenAuthenticationFilter();
    }
	
	@Bean
	public SecurityFilterChain springWebFilterChain(HttpSecurity http) throws Exception {
        http
	        .httpBasic(AbstractHttpConfigurer::disable)
	        .csrf(AbstractHttpConfigurer::disable)
	        .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .exceptionHandling(c-> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
	        .authorizeRequests(c -> c
	        	.antMatchers("/auth/**")
	        		.permitAll()
	        	.antMatchers("/h2-console/**")
	        		.permitAll()
	            .anyRequest()
	            	.authenticated()
	        )
	        .addFilterBefore(
	        		tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers().frameOptions().disable();
        return http.build();
    }

	
	@Bean
    AuthenticationManager customAuthenticationManager(UserDetailsServiceImpl userDetailsService) {
        return authentication -> {
            String username = authentication.getPrincipal() + "";
            String password = authentication.getCredentials() + "";
             
            try {
            	UserDetails user = userDetailsService.loadUserByUsername(username); 
            	
            	if (!passwordEncoder().matches(password, user.getPassword())) {
            		throw new BadCredentialsException("Bad credentials");
            	}            	

            	return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            } catch (UsernameNotFoundException ex) {
            	throw new BadCredentialsException("Bad credentials");
            }
        };
    }
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
