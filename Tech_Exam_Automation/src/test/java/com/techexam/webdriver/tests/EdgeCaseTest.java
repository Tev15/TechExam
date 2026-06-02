package com.techexam.webdriver.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.techexam.webdriver.base.BaseTest;
import com.techexam.webdriver.page.HomePage;
import com.techexam.webdriver.page.LoginPage;
import com.techexam.webdriver.report.ExtentReportManager;

/**
 * EC001 – EC008: Edge case tests.
 *
 * @author steve.estoconing
 */
public class EdgeCaseTest extends BaseTest {

	/**
	 * EC001 — Whitespace-only username.
	 */
	@Test(
			groups = { "edge","input-boundary" },
			priority = 31,
			description = "EC001: Whitespace-only username — expects 500 or error, NOT successful login."
			)
	public void EC001_whitespaceOnlyUsername() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);
		LoginPage loginPage = new LoginPage(driver);

		logger.info("EC001: Submitting whitespace-only username.");
		ExtentReportManager.getTest().info("Step 2: Enter Username with whitespaces only.");
		loginPage.login("   ", password);
		screenshot("EC001", "WHITESPACE_USERNAME");

		logServerErrorOrLoginState("EC001", "BUG CONFIRMED: Whitespace username also triggers HTTP 500.");

		assertUserNotLoggedIn("EC001: Whitespace username must never allow login.");
	}

	/**
	 * EC002 — Whitespace-only password.
	 */
	@Test(
			groups = { "edge", "input-boundary" },
			priority = 32,
			description = "EC002: Whitespace-only password"
			)
	public void EC002_whitespaceOnlyPassword() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);
		LoginPage loginPage = new LoginPage(driver);

		logger.info("EC002: Submitting whitespace-only password.");
		ExtentReportManager.getTest().info("Step 2: Enter Password with whitespaces only.");
		loginPage.login(userName, "     ");
		screenshot("EC002", "WHITESPACE_PASSWORD");

		logServerErrorOrLoginState("EC002", "Whitespace password: check if server throws 500 or handles gracefully.");

		assertUserNotLoggedIn("EC002: Whitespace password must never allow login.");
	}

	/**
	 * EC003 — Case sensitivity of username.
	 */
	@Test(
			groups = { "edge", "input-boundary" },
			priority = 33,
			description = "EC003: Uppercase username."
			)
	public void EC003_caseSensitivityUsername() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);
		LoginPage loginPage = new LoginPage(driver);
		String upperUsername = userName.toUpperCase();

		logger.info("EC003: Attempting login with uppercase username: {}", upperUsername);
		ExtentReportManager.getTest().info("Step 2: Enter Username with Uppercase characters only.");
		loginPage.login(upperUsername, password);
		screenshot("EC003", "UPPERCASE_USERNAME");

		HomePage homePage = new HomePage(driver);
		if (homePage.isHomePageDisplayed()) {
			logger.warn("EC003: Successfully logged in as SAKAMOTO. Authentication is CASE-INSENSITIVE.");
			ExtentReportManager.getTest().warning("EC003 RESULT: Usernames are case-insensitive.");
		} else {
			logger.info("EC003: SAKAMOTO was rejected. Authentication is CASE-SENSITIVE");
			ExtentReportManager.getTest().log(Status.INFO, "EC003 RESULT: Usernames are case-sensitive.");
		}

		Assert.assertFalse(driver.getPageSource().isBlank(), "EC003 FAIL: Page crashed.");
	}

	/**
	 * EC004 — Oversized username (500 characters).
	 */
	@Test(
			groups = { "edge", "input-boundary" },
			priority = 34,
			description = "EC004: 500-character username."
			)
	public void EC004_maxLengthUsername() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);
		LoginPage loginPage = new LoginPage(driver);
		String longUsername = "adminsteve".repeat(50);

		logger.info("EC004: Submitting 500-char username.");
		ExtentReportManager.getTest().info("Step 2: Enter 500-char Username.");
		loginPage.login(longUsername, password);
		screenshot("EC004", "LONG_USERNAME");

		logServerErrorOrLoginState("EC004", "500-char username: check for crash or truncation.");
		assertUserNotLoggedIn("EC004: Oversized username must not allow login.");
	}

	/**
	 * EC005 — Oversized password (1000 characters).
	 */
	@Test(
			groups = { "edge", "input-boundary" },
			priority = 35,
			description = "EC005: 1000-character password."
			)
	public void EC005_maxLengthPassword() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);
		LoginPage loginPage = new LoginPage(driver);
		String longPassword = "1234passWord".repeat(84);

		logger.info("EC005: Submitting 1000-char password.");
		ExtentReportManager.getTest().info("Step 2: Enter 1000-char Password.");
		loginPage.login(userName, longPassword);
		// add explicit wait just in case
		screenshot("EC005", "VERYLONG_USERNAME");

		logServerErrorOrLoginState("EC005", "1000-char password: check for crash or timeout.");
		assertUserNotLoggedIn("EC005: Oversized password must not allow login.");
	}
	
	/**
	 * EC006 — Leading and trailing spaces around valid username.
	 */
	@Test(
			groups = { "edge","input-boundary" },
			priority = 36,
			description = "EC006: Username with leading/trailing spaces."
			)
	public void EC006_leadingTrailingSpacesInUsername() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);
		LoginPage loginPage = new LoginPage(driver);
		String paddedUsername = "  " + userName + "  ";

		logger.info("EC006: Login with padded username: '{}'", paddedUsername);
		ExtentReportManager.getTest().info("Step 2: Login with padded username: " + paddedUsername);
		loginPage.login(paddedUsername, password);
		screenshot("EC006", "LEADING_TRAILING_SPACES");

		HomePage homePage = new HomePage(driver);
		boolean loggedIn = homePage.isHomePageDisplayed();
		boolean errorShown = loginPage.isErrorMessageDisplayed();

		if (loggedIn) {
			logger.warn("EC006: Server TRIMS username. Document this behaviour.");
			ExtentReportManager.getTest()
					.warning("EC006 NOTE: Server trims whitespace. Login with padded username succeeded.");
		} else if (errorShown) {
			logger.info("EC006: Server is case-strict. Padded username was rejected.");
			ExtentReportManager.getTest().log(Status.INFO,
					"EC006 INFO: Padded username rejected. Server does not trim input.");
		} else {
			logServerErrorOrLoginState("EC006", "Unexpected outcome for padded username.");
		}

		// Either outcome is acceptable — what matters is the app does not crash
		Assert.assertFalse(driver.getPageSource().isBlank(),
				"EC006 FAIL: Page source is blank — application may have crashed.");
	}

	/**
	 * EC007 — Leading and trailing values around valid username.
	 */
	@Test(
			groups = { "edge","input-boundary" },
			priority = 37,
			description = "EC007: Username with leading/trailing values."
			)
	public void EC007_leadingTrailingValuesInUsername() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);
		String[] paddedUsername = { "1234sakamoto5678", "adminsakamoto", "sakamoto12345" };

		for (String input : paddedUsername) {
			driver.get(baseUrl);
			LoginPage loginPage = new LoginPage(driver);

			logger.info("EC007: Testing inputs with valid Username: {}", input);
			ExtentReportManager.getTest().info("Step: Testing inputs with valid Username: " + input);
			loginPage.login(input, password);
			screenshot("EC007", "USERNAME_WITH_" + input.toUpperCase());
			HomePage homePage = new HomePage(driver);
			boolean loggedIn = homePage.isHomePageDisplayed();
			boolean errorShown = loginPage.isErrorMessageDisplayed();

			if (loggedIn) {
				logger.error(
						"EC007: Login with padded username succeeded. Server acknowledges if it contains valid username. Document this behaviour.");
				ExtentReportManager.getTest().log(Status.FAIL,
						"EC007 FAIL: Login with padded username succeeded.");
				ExtentReportManager.getTest().warning(
						"EC007 WARNING: Server acknowledges if it contains valid username.");
			} else if (errorShown) {
				logger.info("EC007 INFO: Server is strict. Padded username was rejected.");
				ExtentReportManager.getTest().log(Status.INFO,
						"EC007 INFO: Padded username rejected.");
			} else {
				logServerErrorOrLoginState("EC007", "Unexpected outcome for padded username.");
			}
		}

		// Either outcome is acceptable — what matters is the app does not crash
		Assert.assertFalse(driver.getPageSource().isBlank(),
				"EC007 FAIL: Page source is blank — application may have crashed.");
	}

	/**
	 * EC008 — Back button after logout
	 */
	@Test(
			groups = { "edge", "session" },
			priority = 38,
			description = "EC008: Browser Back after logout."
			)
	public void EC008_backButtonAfterLogout() {
		
		// Step 1: log in
		new LoginPage(driver).login(userName, password);

		Assert.assertTrue(new HomePage(driver).isHomePageDisplayed(), "EC008 PRE-CONDITION: Must be logged in.");
		
		ExtentReportManager.getTest().info("Pre-condition: Logged in successfully.");

		// Step 2: log out
		new HomePage(driver).clickLogoutButton();
		ExtentReportManager.getTest().info("Step 1: Click Logout Button.");
		Assert.assertTrue(new LoginPage(driver).isLoginPageDisplayed(),
				"EC008 PRE-CONDITION: Should be on login page after logout.");

		// Step 3: click Back
		logger.info("EC008: Clicking browser Back after logout.");
		ExtentReportManager.getTest().info("Step 2: Click Back Button after Logout.");
		driver.navigate().back();
		screenshot("EC008", "AFTER_BACK_POST_LOGOUT");

		String resultUrl = currentUrl();
		logger.info("EC008: URL after Back: {}", resultUrl);

		// The page returned to must NOT give access to authenticated content
		HomePage home = new HomePage(driver);
		boolean contentVisible = home.isHomePageDisplayed();

		if (contentVisible) {
			logger.error("EC008 DEFECT: Content visible after clicking Back after logout!");
			ExtentReportManager.getTest().log(Status.FAIL,
					"EC008 DEFECT: Content visible after clicking Back after logout!");
			ExtentReportManager.getTest().warning("EC008: Ensure the Home Page content is unaccessible after logout");
		} else {
			ExtentReportManager.getTest().log(Status.PASS,
					"EC008 PASS: Back button after logout did not redirect back to Home Page.");
		}
	}

	/**
	 * Detects whether the current page is an HTTP 500 error and logs accordingly.
	 * Safe to call regardless of whether a 500 actually occurred.
	 */
	private void logServerErrorOrLoginState(String tcId, String bugNote) {
		String source = driver.getPageSource();
		boolean is500 = source.contains("HTTP Status 500") || source.contains("NullPointerException")
				|| (source.contains("500") && source.contains("Apache Tomcat"));

		if (is500) {
			logger.error("{}: HTTP 500 encountered — {}", tcId, bugNote);
			ExtentReportManager.getTest().log(Status.WARNING,
					tcId + " WARNING: HTTP 500 triggered. " + bugNote);
		} else {
			logger.info("{}: No 500 error. URL: {}", tcId, currentUrl());
			ExtentReportManager.getTest().log(Status.INFO, tcId + ": No server error. Current URL: " + currentUrl());
		}
	}

	/**
	 * Asserts the user did NOT successfully log in. Uses the logout-link as the
	 * definitive "logged in" indicator.
	 */
	private void assertUserNotLoggedIn(String message) {
		Assert.assertFalse(new HomePage(driver).isHomePageDisplayed(), message);
	}
}
