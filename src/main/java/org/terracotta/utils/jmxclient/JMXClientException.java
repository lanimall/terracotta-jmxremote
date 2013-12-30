package org.terracotta.utils.jmxclient;

public class JMXClientException extends Exception {

	public JMXClientException() {
		super();
	}

	public JMXClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public JMXClientException(String message) {
		super(message);
	}

	public JMXClientException(Throwable cause) {
		super(cause);
	}
}
