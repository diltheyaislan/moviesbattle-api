package com.diltheyaislan.moviesbattle.api.web.request;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SignUpBodyRequest implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotBlank
	@Size(min = 1, max = 100)
	private String name;
	
	@NotBlank
	@Size(min = 1, max = 40)
	private String username;
	
	@NotBlank
	private String password;
}
