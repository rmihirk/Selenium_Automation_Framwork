package baseclass;

import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

public class Chrome_Driver {

	public static WebDriver driver;

	protected static ChromeOptions  chromeOption() {
		LoggingPreferences logPrefs = new LoggingPreferences();
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");
		options.addArguments("start-maximized");
		options.addArguments("incognito");
		options.addArguments("--ignore-certificate-errors");
		options.addArguments("--disable-popup-blocking");
		options.setCapability("goog:loggingPrefs",logPrefs);
		
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		return options;
	}

	public static WebDriver getInstance() {
		if (driver == null) {
			driver = new ChromeDriver(chromeOption());
			return driver;
		}
		return driver;
	}
}
