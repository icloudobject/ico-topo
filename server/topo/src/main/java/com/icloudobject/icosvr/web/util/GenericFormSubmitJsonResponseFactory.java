package com.icloudobject.icosvr.web.util;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Component
public class GenericFormSubmitJsonResponseFactory {

	@Autowired
	private MessageSource messageSource;

	public GenericFormSubmitJsonResponse create() {
		return new GenericFormSubmitJsonResponse();
	}

	public GenericFormSubmitJsonResponse create(final BindingResult bindingResult, final Locale locale) {
		GenericFormSubmitJsonResponse jsonResponse = new GenericFormSubmitJsonResponse();
		jsonResponse.setSuccess(true);
		if (bindingResult.hasErrors()) {
			jsonResponse.setSuccess(false);
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				String message = this.messageSource.getMessage(fieldError, locale);
				jsonResponse.addFieldError(fieldError.getField() + " - " + message);
			}
		}
		return jsonResponse;
	}

}
