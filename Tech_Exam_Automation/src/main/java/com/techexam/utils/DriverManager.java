package com.techexam.utils;

import java.text.MessageFormat;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.techexam.props.EnvironmentProperties;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverManager {
	private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

	public static void initDriver(String browser) {
		WebDriver webDriver;

		switch (browser.toLowerCase()) {
		case "chrome":
			WebDriverManager.chromedriver().setup();
			webDriver = new ChromeDriver();
			break;
		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			webDriver = new FirefoxDriver();
			break;
		default:
			throw new IllegalArgumentException(MessageFormat.format(
					EnvironmentProperties.getInstance().getProperty("error.webdriver.browser.unsupported"), browser));
		}
		
		webDriver.manage().window().maximize();
		webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.set(webDriver);
	}

	public static void quit() {
		if (driver.get() != null) {
			driver.get().quit();
			driver.remove();
		}
	}
}
