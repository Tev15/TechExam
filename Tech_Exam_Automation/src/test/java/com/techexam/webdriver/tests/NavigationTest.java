package com.techexam.webdriver.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.techexam.webdriver.base.BaseTest;
import com.techexam.webdriver.page.HomePage;
import com.techexam.webdriver.page.LoginPage;
import com.techexam.webdriver.report.ExtentReportManager;

/**
 * TC008 – TC010: Navigation tests.
 *
 * @author steve.estoconing
 */
public class NavigationTest extends BaseTest {

	private HomePage homePage;

	private void loginFirst() {
		logger.info("Pre-condition: logging in before current Test");

		new LoginPage(driver).login(userName, password);

		homePage = new HomePage(driver);

		Assert.assertTrue(homePage.isHomePageDisplayed(), "Pre-condition FAIL: Could not log in before current Test");

		ExtentReportManager.getTest().info("Pre-condition: Logged in successfully.");
	}

	// ----------------------------------------------------------------
	// TC008 — Home page loads correctly after login
	// ----------------------------------------------------------------

	@Test(
			groups = { "smoke", "regression","navigation" },
			priority = 8,
			description = "TC008: Verify the home/dashboard page loads and is not blank after login."
			)
	public void TC008_homePageLoadsAfterLogin() {

		loginFirst();

		logger.info("TC008: Verifying home page content.");
		
		ExtentReportManager.getTest().info("Step1: Verify Home Page content.");

		screenshot("TC008", "HOME_PAGE");

		String url = currentUrl();
		String title = currentTitle();

		logger.info("TC008: URL='{}', Title='{}'", url, title);
		ExtentReportManager.getTest().info("URL: " + url + "  |  Title: " + title);

		Assert.assertNotNull(url, "TC008 FAIL: URL should not be null.");
		Assert.assertFalse(title.isBlank(), "TC008 FAIL: Page title should not be blank after login. URL: " + url);

		Assert.assertFalse(url.contains("login"),
				"TC008 FAIL: URL still contains 'login' — user may not be logged in.");

		ExtentReportManager.getTest().log(Status.PASS, "TC008 PASS: Home page loaded. Title=" + title);
	}

	// ----------------------------------------------------------------
	// TC009 — Direct URL to login while logged in
	// ----------------------------------------------------------------

	@Test(
			groups = { "regression","navigation" },
			priority = 9,
			description = "TC009: Verify that navigating directly to the login URL while logged in is handled."
			)
	public void TC009_directUrlToLoginWhileLoggedIn() {
		loginFirst();

		String loginUrl = baseUrl; // BASE_URL is the login page

		logger.info("TC009: Navigating directly to login page while logged in: {}", loginUrl);
		
		ExtentReportManager.getTest().info("Step1: Navigate directly to login page while logged in.");
		
		driver.get(loginUrl);

		screenshot("TC009", "DIRECT_LOGIN_URL");

		String resultUrl = currentUrl();
		logger.info("TC009: Resulting URL: {}", resultUrl);

		boolean redirectedToHome = !resultUrl.contains("login");
		boolean loginPageShown = new LoginPage(driver).isLoginPageDisplayed();

		Assert.assertTrue(redirectedToHome || loginPageShown,
				"TC009 FAIL: Neither home page redirect nor login page displayed. URL: " + resultUrl);

		ExtentReportManager.getTest().log(Status.PASS,
				"TC009 PASS: Direct login URL handled gracefully. Result URL: " + resultUrl);
	}

	// ----------------------------------------------------------------
	// TC010 — No page has a blank title
	// ----------------------------------------------------------------

	@Test(
			groups = { "regression","navigation" },
			priority = 10,
			description = "TC010: Verify that every navigated page has a non-empty page title.")
	public void TC010_pageTitlesAreNotBlank() {

		loginFirst();

		// Always check at least the current (home) page
		String homeTitle = currentTitle();
		logger.info("TC010: Home Page Title: '{}'", homeTitle);
		ExtentReportManager.getTest().info("Step1: Verify Home Page Title.");
		Assert.assertFalse(homeTitle.isBlank(), "TC010 FAIL: Home page title is blank.");

		homePage.clickLogoutButton();

		String loginTitle = currentTitle();
		logger.info("TC010: Login Page Title: '{}'", loginTitle);
		ExtentReportManager.getTest().info("Step2: Verify Login Page Title.");
		Assert.assertFalse(loginTitle.isBlank(), "TC010 FAIL: Home page title is blank.");

		ExtentReportManager.getTest().log(Status.PASS, "TC010 PASS: All navigated pages have non-blank titles.");
	}
}
