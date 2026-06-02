package com.techexam.webdriver.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.techexam.webdriver.base.BaseTest;
import com.techexam.webdriver.page.LoginPage;
import com.techexam.webdriver.report.ExtentReportManager;

/**
 * TC011 – TC012: Form submission and error handling tests.
 *
 * @author steve.estoconing
 */
public class FormTest extends BaseTest {

	// ------------------------------------------------------------------
	// TC011 — Username input retains typed value
	// ------------------------------------------------------------------

	@Test(
			groups = {"regression", "form"}, 
			priority = 11, 
			description = "TC011: Verify that text typed into Username field is retained."
			)
	public void TC011_userIdInputRetainsValue() {
		ExtentReportManager.getTest().info("Step 1: Open login page at " + baseUrl);

		logger.info("TC011: Testing text input retention.");

		LoginPage loginPage = new LoginPage(driver);

		String testValue = userName;

		logger.info("TC011: Entering only username with wrong password.");

		ExtentReportManager.getTest().info("Step 2: Enter Username with wrong password.");

		loginPage.enterUserName(testValue);

		ExtentReportManager.getTest().info("Step 3: Click Login Button. Verify Username retained its value.");

		loginPage.clickLoginButton();
		
		WebElement userNameText = driver.findElement(By.id("uid"));
		
		String actualValue = userNameText.getAttribute("value");

		logger.info("TC011: Expected='{}', Actual='{}'", testValue, actualValue);

		Assert.assertEquals(actualValue, testValue, "TC011 FAIL: Text input did not retain the typed value.");

		screenshot("TC011", "USERNAME_RETAINED");

		ExtentReportManager.getTest().log(Status.PASS, "TC011 PASS: Username Input Value Retained.");
	}

	// ------------------------------------------------------------------
	// TC012 — 404 / Invalid URL is handled gracefully
	// ------------------------------------------------------------------

	@Test(
			groups = { "regression","errorhandling" }, 
			priority = 12, 
			description = "TC012: Verify that navigating to a non-existent page shows an error page, not a crash."
			)
	public void TC012_invalidUrl() {
		String invalidUrl = baseUrl.replaceAll("/login$", "") + "/this-page-does-not-exist-99";
		logger.info("TC012: Navigating to invalid URL: {}", invalidUrl);

		ExtentReportManager.getTest().info("Step 1: Open Invalid URL at " + invalidUrl);

		driver.get(invalidUrl);

		screenshot("TC012", "INVALID_URL");

		// Application must not produce a completely blank white page
		String pageSource = driver.getPageSource();
		Assert.assertNotNull(pageSource, "TC012 FAIL: Page source is null for invalid URL.");
		Assert.assertFalse(pageSource.isBlank(), "TC012 FAIL: Blank page for invalid URL " + invalidUrl);

		Assert.assertTrue(pageSource.contains("404") || pageSource.toLowerCase().contains("http status 404"),
				"TC012 FAIL: Page body does not contain expected 404 error content.");

		logger.info("TC012: Page returned for invalid URL. Title: '{}'", currentTitle());
		ExtentReportManager.getTest().log(Status.PASS,
				"TC012 PASS: Invalid URL returned non-blank page. Title=" + currentTitle());
	}
}
