package com.diltheyaislan.moviesbattle.api.core.locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocaleMessageSource {

	@Autowired
	private MessageSource messageSource;
	
	public String getMessage(String code) {
		return getMessage(code, (Object[]) null);
	}
	
	public String getMessage(String code, Object... args) { 
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
}
