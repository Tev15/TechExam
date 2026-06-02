package com.techexam.webdriver.listeners;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.techexam.webdriver.WebDriverComponent;
import com.techexam.webdriver.report.ExtentReportManager;
import com.techexam.webdriver.utils.WebDriverUtils;

/**
 * @author steve.estoconing
 */
public class TestListener implements ITestListener {

	private static final Logger logger = LogManager.getLogger(TestListener.class);

	private static final String SCREENSHOT_DIR = "screenshots";

	// ----------------------------------------------------------------
	// Suite level
	// ----------------------------------------------------------------

	@Override
	public void onStart(ITestContext context) {
		logger.info("====================================================");
		logger.info("Test Suite Started: {}", context.getName());
		logger.info("====================================================");
		ExtentReportManager.init();
	}

	@Override
	public void onFinish(ITestContext context) {
		logger.info("====================================================");
		logger.info("Test Suite Finished: {}", context.getName());
		logger.info("  Passed  : {}", context.getPassedTests().size());
		logger.info("  Failed  : {}", context.getFailedTests().size());
		logger.info("  Skipped : {}", context.getSkippedTests().size());
		logger.info("====================================================");
		ExtentReportManager.flush();
	}

	// ----------------------------------------------------------------
	// Test level
	// ----------------------------------------------------------------

	@Override
	public void onTestStart(ITestResult result) {
		String testName = formatTestName(result);
		logger.info("---------------------------------------------------");
		logger.info("Test Start: {}", testName);

		// Get description from @Test annotation if available
		String description = result.getMethod().getDescription();
		ExtentReportManager.createTest(testName, description);
		ExtentReportManager.getTest().info("Test START");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		String testName = formatTestName(result);
		logger.info("Test Result: PASS — {}", testName);

		ExtentTest test = ExtentReportManager.getTest();
		if (test != null) {
			test.log(Status.PASS, "Test PASSED");
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testName = formatTestName(result);
		Throwable cause = result.getThrowable();

		logger.error("Test Result: FAIL — {}", testName);
		if (cause != null) {
			logger.error("Cause: {}", ExceptionUtils.getStackTrace(cause));
		}

		ExtentTest test = ExtentReportManager.getTest();
		if (test != null) {
			// Log exception details
			if (cause != null) {
				test.log(Status.FAIL, "Exception: " + cause.getMessage());
				test.log(Status.FAIL, "<pre>" + ExceptionUtils.getStackTrace(cause) + "</pre>");
			} else {
				test.log(Status.FAIL, "Test FAILED (no exception details)");
			}

			// Capture and embed screenshot
			captureAndEmbedScreenshot(test, testName);
		}

		// Also save screenshot to disk for the ZIP archive
		saveScreenshotToDisk(testName);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String testName = formatTestName(result);
		logger.warn("Test Result: SKIP — {}", testName);

		ExtentTest test = ExtentReportManager.getTest();
		if (test != null) {
			test.log(Status.SKIP, "Test SKIPPED");
			if (result.getThrowable() != null) {
				test.log(Status.SKIP, result.getThrowable().getMessage());
			}
		}
	}

	// ----------------------------------------------------------------
	// Internal helpers
	// ----------------------------------------------------------------

	private String formatTestName(ITestResult result) {
		return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
	}

	/**
	 * Captures a Base64 screenshot and embeds it inline into the Extent report.
	 * This is the primary failure evidence mechanism.
	 */
	private void captureAndEmbedScreenshot(ExtentTest test, String testName) {
		try {
			WebDriverComponent component = WebDriverComponent.getInstance();
			if (component == null || component.getWebDriver() == null) {
				test.log(Status.WARNING, "Could not capture screenshot — WebDriver is null.");
				return;
			}
			String base64 = ((TakesScreenshot) component.getWebDriver()).getScreenshotAs(OutputType.BASE64);
			test.fail("Screenshot at failure:", MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
		} catch (Exception ex) {
			logger.warn("Failed to embed screenshot in report: {}", ex.getMessage());
		}
	}

	/**
	 * Uses WebDriverUtils.takeScreenshot to save a PNG named
	 * {@code <testName>-<timestamp>.png} into the screenshots directory. Mirrors
	 * the reference screenshot naming convention.
	 */
	private void saveScreenshotToDisk(String testName) {
		try {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			WebDriverUtils.takeScreenshot(SCREENSHOT_DIR, testName, "FAIL_" + timestamp);
		} catch (Exception ex) {
			logger.warn("Failed to save screenshot to disk: {}", ex.getMessage());
		}
	}
}
