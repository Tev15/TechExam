package com.techexam.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.techexam.webdriver.BasePage;

public class HomePage extends BasePage {

	// ----------------------------------------------------------------
	// Locators
	// ----------------------------------------------------------------
	
	@FindBy(linkText = "戻る")
	private WebElement logoutButton;
	
	// ----------------------------------------------------------------

	public HomePage(WebDriver webDriver) {
		super(webDriver);
	}

	// ----------------------------------------------------------------
	// Page actions
	// ----------------------------------------------------------------

	
	public void loginCompletePage() {
		webDriver.findElement(By.tagName("text()"));
	}
	
	/**
     * Logs out by clicking the logout link.
     * After this call the driver will be on the Login page.
     */
	public void clickLogout() {
		click(logoutButton);
	}
}
