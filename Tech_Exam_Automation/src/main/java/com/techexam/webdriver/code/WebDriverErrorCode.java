package com.techexam.webdriver.code;

import com.techexam.webdriver.Code;

public enum WebDriverErrorCode implements Code {
	
	ERROR_HTTP_INTERNAL_SERVER	("500"),
	;
	
	private String code;
	
	private WebDriverErrorCode(String code) {
		this.code = code;
	}
	
	@Override
	public String getCode() {
		return this.code;
	}
}
