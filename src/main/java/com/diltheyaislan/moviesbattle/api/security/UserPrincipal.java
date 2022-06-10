package com.diltheyaislan.moviesbattle.api.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;

import java.util.Collection;
import java.util.UUID;

@Builder
public class UserPrincipal implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private UUID id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(UUID id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
