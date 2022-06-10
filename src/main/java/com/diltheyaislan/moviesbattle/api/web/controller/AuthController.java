package com.diltheyaislan.moviesbattle.api.web.controller;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.TechnicalException;
import com.diltheyaislan.moviesbattle.api.domain.dto.SignUpDTO;
import com.diltheyaislan.moviesbattle.api.domain.dto.UserAccessTokenDTO;
import com.diltheyaislan.moviesbattle.api.domain.entity.User;
import com.diltheyaislan.moviesbattle.api.domain.service.AuthService;
import com.diltheyaislan.moviesbattle.api.web.request.SignInBodyRequest;
import com.diltheyaislan.moviesbattle.api.web.request.SignUpBodyRequest;
import com.diltheyaislan.moviesbattle.api.web.response.UserAccessTokenResponse;
import com.diltheyaislan.moviesbattle.api.web.response.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "${api.service.auth.name}", description = "${api.service.auth.description}")
public class AuthController extends BaseController {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private AuthService authService;
	
	@Operation(description = "${api.service.auth.signup.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "${api.response.201.description}"),
			@ApiResponse(responseCode = "400", description = "${api.response.400.validation.description}", content = @Content)})
	@PostMapping("/signup")
	public ResponseEntity<UserResponse> signup(
			@Valid @RequestBody SignUpBodyRequest request) throws BusinessException, TechnicalException {

		SignUpDTO dto = SignUpDTO.builder()
				.name(request.getName())
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.build();
		
		User user = authService.signUp(dto);
		return buildResponse(user, UserResponse.class, HttpStatus.CREATED);
	}
	
	@Operation(description = "${api.service.auth.signin.description}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "${api.response.200.description}"),
			@ApiResponse(responseCode = "400", description = "${api.response.400.validation.description}", content = @Content),
			@ApiResponse(responseCode = "401", description = "${api.response.401.description}", content = @Content)})
	@PostMapping("/signin")
	public ResponseEntity<UserAccessTokenResponse> signin(
			@Valid @RequestBody SignInBodyRequest request) throws BusinessException, TechnicalException {
		
		UserAccessTokenDTO userAccessToken 
			= authService.signIn(request.getUsername(), request.getPassword());
        
		UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(userAccessToken.getUser(), userResponse);
        
        return ResponseEntity.ok(new UserAccessTokenResponse(
        		userAccessToken.getAccessToken(),
        		userResponse));
	}
}
