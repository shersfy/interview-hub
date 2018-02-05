package org.interview.exception;

public class NetException extends Exception {

	/**
	 * 网络异常
	 */
	private static final long serialVersionUID = 1L;

	public NetException() {
		super();
	}

	public NetException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NetException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetException(String message) {
		super(message);
	}

	public NetException(Throwable cause) {
		super(cause);
	}

}
