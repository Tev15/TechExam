package com.techexam.base;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 * Base class containing methods to call 
 * 
 * @author steve.estoconing
 */
public class BasePage {
	
	protected WebDriver driver;
	
	protected WebDriverWait wait;
	
	public BasePage(WebDriver driver) {
		
		this.driver = driver;
		
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		
		PageFactory.initElements(driver, this);
	}
	
	/**
	 * Clicks the WebElement when it is clickable
	 */
	protected void click(WebElement element) {
		wait.until(ExpectedConditions.elementToBeClickable(element)).click();
	}
	
	/**
	 * Returns the displayed text of a WebElement when it is visible
	 */
	protected String getText(WebElement element) {
		return wait.until(ExpectedConditions.visibilityOf(element)).getText();
	}
	
	/**
	 * Provides text input to a WebElement, preferably text fields.
	 */
	protected void type(WebElement element, CharSequence... text) {
		type(element, false, text);
	}
	
	protected void type(WebElement element, boolean clearBefore, CharSequence... text) {
		type(element, clearBefore, false, text);
	}
	
	protected void type(WebElement element, boolean clearBefore, boolean clickBefore, CharSequence... text) {
		if (clearBefore) {
			element.clear();
		}
		
		if (clickBefore) {
			element.click();
		}
		
		element.sendKeys(text);
	}
}
