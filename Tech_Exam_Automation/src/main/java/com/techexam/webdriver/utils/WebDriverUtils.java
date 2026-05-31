package com.techexam.webdriver.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.techexam.webdriver.WebDriverComponent;
import com.techexam.webdriver.WebDriverRuntimeException;

/**
 * Static utility methods for WebDriver operations.
 *
 * Closely mirrors the reference WebDriverUtils: - {@link #delay(long)} — thread
 * sleep (wraps InterruptedException) - {@link #takeScreenshot()} — full-page
 * PNG via Shutterbug - {@link #takeScreenshot(String, String, String)} — named
 * screenshot to disk - {@link #handleLogout()} — graceful logout helper
 *
 * @author exam-automation
 */
public final class WebDriverUtils {

	private static final Logger logger = LogManager.getLogger(WebDriverUtils.class);

	/** Common logout link locators to try in order. */
	private static final By[] LOGOUT_LOCATORS = { By.linkText("Logout"), By.linkText("Log Out"),
			By.linkText("Sign Out"), By.partialLinkText("Logout"), By.id("logout"), By.cssSelector("a[href*='logout']"),
			By.cssSelector("a[href*='signout']"),
			By.xpath("//a[contains(translate(text(),'LOGOUT','logout'),'logout')]") };

	// ----------------------------------------------------------------
	// Private constructor — utility class, no instantiation
	// ----------------------------------------------------------------
	private WebDriverUtils() {
	}

	// ================================================================
	// DELAY (mirrors reference delay(long))
	// ================================================================

	/**
	 * Pauses the current thread for {@code timeInSeconds} seconds. Wraps
	 * {@link InterruptedException} in {@link WebDriverRuntimeException} exactly as
	 * the reference implementation does.
	 *
	 * @param timeInSeconds seconds to sleep
	 */
	public static void delay(long timeInSeconds) {
		logger.debug("Delay: {}s", timeInSeconds);
		try {
			TimeUnit.SECONDS.sleep(timeInSeconds);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt(); // restore interrupt flag
			throw new WebDriverRuntimeException("Delay Interrupted", ex);
		}
	}

	/**
	 * Millisecond-precision variant for short waits between actions.
	 *
	 * @param timeInMillis milliseconds to sleep
	 */
	public static void delayMs(long timeInMillis) {
		logger.debug("Delay: {}ms", timeInMillis);
		try {
			TimeUnit.MILLISECONDS.sleep(timeInMillis);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new WebDriverRuntimeException("Delay Interrupted", ex);
		}
	}

	// ================================================================
	// SCREENSHOTS (mirrors reference takeScreenshot())
	// ================================================================

	/**
	 * Captures a full-page screenshot using Shutterbug and returns the {@link File}
	 * object. The file is written to {@code image.png} in the working directory
	 * (same behaviour as the reference).
	 *
	 * @return the screenshot file
	 */
	public static File takeScreenshot() {
		WebDriver webDriver = WebDriverComponent.getInstance().getWebDriver();

		try {
			BufferedImage image = Shutterbug.shootPage(webDriver, Capture.FULL_SCROLL).getImage();

			File outputFile = new File("image.png");
			ImageIO.write(image, "png", outputFile);
			return outputFile;

		} catch (IOException ex) {
			throw new WebDriverRuntimeException("Screenshot Failed", ex);
		}
	}

	/**
	 * Captures a full-page screenshot and saves it to a named file.
	 *
	 * <p>
	 * File name format:
	 * <ul>
	 * <li>If {@code testCaseAction} is blank: {@code <dirPath>/<testCaseNo>.png}
	 * <li>Otherwise: {@code <dirPath>/<testCaseNo>-<testCaseAction>.png}
	 * </ul>
	 *
	 * Mirrors the reference exactly, including relative-path resolution.
	 *
	 * @param dirPath        destination directory (absolute or relative to cwd)
	 * @param testCaseNo     test case identifier (e.g. "TC001")
	 * @param testCaseAction optional action label (e.g. "LOGIN", "SUBMIT")
	 */
	public static void takeScreenshot(String dirPath, String testCaseNo, String testCaseAction) {
		try {
			File screenshotFile = takeScreenshot();

			if (StringUtils.isEmpty(dirPath)) {
				dirPath = System.getProperty("user.dir");
			} else if (!(dirPath.contains(":") || dirPath.startsWith("/"))) {
				// Relative path — resolve against cwd
				File base = new File(System.getProperty("user.dir"));
				File relative = new File(base, dirPath);
				dirPath = relative.getCanonicalPath();
			}

			// Build destination file name
			String destPath = StringUtils.isEmpty(testCaseAction)
					? MessageFormat.format("{0}/{1}.png", dirPath, testCaseNo)
					: MessageFormat.format("{0}/{1}-{2}.png", dirPath, testCaseNo, testCaseAction);

			FileUtils.copyFile(screenshotFile, new File(destPath.replace("/", File.separator)));

			logger.info("Screenshot saved: {}", destPath);

		} catch (IOException ex) {
			throw new WebDriverRuntimeException("Unable to Save Screenshot", ex);
		}
	}

	/**
	 * Convenience overload that auto-generates a timestamp-based file name. Useful
	 * for failure screenshots in {@code @AfterMethod}.
	 *
	 * @param dirPath    destination directory
	 * @param testCaseNo test case identifier
	 */
	public static void takeScreenshot(String dirPath, String testCaseNo) {
		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		takeScreenshot(dirPath, testCaseNo, timestamp);
	}

	// ================================================================
	// LOGOUT (mirrors reference handleLogout())
	// ================================================================

	/**
	 * Attempts to log out of the application using common locators. Safe to call
	 * even if no user is logged in — logs a warning and returns.
	 *
	 * Mirrors reference {@code handleLogout()} / {@code handleLogoutMain()}.
	 */
	public static void handleLogout() {
		WebDriverComponent component = WebDriverComponent.getInstance();
		if (component == null) {
			logger.warn("handleLogout() called but no WebDriver instance exists.");
			return;
		}

		WebDriver webDriver = component.getWebDriver();
		logger.info("Attempting logout.");

		for (By locator : LOGOUT_LOCATORS) {
			try {
				WebElement logoutLink = webDriver.findElement(locator);
				if (logoutLink.isDisplayed()) {
					logoutLink.click();
					logger.info("Logout link clicked via locator: {}", locator);
					return;
				}
			} catch (NoSuchElementException ignored) {
				// try next locator
			}
		}

		logger.warn("Logout element not found with any known locator.");
	}

	/**
	 * Full logout + driver teardown. Mirrors reference pattern: click logout →
	 * clear WebDriverComponent.
	 */
	public static void handleLogoutAndQuit() {
		handleLogout();
		WebDriverComponent component = WebDriverComponent.getInstance();
		if (component != null) {
			component.clear();
		}
	}
}
