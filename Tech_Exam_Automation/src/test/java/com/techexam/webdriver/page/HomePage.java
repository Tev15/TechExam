package com.techexam.webdriver.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Home page.
 *
 * @author steve.estoconing
 */
public class HomePage extends BasePage {

	// ----------------------------------------------------------------
	// @FindBy annotations
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

	/**
	 * Logs out by clicking the logout link. After this call the driver will be on
	 * the Login page.
	 */
	public void clickLogoutButton() {
		click(logoutButton);
	}

	/** {@code true} if the home/dashboard page header is visible. */
	public boolean isHomePageDisplayed() {
		return isElementPresent(logoutButton);
	}
}
