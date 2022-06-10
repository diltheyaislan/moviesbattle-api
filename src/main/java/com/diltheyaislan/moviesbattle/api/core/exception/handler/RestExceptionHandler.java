package com.diltheyaislan.moviesbattle.api.core.exception.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.diltheyaislan.moviesbattle.api.core.exception.BusinessException;
import com.diltheyaislan.moviesbattle.api.core.exception.handler.CommonError.Argument;
import com.diltheyaislan.moviesbattle.api.core.locale.LocaleMessageSource;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);
	
	private LocaleMessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<CommonError.Argument> fields = ex.getBindingResult().getAllErrors().stream().map(error -> {
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			return new CommonError.Argument(fieldName, message);
		}).collect(Collectors.toList());
		
		CommonError error 
			= buildCommonError(
					status.value(), 
					messageSource.getMessage("message.error.oneOrMoreFieldWithInvalidData"), 
					fields);

		logger.warn("Handled MethodArgumentNotValidException");
		return handleExceptionInternal(ex, buildResponse(error), headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
	    String message = "message.error.missingRequestParam";
	    String parameter = ex.getParameterName();
	    
		CommonError error = buildCommonError(status.value(), message, new Object[] { parameter }, null);
		
		logger.warn("Handled MissingServletRequestParameterException");
		return handleExceptionInternal(ex, buildResponse(error), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleBusinessException(BusinessException ex, WebRequest request) {
		
		HttpStatus status = ex.getStatus() != null ? HttpStatus.valueOf(ex.getStatus().getStatusCode()) : HttpStatus.BAD_REQUEST;	
		List<CommonError.Argument> args = ex.getArguments() != null && ex.getArguments().length > 0
				? Stream.of(ex.getArguments()).map(arg -> arg).collect(Collectors.toList())
				: null;
		
		CommonError error = buildCommonError(status.value(), ex.getMessage(), ex.getMessageArguments(), args);
		
		logger.warn("Handled BusinessException");
		return handleExceptionInternal(ex, buildResponse(error), new HttpHeaders(), status, request);
	}
	
	
	private CommonError buildCommonError(Integer httpStatus, String message, List<Argument> errorArgs) {
		return buildCommonError(httpStatus, message, null, errorArgs);
	}
	
	private CommonError buildCommonError(
			Integer httpStatus, 
			String message, 
			Object[] messageArgs, 
			List<Argument> errorArgs) {

			message = getInterpolatedMessage(message, messageArgs);
		
		if (errorArgs != null) {
			errorArgs = errorArgs.stream().map(arg -> {
				arg.setMessage(getInterpolatedMessage(arg.getMessage()));
				return arg;
			}).collect(Collectors.toList());
		}
		
		return CommonError.builder()
			.status(httpStatus)
			.dateTime(LocalDateTime.now())
			.message(message)
			.args(errorArgs)
			.build();
	}

	private ResponseCommonError buildResponse(CommonError error) {
		return new ResponseCommonError(error);
	}
	
	private String getInterpolatedMessage(String message, Object... messageArgs) {
		try {
			return messageSource.getMessage(message, messageArgs);
		} catch(NoSuchMessageException e) {
			return message;
		}
	}
}
