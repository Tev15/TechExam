package com.techexam.webdriver;

import java.time.Duration;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base class for all Page Objects containing methods to call
 * 
 * @author steve.estoconing
 */
public class BasePage {

	protected WebDriver webDriver;

	protected WebDriverWait wait;

	public BasePage(WebDriver webDriver) {

		this.webDriver = webDriver;
		this.wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
		PageFactory.initElements(webDriver, this);
	}

	// ----------------------------------------------------------------
	// Element interactions
	// ----------------------------------------------------------------

	/**
	 * Clicks the {@link WebElement} when it is clickable
	 * 
	 * @param webElement {@link WebElement} to click
	 */
	protected void click(WebElement element) {
		wait.until(ExpectedConditions.elementToBeClickable(element)).click();
	}

	/**
	 * Returns the displayed text of a {@link WebElement} when it is visible
	 * 
	 * @param webElement {@link WebElement} to get displayed text
	 */
	protected String getText(WebElement element) {
		return wait.until(ExpectedConditions.visibilityOf(element)).getText();
	}

	/**
	 * Provides text input to a {@link WebElement}, preferably text fields.
	 * 
	 * @param webElement {@link WebElement} to put text into
	 * @param text       text to put in {@link WebElement}
	 */
	protected void type(WebElement element, CharSequence... text) {
		type(element, false, text);
	}

	/**
	 * Provides text input to a {@link WebElement}, preferably text fields.
	 * 
	 * @param webElement  {@link WebElement} to put text into
	 * @param clearBefore clears the {@link WebElement} of any pre-displayed value
	 * @param text        text to put in {@link WebElement}
	 */
	protected void type(WebElement element, boolean clearBefore, CharSequence... text) {
		type(element, clearBefore, false, text);
	}

	/**
	 * Provides text input to a {@link WebElement}, preferably text fields.
	 * 
	 * @param webElement  {@link WebElement} to put text into
	 * @param clearBefore clears the {@link WebElement} of any pre-displayed value
	 * @param clickBefore clicks the {@link WebElement} before text input, use this
	 *                    only if text input doesn't work due to focus issues
	 * @param text        text to put in {@link WebElement}
	 */
	protected void type(WebElement element, boolean clearBefore, boolean clickBefore, CharSequence... text) {
		if (clearBefore) {
			element.clear();
		}

		if (clickBefore) {
			element.click();
		}

		element.sendKeys(text);
	}

	public boolean isElementPresent(WebElement element) {
		try {
			wait.until(ExpectedConditions.visibilityOf(element));
			return true;
		} catch (TimeoutException e) {
			return false;
		}
	}
}
