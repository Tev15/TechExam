# Tech Exam QA Automation Framework

**QA Automation — Selenium WebDriver + TestNG + Java (Eclipse / Maven)**

## 1. Prerequisites

| Requirement        | Version  | Notes                                        |
|--------------------|----------|----------------------------------------------|
| Java JDK           | 11+      | Add JAVA_HOME to system PATH                 |
| Eclipse IDE        | 2026-03+ | "Eclipse IDE for Enterprise Java Developers" |
| Maven              | 3.8+     | Bundled with Eclipse; also works standalone  |
| Google Chrome      | Latest   | WebDriverManager auto-downloads ChromeDriver |
| Git                | Optional | For version control                          |

---

## 2. Importing the Project into Eclipse

1. **File → Import → Maven → Existing Maven Projects**
2. Browse to the `exam-automation` folder → **Finish**
3. Eclipse will automatically resolve all Maven dependencies
4. If prompted about TestNG plugin: **Help → Eclipse Marketplace → search "TestNG"** → Install

---

## 3. Project Structure

```
techexam-automation/
├── src/
│   ├── main/java/com/exam/webdriver/
│   │   ├── WebDriverRuntimeException.java   ← Custom exception
│   │   ├── WebDriverComponent.java          ← Driver lifecycle (getInstance/createInstance)
│   │   └── util/
│   │       └── WebDriverUtils.java          ← takeScreenshot()
│   │   └── props/
│   │       └── EnvironmentProperties.java   ← Singleton handling timeouts and page errors (getInstance/createInstance)
│   └── test/java/com/exam/webdriver/
│       ├── base/
│       │   └── BaseTest.java                ← @BeforeMethod init + @AfterMethod teardown + @BeforeSuite & @AfterSuite logs
│       ├── pages/
│       │   ├── BasePage.java                ← Shared wait/click/type helpers
│       │   ├── LoginPage.java               ← Login page interactions
│       │   └── HomePage.java                ← Dashboard / home page interactions
│       ├── tests/
│       │   ├── LoginTest.java               ← TC001–TC007 (Authentication)
│       │   ├── NavigationTest.java          ← TC008–TC010 (Navigation)
│       │   └── FormTest.java                ← TC011–TC012 (Forms)
│       │   └── EdgeCaseTest.java            ← EC001–EC008 (Edge Cases)
│       ├── listeners/
│       │   └── TestListener.java            ← Screenshots on failure + Extent logs
│       └── report/
│           └── ExtentReportManager.java     ← HTML report builder
├── src/main/resources/
│   └── log4j2.xml                           ← Console + rolling-file logger config
│   └── environment.properties               ← Timeouts and Page Errors
├── src/test/resources/
│   └── config.properties                    ← URL, credentials, browser, screenshot dir
├── screenshots/                             ← Auto-created; named TC00X-ACTION.png
├── test-output/
│   ├── logs/automation.log                  ← Execution log (rolling)
│   └── reports/ExtentReport_<ts>.html       ← HTML test report
├── testng.xml                               ← Suite, groups, listener registration
└── pom.xml                                  ← Maven dependencies
```

---

## 4. Configuration

Edit **`src/test/resources/config.properties`** before the first run:

```properties
app.base.url=http://35.78.90.242:8080/exam/login
app.username=sakamoto
app.password=1234passWord
app.browser=chrome          # chrome | firefox
```
### Required Dependencies

The following dependencies are declared in **`pom.xml`**.
Java compiler source/target is set to **Java 11** (`maven.compiler.source` / `maven.compiler.target`).

| Library                  | Group ID                          | Version  | Scope   | Purpose                              |
|--------------------------|-----------------------------------|----------|---------|--------------------------------------|
| Selenium Java            | `org.seleniumhq.selenium`         | 4.44.0   | compile | Core browser automation              |
| WebDriverManager         | `io.github.bonigarcia`            | 6.3.4    | compile | Auto-downloads ChromeDriver          |
| TestNG                   | `org.testng`                      | 7.9.0    | test    | Test framework & assertions          |
| Selenium Shutterbug      | `com.assertthat`                  | 1.6      | compile | Full-page screenshot capture         |
| Apache Commons Lang3     | `org.apache.commons`              | 3.14.0   | compile | String/object utilities              |
| Commons IO               | `commons-io`                      | 2.15.1   | compile | File I/O utilities                   |
| ExtentReports            | `com.aventstack`                  | 5.1.2    | compile | HTML test report generation          |
| Log4j Core               | `org.apache.logging.log4j`        | 2.22.1   | compile | Logging implementation               |
| Log4j API                | `org.apache.logging.log4j`        | 2.22.1   | compile | Logging API                          |

#### Build Plugins

| Plugin                   | Version  | Purpose                                      |
|--------------------------|----------|----------------------------------------------|
| `maven-compiler-plugin`  | 3.12.1   | Compiles source at Java 11                   |
| `maven-surefire-plugin`  | 3.0.0    | Runs TestNG suite via `testng.xml`           |

#### Full `pom.xml` Dependencies Block

```xml
<dependencies>
    <!-- Selenium WebDriver -->
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.44.0</version>
    </dependency>

    <!-- WebDriverManager (auto-downloads ChromeDriver) -->
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>6.3.4</version>
    </dependency>

    <!-- TestNG -->
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.9.0</version>
        <scope>test</scope>
    </dependency>

    <!-- Selenium Shutterbug (screenshots) -->
    <dependency>
        <groupId>com.assertthat</groupId>
        <artifactId>selenium-shutterbug</artifactId>
        <version>1.6</version>
    </dependency>

    <!-- Apache Commons -->
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.14.0</version>
    </dependency>
    <dependency>
        <groupId>commons-io</groupId>
        <artifactId>commons-io</artifactId>
        <version>2.15.1</version>
    </dependency>

    <!-- ExtentReports (HTML reporting) -->
    <dependency>
        <groupId>com.aventstack</groupId>
        <artifactId>extentreports</artifactId>
        <version>5.1.2</version>
    </dependency>

    <!-- Log4j2 (logging) -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.22.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>2.22.1</version>
    </dependency>
</dependencies>
```

---

## 5. Running Tests

### Option A — Eclipse (Recommended for demos)

1. Right-click `testng.xml` → **Run As → TestNG Suite**
2. Or right-click any `*Test.java` → **Run As → TestNG Test**

### Option B — Maven (CI/CD)

```bash
# Full regression suite
mvn clean test

# Smoke tests only
mvn clean test -Dgroups=smoke

# Single test class
mvn clean test -Dtest=LoginTest
```

---

## 6. Test Cases

| ID    | Class           | Description                                        | Group                |
|-------|-----------------|----------------------------------------------------|----------------------|
| TC001 | LoginTest       | Valid login with correct credentials               | smoke, auth          |
| TC002 | LoginTest       | Invalid login — wrong password                     | regression, neg      |
| TC003 | LoginTest       | Invalid login — wrong username                     | regression, neg      |
| TC004 | LoginTest       | Login with empty username and password             | regression, neg      |
| TC005 | LoginTest       | Login with empty username only                     | regression, neg      |
| TC006 | LoginTest       | Login with empty password only                     | regression, neg      |
| TC007 | LoginTest       | Successful logout                                  | smoke, auth          |
| TC008 | NavigationTest  | Home page loads after login                        | smoke, navigation    |
| TC009 | NavigationTest  | Navigate to secondary page via nav link            | regression           |
| TC010 | NavigationTest  | Browser back button restores previous page         | regression           |
| TC011 | FormTest        | Text Input Retains Value                           | form                 |
| TC012 | FormTest        | Invalid URL Returns Error Page                     | error handling       |
| EC001 | EdgeCaseTest    | Whitespace-only Username                           | edge, input-boundary |
| EC002 | EdgeCaseTest    | Whitespace-only Password                           | edge, input-boundary |
| EC003 | EdgeCaseTest    | Case sensitivity of Username                       | edge, input-boundary |
| EC004 | EdgeCaseTest    | Oversized username (500 chars)                     | edge, input-boundary |
| EC005 | EdgeCaseTest    | Oversized password (1000 chars)                    | edge, input-boundary |
| EC006 | EdgeCaseTest    | Leading and trailing spaces around valid username. | edge, input-boundary |
| EC007 | EdgeCaseTest    | Leading and trailing values around valid username  | edge, input-boundary |
| EC008 | EdgeCaseTest    | Back button after logout                           | edge, session        |

---

## 7. Reports and Logs

### Extent HTML Report
- Generated at `test-output/reports/ExtentReport_<timestamp>.html`
- Open in any browser — screenshots embedded inline for FAIL cases

### Log File
- Rolling log at `test-output/logs/automation.log`
- Matches the reference tool's format:
  ```
  2026-06-02 13:36:07 [INFO ] BaseTest - Starting WebDriver Test Tool
  2026-06-02 13:36:07 [INFO ] BaseTest - Configuration loaded from config.properties
  2026-06-02 13:36:07 [INFO ] BaseTest - Test start : LoginTest.TC001_validLogin
  2026-06-02 13:36:07 [INFO ] BaseTest - Initializing Selenium WebDriver. Browser: chrome
  2026-06-02 13:36:11 [INFO ] WebDriverComponent - WebDriver initialized successfully.
  2026-06-02 13:36:13 [INFO ] BaseTest - Test end   : LoginTest.TC001_validLogin
  2026-06-02 13:36:13 [INFO ] WebDriverComponent - Closing WebDriver instance.
  ```

### Screenshots
- Saved to `screenshots/<TCXXX>-<ACTION>_<timestamp>.png`
- Format mirrors `WebDriverUtils.takeScreenshot(dirPath, testCaseNo, testCaseAction)`

---

## 8. Troubleshooting

| Symptom                              | Fix                                                              |
|--------------------------------------|------------------------------------------------------------------|
| `SessionNotFoundException`           | Chrome version mismatch — run `mvn -U clean test` to re-download |
| Tests fail at pre-condition login    | Confirm `app.base.url` in config.properties is reachable         |
| `NoSuchElementException` on locators | Inspect live HTML and update `@FindBy` in Page Objects           |
| `PageLoadTimeoutException`           | Increase `webdriver.page.load.timeout` in config.properties      |
| Report not generated                 | Ensure `test-output/reports/` exists (Maven creates it)          |
| Screenshot empty / black             | Shutterbug requires page to be fully rendered; add `delay()`     |

---

