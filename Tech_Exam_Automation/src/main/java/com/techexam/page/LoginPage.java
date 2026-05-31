package com.techexam.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.techexam.utils.LoginResult;
import com.techexam.webdriver.BasePage;

/**
 * Page Object for the Login page.
 *
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
	public LoginResult login(String username, String password) {
		return enterUserName(username).enterPassword(password).clickLogin();
	}

	/** Types the username without submitting. */
	public LoginPage enterUserName(String userName) {
		type(ssoUserNameText, true);
		return this;
	}

	/** Types the password without submitting. */
	public LoginPage enterPassword(String password) {
		type(ssoPasswordText, true);
		return this;
	}

	/** Clicks the login / submit button. */
	public LoginResult clickLogin() {
		click(loginButton);

		if (isElementPresent(logoutButton)) {
			return LoginResult.success(new HomePage(webDriver));
		} else if (isElementPresent(errorLoginMessage)) {
			return LoginResult.failure(this);
		} else {
			throw new IllegalStateException("Unknown login outcome");
		}
	}

	public String verifyErrorMessage() {

		if (errorLoginMessage.isDisplayed()) {
			return errorLoginMessage.getText(); // Update checking
		}
		return "";
	}

	public boolean isLoginPageDisplayed() {
		return isElementPresent(ssoUserNameText) && isElementPresent(ssoPasswordText);
	}
}
