package com.icloudobject.topo.core;

/**
 * Some operations at service and/or DAO level can be "illegal". For example, an delete operation performed against a
 * CloudConfig that doesn't belong to the target Tenant.
 * 
 * @author cyrus
 *
 */
public class IllegalOperationException extends Exception {

	private static final long serialVersionUID = 4649286400046703977L;

	public IllegalOperationException() {
		super();
	}

	public IllegalOperationException(String message) {
		super(message);
	}

	public IllegalOperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalOperationException(Throwable cause) {
		super(cause);
	}

}
