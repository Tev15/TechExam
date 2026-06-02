package com.techexam.webdriver.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.techexam.webdriver.base.BaseTest;
import com.techexam.webdriver.page.HomePage;
import com.techexam.webdriver.page.LoginPage;
import com.techexam.webdriver.props.EnvironmentProperties;
import com.techexam.webdriver.report.ExtentReportManager;

/**
 * TC001 – TC007: User Authentication tests.
 *
 * @author steve.estoconing
 */
public class LoginTest extends BaseTest {

	// ----------------------------------------------------------------
	// TC001 — Valid Login
	// ----------------------------------------------------------------

	@Test(
			groups = { "smoke", "regression","auth" },
			priority = 1, 
			description = "TC001: Verify that a user with valid credentials can log in successfully."
			)
	public void TC001_validLogin() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);

		LoginPage loginPage = new LoginPage(driver);

		Assert.assertTrue(loginPage.isLoginPageDisplayed(), "Login Page not visible.");

		ExtentReportManager.getTest().log(Status.PASS, "Login Page is displayed.");

		logger.info("TC001: Entering valid credentials. User: {}", userName);

		ExtentReportManager.getTest().info("Step 2: Enter valid credentials. Click Login Button");

		loginPage.login(userName, password);

		screenshot("TC001", "AFTER_LOGIN");

		HomePage homePage = new HomePage(driver);
		Assert.assertTrue(homePage.isHomePageDisplayed(),
				"TC001 FAIL: Home page was not displayed after valid login. Current URL: " + currentUrl());

		ExtentReportManager.getTest().log(Status.PASS, "TC001 PASS: User logged in.");

		logger.info("TC001 PASS — User logged in.");
	}

	// ----------------------------------------------------------------
	// TC002 — Wrong Password
	// ----------------------------------------------------------------

	@Test(
			groups = { "regression", "negative","auth" },
			priority = 2,
			description = "TC002: Verify that login fails with a correct username but wrong password."
			)
	public void TC002_wrongPassword() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);

		LoginPage loginPage = new LoginPage(driver);

		logger.info("TC002: Attempting login with wrong password.");

		ExtentReportManager.getTest().info("Step 2: Enter valid username with incorrect password. Click Login Button");

		loginPage.login(userName, "wrongPassword123!");

		screenshot("TC002", "AFTER_FAILED_LOGIN");

		String errorText = loginPage.verifyErrorMessage();

		Assert.assertTrue(
				loginPage.isErrorMessageDisplayed() && errorText.trim()
						.equals(EnvironmentProperties.getInstance().getProperty("error.login.page.wrongpassword")),
				"TC002 FAIL: Error Message" + errorText + " not Displayed.");

		logger.info("TC002: Error message shown: '{}'", errorText);

		ExtentReportManager.getTest().log(Status.PASS,
				"TC002 PASS: Login Unsuccessful. Error message displayed: " + errorText);
	}

	// ----------------------------------------------------------------
	// TC003 — Wrong Username
	// ----------------------------------------------------------------

	@Test(
			groups = { "regression", "negative","auth" },
			priority = 3, 
			description = "TC003: Verify that login fails with a non-existent username."
			)
	public void TC003_wrongUsername() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);

		LoginPage loginPage = new LoginPage(driver);

		logger.info("TC003: Attempting login with wrong username.");

		ExtentReportManager.getTest().info("Step 2: Enter wrong username with password. Click Login Button");

		loginPage.login("adminSteve", password);

		screenshot("TC003", "AFTER_FAILED_LOGIN");

		String errorText = loginPage.verifyErrorMessage();

		Assert.assertTrue(
				loginPage.isErrorMessageDisplayed() && errorText
						.equals(EnvironmentProperties.getInstance().getProperty("error.login.page.wrongusername")),
				"TC003 FAIL: Error Message" + errorText + "not Displayed.");

		logger.info("TC003: Error message shown: '{}'", errorText);

		ExtentReportManager.getTest().log(Status.PASS,
				"TC003 PASS: Login Unsuccessful. Error Message Displayed: " + errorText);
	}

	// ----------------------------------------------------------------
	// TC004 — Empty Username and Password
	// ----------------------------------------------------------------

	@Test(
			groups = { "regression", "negative","auth" },
			priority = 4,
			description = "TC004: Verify that  login fails with both fields empty is blocked."
			)
	public void TC004_emptyCredentials() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);

		LoginPage loginPage = new LoginPage(driver);

		logger.info("TC004: Attempting login with empty credentials.");

		ExtentReportManager.getTest().info("Step 2: Click Login Button.");

		loginPage.clickLoginButton();

		screenshot("TC004", "EMPTY_CREDENTIALS");

		String errorText = loginPage.verifyErrorMessage();

		// Should stay on login page OR show an error
		Assert.assertTrue(
				loginPage.isErrorMessageDisplayed() && errorText
						.equals(EnvironmentProperties.getInstance().getProperty("error.login.page.emptycredentials")),
				"TC004 FAIL: Error Message" + errorText + "not Displayed.");

		ExtentReportManager.getTest().log(Status.PASS,
				"TC004 PASS: Login Unsuccessful. Error Message Displayed: " + errorText);
	}

	// ----------------------------------------------------------------
	// TC005 — Empty Username Only
	// ----------------------------------------------------------------

	@Test(
			groups = { "regression", "negative","auth" },
			priority = 5,
			description = "TC005: Verify that Internal Error occurs when only the password is provided."
			)
	public void TC005_emptyUsername() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);

		LoginPage loginPage = new LoginPage(driver);

		logger.info("TC005: Entering only password, leaving username empty.");

		ExtentReportManager.getTest().info("Step 2: Enter password only. Click Login Button.");

		loginPage.enterPassword(password).clickLoginButton();

		screenshot("TC005", "EMPTY_USERNAME");

		String pageSource = driver.getPageSource();
		
		boolean is500 = pageSource.contains("HTTP Status 500") || pageSource.contains("NullPointerException")
				|| (pageSource.contains("500") && pageSource.contains("Apache Tomcat"));

		if (is500) {
			logger.error("HTTP 500 encountered");
			ExtentReportManager.getTest().log(Status.WARNING," WARNING: HTTP 500 triggered. ");
		} else {
			Assert.assertTrue(loginPage.isLoginPageDisplayed(), "TC005 FAIL: Login page not visible. Current URL: "  + currentUrl());
			logger.info("{}: No 500 error. URL: {}");
			ExtentReportManager.getTest().log(Status.INFO,": No Internal Server Error Occurred.");
		}

	}

	// ----------------------------------------------------------------
	// TC006 — Empty Password Only
	// ----------------------------------------------------------------

	@Test(
			groups = { "regression", "negative","auth" },
			priority = 6,
			description = "TC006: Verify that login fails when only the username is provided."
			)
	public void TC006_emptyPassword() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);

		LoginPage loginPage = new LoginPage(driver);

		logger.info("TC006: Entering only username, leaving password empty.");

		ExtentReportManager.getTest().info("Step 2: Enter Username only. Click Login Button.");

		loginPage.enterUserName(userName).clickLoginButton();

		screenshot("TC006", "EMPTY_PASSWORD");

		String errorText = loginPage.verifyErrorMessage();

		Assert.assertTrue(
				loginPage.isErrorMessageDisplayed() && errorText
						.equals(EnvironmentProperties.getInstance().getProperty("error.login.page.onlyusername")),
				"TC006 FAIL: Error Message" + errorText + "not Displayed.");

		ExtentReportManager.getTest().log(Status.PASS,
				"TC006 PASS: Login Unsuccessful. Error Message Displayed: " + errorText);
	}

	// ----------------------------------------------------------------
	// TC007 — Logout
	// ----------------------------------------------------------------

	@Test(
			groups = { "smoke", "regression","auth" },
			priority = 7,
			description = "TC007: Verify that a logged-in user can log out and is redirected to the login page."
			)
	public void TC007_logout() {
		// First: log in
		LoginPage loginPage = new LoginPage(driver);

		logger.info("TC007 Pre-Condition: Logging in with valid credentials.");

		ExtentReportManager.getTest().info("Precondition: Login with valid credentials");

		loginPage.login(userName, password);

		HomePage homePage = new HomePage(driver);

		Assert.assertTrue(homePage.isHomePageDisplayed(), "TC007 FAIL: Could not log in before testing logout.");

		logger.info("TC007: Logged in. Now clicking logout.");
		ExtentReportManager.getTest().info("Step 1: Login succeeded. Click Logout Button.");

		homePage.clickLogoutButton();

		screenshot("TC007", "AFTER_LOGOUT");

		// After logout the login form should be visible again
		LoginPage loginPageAfterLogout = new LoginPage(driver);
		Assert.assertTrue(loginPageAfterLogout.isLoginPageDisplayed(),
				"TC007 FAIL: Login Page not shown after logout. URL: " + currentUrl());

		ExtentReportManager.getTest().log(Status.PASS, "TC007 PASS: Logout successful.");

		logger.info("TC007 PASS — User logged out.");
	}
}
