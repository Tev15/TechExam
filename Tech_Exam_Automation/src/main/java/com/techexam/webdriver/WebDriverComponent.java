package com.techexam.webdriver;

import java.text.MessageFormat;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.techexam.webdriver.props.EnvironmentProperties;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Manages the WebDriver lifecycle.
 * 
 * Uses WebDriverManager so no manual chromedriver/geckodriver download is
 * needed.
 *
 * @author steve.estoconing
 */
public class WebDriverComponent {

	private static final Logger logger = LogManager.getLogger(WebDriverComponent.class);

	private static ThreadLocal<WebDriverComponent> driver = new ThreadLocal<>();

	private WebDriver webDriver;

	// private constructor — use createInstance()
	private WebDriverComponent(WebDriver webDriver) {
		this.webDriver = webDriver;
	}

	public static WebDriverComponent createInstance(String browserName) {

		EnvironmentProperties envProps = EnvironmentProperties.getInstance();

		int implicitWait = Integer.parseInt(envProps.getProperty("webdriver.implicit.wait"));
		int pageLoadTimeout = Integer.parseInt(envProps.getProperty("webdriver.page.load.timeout"));
		WebDriver browserDriver;

		try {
			switch (browserName.trim().toLowerCase()) {

			case "firefox": {
				WebDriverManager.firefoxdriver().setup();
				FirefoxOptions options = new FirefoxOptions();
				browserDriver = new FirefoxDriver(options);
				break;
			}

			case "chrome":
			default: {
				WebDriverManager.chromedriver().setup();
				ChromeOptions options = new ChromeOptions();
				// Uncomment for CI/CD headless:
				// options.addArguments("--headless=new", "--no-sandbox",
				// "--disable-dev-shm-usage", "--window-size=1920,1080");
				browserDriver = new ChromeDriver(options);
				break;
			}
			}
		} catch (Exception ex) {
			throw new WebDriverRuntimeException(
					MessageFormat.format(envProps.getProperty("error.webdriver.browser.initialize"), browserName));
		}

		// Apply timeouts
		browserDriver.manage().window().maximize();
		browserDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
		browserDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

		WebDriverComponent component = new WebDriverComponent(browserDriver);
		driver.set(component);

		logger.info("WebDriver initialized successfully.");
		return component;
	}

	/**
	 * Returns the WebDriverComponent for the current thread. Returns {@code null}
	 * if no driver has been created yet.
	 */
	public static WebDriverComponent getInstance() {
		return driver.get();
	}

	/** Returns the raw WebDriver. */
	public WebDriver getWebDriver() {
		return webDriver;
	}

	/** Returns an explicit wait using the default {@code explicitWaitSeconds}. */
	public WebDriverWait getWait() {
		return getWait(Integer.parseInt(EnvironmentProperties.getInstance().getProperty("webdriver.explicit.wait")));
	}

	/**
	 * Creates a pre-configured {@link WebDriverWait} for the current driver.
	 *
	 * @param timeoutSeconds custom wait timeout
	 */
	public WebDriverWait getWait(long timeoutSeconds) {
		return new WebDriverWait(webDriver, Duration.ofSeconds(timeoutSeconds));
	}

	/**
	 * Quits the WebDriver and removes the ThreadLocal entry. Mirrors reference
	 * {@code WebDriverComponent.getInstance().clear()}.
	 */
	public void clear() {
		if (webDriver != null) {
			try {
				logger.info("Closing WebDriver instance.");
				webDriver.quit();
			} catch (Exception ex) {
				logger.warn("WebDriver quit encountered an issue: {}", ex.getMessage());
			} finally {
				webDriver = null;
				driver.remove();
			}
		}
	}
}
