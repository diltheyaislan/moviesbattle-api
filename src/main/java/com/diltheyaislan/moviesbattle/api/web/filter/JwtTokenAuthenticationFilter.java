package com.diltheyaislan.moviesbattle.api.web.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError;
import com.diltheyaislan.moviesbattle.api.security.UserDetailsServiceImpl;
import com.diltheyaislan.moviesbattle.api.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends GenericFilterBean {
    
    public static final String HEADER_PREFIX = "Bearer ";
    
    private final JwtTokenProvider jwtTokenProvider;
  
    private final UserDetailsServiceImpl userDetailsService;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        
    	try {
            String jwt = getJwtFromRequest((HttpServletRequest) request);
            
            System.out.println("SER " + userDetailsService);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                UUID userId = jwtTokenProvider.getUserIdFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {         
        	response.setContentType("application/json");
        	((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	response.getOutputStream().println(buildResponse(StatusException.UNAUTHORIZED, "Expired token"));
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            filterChain.doFilter(request, response);
        }
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
    
    private String buildResponse(StatusException status, String message) throws JsonProcessingException {
    	CommonError error = buildCommonError(status.getStatusCode(), message);
    	
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.findAndRegisterModules();
    	mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
   
    	return mapper.writeValueAsString(error);
    }
    
    private CommonError buildCommonError(Integer httpStatus, String message) {
		CommonError error = new CommonError();
		error.setStatus(httpStatus);
		error.setDateTime(LocalDateTime.now());
		error.setMessage(message);
		return error;
	}
}