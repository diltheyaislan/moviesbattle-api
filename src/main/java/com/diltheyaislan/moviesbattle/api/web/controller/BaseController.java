package com.diltheyaislan.moviesbattle.api.web.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.diltheyaislan.moviesbattle.api.core.exception.StatusException;
import com.diltheyaislan.moviesbattle.api.core.exception.TechnicalException;
import com.diltheyaislan.moviesbattle.api.domain.entity.BaseEntity;
import com.diltheyaislan.moviesbattle.api.web.response.BaseResponse;

public abstract class BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	public <E extends BaseEntity, R extends BaseResponse> ResponseEntity<R> buildResponse(
			E entity, Class<R> responseClass, HttpStatus status) throws TechnicalException {

		R response = newResponseInstance(responseClass);
		BeanUtils.copyProperties(entity, response);
		return buildResponse(response, status);
	}

	public <E extends BaseEntity, R extends BaseResponse> ResponseEntity<R> buildResponse(
			E entity, Class<R> responseClass, MultiValueMap<String, String> headers, HttpStatus status) 
					throws TechnicalException {
		
		R response = newResponseInstance(responseClass);
		BeanUtils.copyProperties(entity, response);
		return buildResponse(response, headers, status);
	}

	public <R extends BaseResponse> ResponseEntity<R> buildResponse(R data, HttpStatus status) {
		return buildResponse(data, (MultiValueMap<String, String>) null, status);
	}
	
	public <R extends BaseResponse> ResponseEntity<R> buildResponse(
			R data, MultiValueMap<String, String> headers, HttpStatus status) {
		
		return new ResponseEntity<R>(data, headers, status);
	}
	
	public <E extends BaseEntity, R extends BaseResponse> ResponseEntity<R> responseOK(
			E entity, Class<R> responseClass) throws TechnicalException {
		
		R response = newResponseInstance(responseClass);
		BeanUtils.copyProperties(entity, response);
		return responseOk(response);
	}
	
	public <R extends BaseResponse> ResponseEntity<R> responseOk(R data) {
		return ResponseEntity.ok(data);
	}
	
	public <E extends BaseEntity, R extends BaseResponse> ResponseEntity<List<R>> buildResponse(
			List<E> entities, Class<R> responseClass, HttpStatus status) throws TechnicalException {
	
		List<R> response = entityListToResponseList(entities, responseClass);
		return buildResponse(response, status);
	}
	
	public <E extends BaseEntity, R extends BaseResponse> ResponseEntity<List<R>> buildResponse(
			List<E> entities, Class<R> responseClass, MultiValueMap<String, String> headers, HttpStatus status)
				throws TechnicalException {
		
		List<R> response = entityListToResponseList(entities, responseClass);
		return buildResponse(response, headers, status);
	}
	
	public <R extends BaseResponse> ResponseEntity<List<R>> buildResponse(List<R> data, HttpStatus status) {
		return buildResponse(data, (MultiValueMap<String, String>) null, status);
	}
	
	public <R extends BaseResponse> ResponseEntity<List<R>> buildResponse(
			List<R> data, MultiValueMap<String, String> headers, HttpStatus status) {
		
		return new ResponseEntity<List<R>>(data, headers, status);
	}

	public <E extends BaseEntity, R extends BaseResponse> ResponseEntity<List<R>> responseOK(
			List<E> entities, Class<R> responseClass) throws TechnicalException {
		
		List<R> response = entityListToResponseList(entities, responseClass);
		return responseOk(response);
	}
	
	
	public <R extends BaseResponse> ResponseEntity<List<R>> responseOk(List<R> data) {
		return ResponseEntity.ok(data);
	}
	
	public ResponseEntity<?> responseNoContent() {
		return ResponseEntity.noContent().build(); 
	}
	
	public ResponseEntity<?> responseNotFound() {
		return ResponseEntity.notFound().build(); 
	}
	
	
	private <R extends BaseResponse> R newResponseInstance(Class<R> responseClass) throws TechnicalException {
		try {
			return responseClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException ex) {
			logger.error("Failed to create new persistence entity instance: {}", ex.getMessage());
			throw new TechnicalException(StatusException.INTERNAL_SERVER_ERROR, "message.error.failedToCreateNewInstance", responseClass);
		}
	}
	
	private <E extends BaseEntity, R extends BaseResponse> List<R> 
		entityListToResponseList(List<E> entities, Class<R> responseClass) throws TechnicalException {
		
		List<R> responseList = new ArrayList<>();
		for (E entity : entities) {
			R response = newResponseInstance(responseClass);
			BeanUtils.copyProperties(entity, response);
		}
		return responseList;
	}
}