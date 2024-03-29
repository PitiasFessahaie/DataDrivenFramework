package library;

import org.openqa.selenium.WebDriver;
import org.testng.AssertJUnit;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.log4testng.Logger;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
/***
 * 
 * @author Pitias Fessahaie
 * Date: 02/20/2014
 *
 */

public class Base {
	public static WebDriver driver;
	public static Library lib;
	public String browser = "";
	public String headless = "";
	public String OS = "Mac iOS";
	public String sendEmail;
	public String startTime;
	Logger logger = Logger.getLogger(Base.class);
	JavaPropertiesManager prop;
	JavaPropertiesManager eprop;

	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest test;

	@BeforeClass
	public void startup() {
		try {

			// initialize the HtmlReporter
			htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "/test-output/testReport.html");
			// initialize ExtentReports and attach the HtmlReporter
			extent = new ExtentReports();
			extent.attachReporter(htmlReporter);

			// To add system or environment info by using the setSystemInfo method.
			extent.setSystemInfo("OS", OS);
			extent.setSystemInfo("Browser", browser);

			// configuration items to change the look and feel
			// add content, manage tests etc
			htmlReporter.config().setChartVisibilityOnOpen(true);
			htmlReporter.config().setDocumentTitle("Extent Report Demo");
			htmlReporter.config().setReportName("Test Report");
			htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
			htmlReporter.config().setTheme(Theme.STANDARD);
			htmlReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
			test = extent.createTest("Starting Class", "Class Inintiated successful!!");

			lib = new Library(driver);
			prop = new JavaPropertiesManager("src/test/resources/config.properties");
			eprop = new JavaPropertiesManager("src/test/resources/SessionTime.properties");
			logger.info("Starting the Class ...");
			test.log(Status.INFO, "Starting the Class ...");

			startTime = lib.getCurrentTime();
			logger.info("Starting Time : " + startTime);
			test.log(Status.INFO, startTime);

			eprop.writeData("SessionTime", startTime);
			logger.info("Starting Time setting complete...." + startTime);
			browser = prop.readData("browserType");
			headless = prop.readData("Headless");
			if(headless.toLowerCase().contains("on")) {
				lib.setHeadless(true);
			}
			logger.info("Reading Browser Type = " + browser);
			test.log(Status.INFO, "Reading Browser Type = " + browser);

			sendEmail = prop.readData("email");
			logger.info("EmailSend :" + sendEmail);
			test.log(Status.INFO, "EmailSend :" + sendEmail);
			OS = prop.readData("OS");

		} catch (Exception e) {
			logger.error(e.getMessage());
			test.error(e.getMessage());
		}
	}

	@BeforeMethod
	public void setup() {
		try {
			driver = lib.startBrowser(browser);
			logger.info("Starting the webbrowser...");
			test.log(Status.INFO, "Starting the webbrowser...");

		} catch (Exception e) {
			logger.error(e.getMessage());
			test.error(e.getMessage());
		}
	}

	@AfterMethod
	public void tearDown(ITestResult result) {
		try {
			logger.info("End a single test...");
			test.log(Status.INFO, "End a single test ...");

			if (result.getStatus() == ITestResult.FAILURE) {
				// capture screenshot here
				lib.screenCapture(result.getName());

				test.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " FAILED ", ExtentColor.RED));
				test.fail(result.getThrowable());
				logger.info("Generating a Screenshot....");
				test.log(Status.INFO, "Generating a ScreenShot....");

			}
			driver.close();

			Thread.sleep(5 * 1000);
			// if(driver.getWindowHandles().size() > 0) {
			// driver.close(); // closing the browser
			// }
		} catch (Exception e) {
			logger.error("Error: ", e);
			test.error(e.getMessage());
		}
	}

	@AfterClass
	public void afterAllTests() {
		try {
			if (driver != null) {
				driver.quit();
			}
			logger.info("All the test is completed...");

			// sending emails...
			Email email = new Email();
			email.toAddress = "selamptsegu@gmail.com";
			// email.ccAddress = "selamdebas1@gmail.com";

			lib.automaticallyAttachErrorImgToEmail();

			lib.errorScreenshots.add("test-output/testReport.html");
			lib.errorScreenshots.add("test-output/emailable-report.html");
			lib.errorScreenshots.add("test-output/index.html");
			lib.errorScreenshots.add("target/logs/Selenium-Report.html");
			logger.info("attaching reports complete...");
			logger.info("SendEmail :" + sendEmail);
			test.log(Status.INFO, "SendEmail :" + sendEmail);

			if (sendEmail.toLowerCase().contains("on")) {
				logger.info("Sending Email....");
				test.log(Status.INFO, "Sending Email....");
				email.sendEmail(lib.errorScreenshots);
				logger.info("Email send !!");
				test.log(Status.INFO, "Email send !!!");
			} else {
				logger.info("Email did not send !!!");
				test.log(Status.FAIL, "Email did not Send !!!");
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
			AssertJUnit.assertTrue(false);
			test.log(Status.FATAL, e.getMessage());
		}
		extent.flush();
	}

}
