package com.techexam.webdriver;

import org.apache.commons.lang3.StringUtils;

/**
 * Custom unchecked exception for WebDriver automation errors.
 * 
 * author: steve.estoconing
 */
@SuppressWarnings("serial")
public class WebDriverRuntimeException extends RuntimeException {

	private String code;

	public WebDriverRuntimeException(Throwable cause) {
		super(cause);
	}

	public WebDriverRuntimeException(String code, Throwable cause) {
		super(code, cause);
		this.code = code;
	}

	/** String code with no cause (configuration / validation errors). */
	public WebDriverRuntimeException(String code) {
		super(code);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(",Code:").append(code);
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (getClass().equals(other.getClass())) {
			return StringUtils.equals(code, ((WebDriverRuntimeException) other).getCode());
		} else {
			return false;
		}
	}
}
