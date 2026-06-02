package com.techexam.webdriver.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object for the Login page.
 *
 * @author steve.estoconing
 */

public class LoginPage extends BasePage {

	// ----------------------------------------------------------------
	// Locators
	// ----------------------------------------------------------------

	@FindBy(id = "uid")
	private WebElement ssoUserNameText;

	@FindBy(name = "password")
	private WebElement ssoPasswordText;

	@FindBy(id = "loginSubmit")
	private WebElement loginButton;

	@FindBy(className = "warnmessage")
	private WebElement errorLoginMessage;

	@FindBy(linkText = "戻る")
	private WebElement logoutButton;

	// ----------------------------------------------------------------

	public LoginPage(WebDriver webDriver) {
		super(webDriver);
	}

	// ----------------------------------------------------------------
	// Page actions
	// ----------------------------------------------------------------

	/**
	 * Enters credentials and clicks the login button.
	 *
	 * @param username user ID
	 * @param password plain-text password
	 * @return this LoginPage (for method chaining / login failure assertion)
	 */
	public LoginPage login(String username, String password) {
		return enterUserName(username).enterPassword(password).clickLoginButton();
	}

	/** Types the username without submitting. */
	public LoginPage enterUserName(String userName) {
		type(ssoUserNameText, userName);
		return this;
	}

	/** Types the password without submitting. */
	public LoginPage enterPassword(String password) {
		type(ssoPasswordText, password);
		return this;
	}

	/** Clicks the login / submit button. */
	public LoginPage clickLoginButton() {
		click(loginButton);
		return this;
	}
	
	public boolean isErrorMessageDisplayed() {
		return isElementPresent(errorLoginMessage);
	}

	/** Verifies Error Message shown after failed login attempt */
	public String verifyErrorMessage() {

		if (isErrorMessageDisplayed()) {
			return errorLoginMessage.getText(); // Update checking
		}
		return "";
	}

	/** Verifies Login Page is displayed */
	public boolean isLoginPageDisplayed() {
		return isElementPresent(ssoUserNameText) && isElementPresent(ssoPasswordText);
	}
}
