package com.techexam.webdriver.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Manages the Extent HTML report lifecycle.
 *
 * Usage:
 *   ExtentReportManager.init();           // call once in @BeforeSuite
 *   ExtentReportManager.createTest(name); // call per @Test method
 *   ExtentReportManager.getTest();        // use inside test/listener
 *   ExtentReportManager.flush();          // call once in @AfterSuite
 *
 * @author steve.estoconing
 */
public final class ExtentReportManager {

    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    private ExtentReportManager() { }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Initialises the Extent report.
     * Writes the HTML report to {@code test-output/reports/ExtentReport_<timestamp>.html}.
     */
    public static void init() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = System.getProperty("user.dir")
                + "/test-output/reports/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("QA Automation — Exam Test Report");
        spark.config().setReportName("Exam Web Application Test Suite");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Application URL",  "http://35.78.90.242:8080/exam/login");
        extent.setSystemInfo("Test User",        "sakamoto");
        extent.setSystemInfo("Framework",        "Selenium WebDriver 4 + TestNG 7");
        extent.setSystemInfo("Java Version",     System.getProperty("java.version"));
        extent.setSystemInfo("OS",               System.getProperty("os.name"));

        logger.info("Extent report initialised → {}", reportPath);
    }

    /**
     * Creates a new test node and binds it to the current thread.
     *
     * @param testName display name for the test
     * @return the created {@link ExtentTest}
     */
    public static ExtentTest createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
        return test;
    }

    /**
     * Creates a test node with a description.
     *
     * @param testName    display name
     * @param description brief purpose/objective
     * @return the created {@link ExtentTest}
     */
    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = extent.createTest(testName, description);
        testThread.set(test);
        return test;
    }

    /** Returns the {@link ExtentTest} for the current thread. */
    public static ExtentTest getTest() {
        return testThread.get();
    }

    /**
     * Flushes all results to the HTML file.
     * Must be called in {@code @AfterSuite}.
     */
    public static void flush() {
        if (extent != null) {
            extent.flush();
            logger.info("Extent report flushed.");
        }
    }
}
