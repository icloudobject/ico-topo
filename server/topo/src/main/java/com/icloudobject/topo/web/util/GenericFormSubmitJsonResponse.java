package com.icloudobject.topo.web.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * The Json response sent back to the client after processing a form submit.
 * 
 * @author cyrus
 * 
 */
public class GenericFormSubmitJsonResponse {

	@JsonProperty
	private boolean success;

	@JsonProperty
	private List<String> fieldErrors = Lists.newArrayList();

	@JsonProperty
	private String additionalInfo;

	GenericFormSubmitJsonResponse() {
		// protect the default constructor
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<String> getFieldErrors() {
		return fieldErrors;
	}

	public void addFieldError(String error) {
		this.fieldErrors.add(error);
	}

	public void addFieldErrors(List<String> errors) {
		this.fieldErrors.addAll(errors);
	}

	public void clearFieldErrors() {
		this.fieldErrors.clear();
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

}
