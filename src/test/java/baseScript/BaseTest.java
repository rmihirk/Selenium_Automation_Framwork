package baseScript;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import baseclass.ApplicationProperties;
import baseclass.Chrome_Driver;
import baseclass.Reporter;

public class BaseTest extends Chrome_Driver {
	protected Logger logger = Logger.getLogger(this.getClass());
	ApplicationProperties pro = ApplicationProperties.getInstance();

	public String password = "";
	public String base_url = "";
	public String username = "";

	private String line = "=================================================================";

	public BaseTest() {
		PropertyConfigurator.configure("config/log4j.properties");
	}

	@BeforeClass
	public void beforeSuite() {
		getStaticData();
		try {
			driver = new ChromeDriver(chromeOption());
		} catch (UnhandledAlertException alertException) {
			logger.info(alertException);
			driver.switchTo().alert().accept();
			driver = new ChromeDriver(chromeOption());
		}
		driver.get(base_url);
		logger.info(line);
		logger.info("TESTCASE START TIME [ BEFORE CLASS ] : " + systemTime());
		logger.info(line);
	}

	@AfterClass
	protected void testCaseEndTime() {
		logger.info(line);
		logger.info("TESTCASE END TIME [AFTER CLASS] : " + systemTime());
		logger.info(line);

		if (null != driver) {
			driver.quit();
		}
	}

	protected void expectedResult() {
		Reporter.log("");
		Reporter.log("Expected :");
		Reporter.log("=========");
	}

	private void getStaticData() {
		username = pro.getProperty("username");
		password = pro.getProperty("password");
		base_url = ApplicationProperties.getInstance().getProperty("base.url");
		logger.info("================= EXECUTION USER DETAILAS ====================");
		logger.info("CURRENT LOGGIN USER EMAIL ID IS : " + username);
		logger.info("CURRENT LOGGIN USER PASSWORD IS : " + password);
		logger.info("URL IS DETECTED FROM PROPERTY FILE : " + base_url);
	}

	protected String getPhoneNumber() {
		return ApplicationProperties.getInstance().getProperty("phonenumber").trim();
	}

	protected String getPassword() {
		return ApplicationProperties.getInstance().getProperty("passsword").trim();
	}

	protected void testCaseDevelopedBy(String scriptWriterName, String testCaseName) {
		Reporter.log("\n===============================================================");
		Reporter.log(
				"<b> <font color='blue' size='2'>Automation Case Developed by : " + scriptWriterName + "</font></b>");
		Reporter.log("<b> <font color='blue' size='2'>TestCase Name : " + testCaseName + "</font></b>");
		Reporter.log("<b> <font color='blue' size='2'>TestCase Start Time : " + systemTime() + "</font></b>");
		Reporter.log("<b> <font color='blue' size='2'>EXECUTION ON URL : " + base_url + "</font></b>");
		Reporter.log("=================================================================");
	}

	protected static String systemTime() {
		Calendar calendar = new GregorianCalendar();
		String amPm;
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		if (calendar.get(Calendar.AM_PM) == 0) {
			amPm = "AM";
		} else {
			amPm = "PM";
		}
		return hour + "_" + minute + "_" + second + "_" + amPm;
	}
}