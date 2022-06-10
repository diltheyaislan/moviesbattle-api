package com.diltheyaislan.moviesbattle.api.security.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.diltheyaislan.moviesbattle.api.security.UserPrincipal;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
	
    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";
    
    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds = 3600000;
    
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    public String createToken(UUID id) {
       
    	Claims claims = Jwts.claims().setSubject(id.toString());
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(encodeSecretKey(secretKey))
            .compact();
    }
    
	public String createToken(Authentication authentication) {

		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
		
		return Jwts.builder()
				.setSubject(userPrincipal.getId().toString())
				.setIssuedAt(new Date())
				.setExpiration(validity)
				.signWith(encodeSecretKey(secretKey))
				.compact();
	}
	
	public UUID getUserIdFromToken(String token) {
		
		try {
			Claims claims = Jwts
					.parserBuilder()
					.setSigningKey(encodeSecretKey(secretKey))
					.build()
					.parseClaimsJws(token)
					.getBody();
			
			return UUID.fromString(claims.getSubject());
		} catch (ExpiredJwtException ex) {
			return UUID.fromString(ex.getClaims().getSubject());
		}
	}
    
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
    
    public boolean validateToken(String token) {
        try {
			Jwts.parserBuilder()
				.setSigningKey(encodeSecretKey(secretKey))
				.build()
				.parseClaimsJws(token);

			return true;
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
			throw ex;
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		return false;
    }
    
    private SecretKey encodeSecretKey(String secret) {		
		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		return key;	
	}
}