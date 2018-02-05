package org.interview.exception;

public class TooManyConnectionException extends Exception {

	/**
	 * 连接过多异常
	 */
	private static final long serialVersionUID = 1L;

	public TooManyConnectionException() {
		super();
	}

	public TooManyConnectionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TooManyConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public TooManyConnectionException(String message) {
		super(message);
	}

	public TooManyConnectionException(Throwable cause) {
		super(cause);
	}

}
