package com.techexam.webdriver.base;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.techexam.webdriver.WebDriverComponent;
import com.techexam.webdriver.WebDriverRuntimeException;
import com.techexam.webdriver.utils.WebDriverUtils;

/**
 * Base class for all test classes.
 *
 * @author steve.estoconing
 */
public abstract class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    // Loaded once from config.properties
    protected static Properties config;
    protected static String baseUrl;
    protected static String userName;
    protected static String password;
    protected static String browser;

    // Per-test driver reference (convenience shortcut)
    protected WebDriver driver;
    protected WebDriverWait wait;

    // ----------------------------------------------------------------
    // Suite-level setup
    // ----------------------------------------------------------------

    @BeforeSuite(alwaysRun = true)
    public void suiteSetUp() {
        logger.info("====================================================");
        logger.info("Starting WebDriver Test Tool");
        logger.info("====================================================");

        loadConfig();
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTearDown() {
        logger.info("====================================================");
        logger.info("Test Suite completed.");
        logger.info("====================================================");
    }

    // ----------------------------------------------------------------
    // Method-level setup / teardown
    // ----------------------------------------------------------------

    /**
     * Called before every @Test method.
     * Initialises a fresh WebDriver, opens the application, and
     * logs the same info messages the reference emits.
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        logger.info("---------------------------------------------------");
        logger.info("Test start : {}.{}",
                getClass().getSimpleName(), method.getName());

        logger.info("Initializing Selenium WebDriver. Browser: {}", browser);
        WebDriverComponent.createInstance(browser);
        logger.info("Creating runner instances.");

        driver = WebDriverComponent.getInstance().getWebDriver();
        wait   = WebDriverComponent.getInstance().getWait();

        logger.info("Setting environment properties.");
        logger.info("Navigating to base URL: {}", baseUrl);

        driver.get(baseUrl);

        logger.info("WebDriver ready.");
    }

    /**
     * Called after every @Test method.
     * Quits the driver and logs completion — mirrors
     * {@code logger.info("Closing WebDriver instance.")} from reference.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        logger.info("Test end   : {}.{}",
                getClass().getSimpleName(), method.getName());

        WebDriverComponent component = WebDriverComponent.getInstance();
        if (component != null) {
            component.clear();
        }
        logger.info("---------------------------------------------------");
    }

    // ----------------------------------------------------------------
    // Config loading
    // ----------------------------------------------------------------

    private static synchronized void loadConfig() {
        if (config != null) 
        	return;   // already loaded

        config = new Properties();
        try (InputStream is = BaseTest.class
                .getResourceAsStream("/config.properties")) {
            if (is == null) {
                throw new WebDriverRuntimeException(
                        "Configuration Reading Failed",
                        new IOException("config.properties not found on classpath"));
            }
            config.load(is);
        } catch (IOException ex) {
            throw new WebDriverRuntimeException(
            		"Configuration Reading Failed", ex);
        }
        
        baseUrl = config.getProperty("app.base.url");
        userName = config.getProperty("app.username");
        password = config.getProperty("app.password");
        browser  = config.getProperty("app.browser");

        logger.info("Configuration loaded from config.properties");
    }

    // ----------------------------------------------------------------
    // Shared helpers
    // ----------------------------------------------------------------

    /** Returns the current page URL from the running driver. */
    protected String currentUrl() {
        return driver.getCurrentUrl();
    }

    /** Returns the current page title. */
    protected String currentTitle() {
        return driver.getTitle();
    }

    /** Convenience wrapper: captures a screenshot with the test case number. */
    protected void screenshot(String testCaseNo, String action) {
        WebDriverUtils.takeScreenshot("screenshots", testCaseNo, action);
    }
}
