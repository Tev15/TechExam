package com.techexam.props;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class EnvironmentProperties {
	private final Properties props;
	
	private EnvironmentProperties() {
		props = new Properties();
		
		try {
			props.load(this.getClass().getResourceAsStream("/environment.properties"));
		} catch (IOException ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void addProperty(String key, String value) {
		props.put(key, value);
	}
	
	public String getProperty(String key) {
		return props.getProperty(key);
	}
	
	public String getProperty(String key, Object... args) {
		return MessageFormat.format(props.getProperty(key), args);
	}
	
	private static EnvironmentProperties instance;
	
	static {
		if (instance == null) {
			instance = new EnvironmentProperties();
		}
	}
	
	public static EnvironmentProperties getInstance() {
		return instance;
	}
}
