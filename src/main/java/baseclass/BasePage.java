package baseclass;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.google.common.base.Function;

public class BasePage extends Chrome_Driver {
	public int Seconds = 60;
	public Logger logger = Logger.getLogger(this.getClass().getName());;
	Actions build;
	public JavascriptExecutor jse;
	WebDriverWait wait;

	public BasePage() {
		PropertyConfigurator.configure("config/log4j.properties");
		PageFactory.initElements(new AjaxElementLocatorFactory(Chrome_Driver.driver, Seconds), this);
	}

	public void delay() {
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5000));
	}

	public void timeInterval() {
		try {
			Thread.sleep(500);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void timeInterval(int timeInSeconds) {
		try {
			Thread.sleep(timeInSeconds * 1000);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public void getHighlightElement(final WebElement element) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(Seconds));
			// Wait for search to complete
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver webDriver) {
					return element != null;
				}
			});
			((JavascriptExecutor) driver).executeScript("arguments[0].style.border='2px solid red'", element);
		} catch (Exception e) {
			logger.info("Fail to highlight the Element");
		}
	}

	public boolean isSelected(By by) {
		waitForParticularElement(by, Seconds);
		try {
			moveToElement(by);
			getHighlightElement(driver.findElement(by));
			return driver.findElement(by).isSelected();
		} catch (NoSuchElementException e) {
			logger.info(e.getMessage());
			return false;
		} catch (Exception e) {
			logger.info("Fail to check isSelected : " + by + " : " + e.getMessage());
			return false;
		}
	}

	public boolean isEnabled(By by) {
		waitForParticularElement(by, Seconds);
		try {
			getHighlightElement(driver.findElement(by));
			return driver.findElement(by).isEnabled();
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false,
					"Fail to find element to check isEnabled : " + by + " on page : " + driver.getCurrentUrl());
			return false;
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
	}

	public boolean waitFordetectPage(By by) {
		try {
			return driver.findElement(by).isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		} catch (Exception e) {
			logger.info("e : " + e.getMessage());
		}
		return false;
	}

	public boolean isDisplayed(By by) {
		try {
			moveToElement(by);
			getHighlightElement(driver.findElement(by));
			return driver.findElement(by).isDisplayed();
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDisplayedAllXPath(By... bys) {
		try {
			for (By by : bys) {
				moveToElement(by);
				getHighlightElement(driver.findElement(by));
				if (!driver.findElement(by).isDisplayed()) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean isDisplayed(WebElement webElement, By locator) {
		timeInterval(1);
		try {
			if ((webElement == null)) {
				return driver.findElement(locator).isDisplayed();
			}
			getHighlightElement(webElement.findElement(locator));
			return webElement.findElement(locator).isDisplayed();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return false;
	}

	public void click(By by) {
		waitForParticularElement(by, Seconds);
		int scroll = 0;
		do {
			try {
				moveToElement(by);
				driver.findElement(by).click();
				break;
			} catch (WebDriverException e) {
				try {
					getHighlightElement(driver.findElement(by));
					((JavascriptExecutor) driver)
							.executeScript("window.scrollTo(0," + driver.findElement(by).getLocation().y + ")");
					((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(by));
					break;
				} catch (Exception e2) {
					scroll++;
					Scroll(400);
				}
			}
		} while (scroll <= 2);

		if (scroll == 3) {
			Assert.assertTrue(false, "Fail to click on link : " + by + " on page : " + driver.getCurrentUrl());
		}
	}

	public void click(WebElement element) {
		int scroll = 0;
		build = new Actions(driver);

		do {
			try {
				build.moveToElement(element).build().perform();
				element.click();
				delay();
				break;
			} catch (NoSuchElementException e) {
				logger.info(e.getMessage());
				break;
			} catch (WebDriverException e) {
				scroll++;
				Scroll(0, 400);
			}
		} while (scroll <= 3);
		if (scroll == 3) {
			Assert.assertTrue(false, "Fail to click on link : " + element + " on page : " + driver.getCurrentUrl());
		}
	}

	public void sendKeys(By by, String value) {
		waitForParticularElementExist(by, Seconds);
		try {
			moveToElement(by);
			timeInterval();
			clear(by);
			driver.findElement(by).sendKeys(value);
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to send keys from text box : " + by + " on page : " + e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public void sendKeyswait(By by, String value, boolean withclear) {
		waitForParticularElement(by, Seconds);
		try {
			moveToElement(by);
			timeInterval();
			if (withclear) {
				clear(by);
			}
			for (int i = 0; i < value.length(); i++) {
				timeInterval();
				driver.findElement(by).sendKeys(String.valueOf(value.charAt(i)));
			}

		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to send keys from text box : " + by + " on page : " + e.getMessage());
		}
	}

	public void clear(By by) {
		try {
			getHighlightElement(driver.findElement(by));
			driver.findElement(by).clear();
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to clear value from text box : " + by + " on page : " + e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public String getText(By by) {
		waitForParticularElement(by, Seconds);
		try {
			getHighlightElement(driver.findElement(by));
			return driver.findElement(by).getText().trim();
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to get text value from : " + by + " on page : " + e.getMessage());

		} catch (Exception e) {
			logger.info(e.getMessage());

		}
		return null;
	}

	public List<String> getTextList(By by) {
		List<String> list = new ArrayList<>();
		waitForParticularElement(by, Seconds);
		int numOfelement1 = getNumberOfListOfElements(by);
		try {
			for (int i = 0; i < numOfelement1; i++) {
				getHighlightElement(driver.findElements(by).get(i));

				list.add(driver.findElements(by).get(i).getText());
			}
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to get text value from : " + by + " on page : " + e.getMessage());

		} catch (Exception e) {
			logger.info(e.getMessage());

		}
		return list;
	}

	public boolean isClickable(By by) {
		try {
			moveToElement(by);
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.elementToBeClickable(by));
			return true;
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
	}

	public boolean isClickable(WebElement element) {
		try {
			moveToElement(element);
			getHighlightElement(element);
			element.click();
			return true;
		} catch (Exception e) {
			logger.info(e.getMessage());
			return false;
		}
	}

	public String getTextRuntime(By by) {
		try {
			moveToElement(by);
			return driver.findElement(by).getText().trim();
		} catch (NoSuchElementException e) {
			logger.info(e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return "";
	}

	public String getAttribute(By by, String attribute) {
		try {
			moveToElement(by);
			getHighlightElement(driver.findElement(by));
			return driver.findElement(by).getAttribute(attribute).trim();
		} catch (NoSuchElementException e) {
			try {
				getHighlightElement(driver.findElement(by));
				WebElement element = driver.findElement(by);
				((JavascriptExecutor) driver).executeScript(
						"window.scrollTo(" + element.getLocation().x + "," + element.getLocation().y + ")");
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
				return element.getAttribute(attribute).trim();
			} catch (Exception e2) {
				logger.info(e.getMessage());
				Assert.assertTrue(false, "Fail to get text value from : " + by + " on page : " + e.getMessage());
			}
		}
		return null;
	}

	public void selectOptionFromDropDown(By by, String option) {

		Select select_list = null;
		try {
			timeInterval();
			moveToElement(by);
			timeInterval(1);
			select_list = new Select(driver.findElement(by));
			timeInterval(1);
			select_list.selectByVisibleText(option);
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false,
					"Fail to find drop down box to select option : " + by + " on page : " + e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public String getValueFromDropDown(By by) {
		timeInterval();
		waitForParticularElement(by, Seconds);
		Select select_list = null;
		try {
			moveToElement(by);
			timeInterval();
			select_list = new Select(driver.findElement(by));
			timeInterval();
			return select_list.getFirstSelectedOption().getText().trim();
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false,
					"Fail to find drop down box to select option : " + by + " on page : " + e.getMessage());
			logger.info("Fail to find drop down box to select option : " + by + " on page : " + e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}

	public List<String> getValuesFromDropDown(By by) {
		timeInterval(1);
		waitForParticularElement(by, Seconds);
		Select select_otions = null;
		List<String> options = new ArrayList<>();
		List<WebElement> options_Elmnt = null;
		try {
			moveToElement(by);
			timeInterval();
			select_otions = new Select(driver.findElement(by));
			options_Elmnt = select_otions.getOptions();
			for (WebElement element : options_Elmnt) {
				options.add(element.getText().trim());
			}
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to fetch value from drop down box on page : " + e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return options;
	}

	public void clickOnLinkText(String linkText) {
		By by;
		by = By.linkText(linkText);
		waitForParticularElement(by, Seconds);
		try {
			moveToElement(by);
			timeInterval();
			driver.findElement(by).click();
			timeInterval(2);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public boolean isLinkTextDisplay(String linkText) {
		timeInterval(2);
		return isDisplayed(By.linkText(linkText));
	}

	public boolean autoSuggestList(By textBox, String autoSuggestValue, By autoSuggestList, boolean verify)
			throws IOException {
		sendKeys(textBox, autoSuggestValue);
		waitForParticularElement(autoSuggestList, Seconds);
		if (isDisplayed(autoSuggestList)) {
			if (verify) {
				return true;
			} else {
				moveToElement(autoSuggestList);
				click(autoSuggestList);
				return true;
			}
		} else {
			driver.findElement(textBox).sendKeys("\t");
			return false;
		}
	}

	public int getNumberOfListOfElements(By by) {
		try {
			return driver.findElements(by).size();
		} catch (Exception e) {
			logger.info(e.getMessage());
			return 0;
		}
	}

	public String getPopupMsg_fromSystemPopup_andAccept() {
		timeInterval(2);
		String msg = "";
		try {
			timeInterval(1);
			msg = driver.switchTo().alert().getText();
			timeInterval(2);
			driver.switchTo().alert().accept();
			timeInterval(1);
			return msg;
		} catch (Exception e) {
			logger.info(e.getMessage());
			return "";
		}
	}

	public void waitForParticularElement(final By element, int waitForSeconds) {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(waitForSeconds));
			// Wait for search to complete
			wait.until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver webDriver) {
					try {
						logger.error("Waiting for element " + element.toString());
						return driver.findElement(element).isDisplayed();
					} catch (Exception e) {
						// logger.error();
					}
					return false;
				}
			});

		} catch (Exception e) {
			logger.error(e + " Fail to Wait for element for " + element.toString());
		}
	}

	public void waitForParticularElementExist(final By element, int waitForSeconds) {

		try {
			Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(waitForSeconds));
			// Wait for search to complete
			wait.until(ExpectedConditions.presenceOfElementLocated((element)));

		} catch (Exception e) {
			logger.error(e + " Element not present in DOM " + element.toString());
		}
	}

	public void waitUntilElementDisplays(final By element) {
		int i = 1;
		do {
			try {
				driver.findElement(element).isDisplayed();
				timeInterval(1);
				i++;
			} catch (NoSuchElementException e) {
				timeInterval();
				logger.info("waiting for element : " + element.toString() + " :  Waiting Time [ " + i + " ] out of "
						+ Seconds);
				break;
			} catch (StaleElementReferenceException ser) {
				timeInterval();
				driver.navigate().refresh();
				timeInterval(3);
			}
		} while (i <= 200);
	}

	public void moveToElement(By by) {
		build = new Actions(driver);
		try {
			getHighlightElement(driver.findElement(by));
			build.moveToElement(driver.findElement(by)).build().perform();
		} catch (NoSuchElementException e) {
			logger.info(e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public void moveToElement(WebElement element) {
		build = new Actions(driver);
		try {
			getHighlightElement(element);
			build.moveToElement(element).build().perform();
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to find Element: " + element + " on page : " + driver.getCurrentUrl());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public void moveScroll(By by, int value) {
		WebElement scrollArea = driver.findElement(by);
		jse.executeScript("arguments[0].scrollTop = arguments[1];", scrollArea, value);
	}

	public void Scroll(int xPoint, int yPoint) {
		jse.executeScript("window.scrollBy(" + xPoint + "," + yPoint + ")");
		timeInterval(2);
	}

	public void ScrollToElement(By by) {
		jse.executeScript("arguments[0].scrollIntoView(true);", driver.findElement(by));
		timeInterval(2);
	}

	public void ScrollToElement(WebElement ele) {
		jse.executeScript("arguments[0].scrollIntoView(true);", ele);
		timeInterval();
	}

	public void scrollToBottom() {
		timeInterval(2);
		((JavascriptExecutor) driver).executeScript(
				"window.scrollTo(0, Math.max(document.documentElement.scrollHeight,document.body.scrollHeight,document.documentElement.clientHeight))");
		timeInterval(2);
	}

	public void scrollToTop() {
		Boolean vertscrollStatus = (Boolean) jse
				.executeScript("return document.documentElement.scrollHeight>document.documentElement.clientHeight;");
		if (vertscrollStatus) {
			driver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.HOME);
			timeInterval(2);
		} else {
			jse.executeScript("scrollBy(0, -1000)");
		}
	}

	public boolean verifyAutoSuggest(By by, String option) {
		List<WebElement> allElements = driver.findElements(by);
		for (int i = 1; i <= allElements.size(); i++) {
			String value = getTextRuntime(By.xpath(".//span[@class='tt-suggestions']/div[" + i + "]"));
			if (value.equalsIgnoreCase(option)) {
				return true;
			}
		}
		return false;
	}

	public String gotoSelectSmallOrGreaterDate(String setDateFormate, boolean pastdate, int days) {
		timeInterval(1);
		SimpleDateFormat formattedDate = new SimpleDateFormat(setDateFormate);
		Calendar c = Calendar.getInstance();
		if (pastdate) {
			c.add(Calendar.DATE, -days); // number of days to add (FOR PAST
		} else {
			c.add(Calendar.DATE, days); // number of days to add (FOR FUTURE
		}

		return formattedDate.format(c.getTime());
	}

	public void switchTab() {
		timeInterval(3);
		ArrayList<String> tabs2 = new ArrayList<>(driver.getWindowHandles());
		driver.switchTo().window(tabs2.get(1));
		timeInterval(3);
	}

	public void backToTab() {
		ArrayList<String> tabs2 = new ArrayList<>(driver.getWindowHandles());
		driver.close();
		timeInterval(3);
		driver.switchTo().window(tabs2.get(0));

	}

	public void Scroll(int value) {
		jse.executeScript("window.scrollBy(0," + value + ")");
		timeInterval(2);
	}

	public String getCSSValue(By by, String attributeName) {
		waitForParticularElement(by, Seconds);
		try {
			getHighlightElement(driver.findElement(by));
			return driver.findElement(by).getCssValue(attributeName).trim();
		} catch (NoSuchElementException e) {
			Assert.assertTrue(false, "Fail to get CSS value from : " + by + " on page : " + e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return null;
	}

	public void dragAndDrop(By source, By target) {
		build = new Actions(driver);
		try {
			moveToElement(source);
			moveToElement(target);
			build.dragAndDrop(driver.findElement(source), driver.findElement(target)).build().perform();
		} catch (Exception e) {
			Assert.assertTrue(false,
					"Fail to drag and drop from : " + source + " to : " + target + " on page: " + e.getMessage());
		}

	}

	public void dragAndDropByPosition(By source, int x, int y) {
		build = new Actions(driver);
		try {
			moveToElement(source);
			build.dragAndDropBy(driver.findElement(source), x, y).build().perform();
		} catch (Exception e) {
			Assert.assertTrue(false, "Fail to drag and drop from : " + source + " to : " + x + " and " + y
					+ " on page: " + e.getMessage());
		}

	}

	public void dragAndDropWithScrollToElement(WebElement source, WebElement target) {
		build = new Actions(driver);
		try {
			ScrollToElement(target);
			getHighlightElement(source);
			getHighlightElement(target);
			build.clickAndHold(source).dragAndDrop(source, target).build().perform();
		} catch (Exception e) {
			logger.info("Fail to drag and drop from : " + source + " to : " + target + " on page: " + e.getMessage());
		}
	}

	public void waitForReady() {
		try {
			wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(new Function<WebDriver, Boolean>() {
				int cnt = 1;

				@Override
				public Boolean apply(WebDriver driver) {
					Boolean javaScriptIsComplete = ((JavascriptExecutor) driver)
							.executeScript("return document.readyState").equals("complete");
					logger.info(cnt++ + " Is waiting for js ready ready = " + javaScriptIsComplete);
					return javaScriptIsComplete;
				}
			});
		} catch (Exception e) {
			logger.error(e + "Fail to wait for Ajax Load");
		}
	}

	public void waitForReady(final String ajaxUrl) {

		try {

			initializeMapAndSetEvent();

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(new Function<WebDriver, Boolean>() {
				int cnt = 1;

				@Override
				public Boolean apply(WebDriver driver) {

					boolean isSpecifiedActionComplete = isActionCompleted(ajaxUrl);
					boolean ajaxIsAllComplete = isAllActionCompleted();
					boolean javaScriptIsComplete = isDocumentReady();

					logger.info(cnt++ + " Waiting for all ajax to complete :IsSpecifiedActionComplete =  "
							+ isSpecifiedActionComplete + ":Is Allajaxcomplte = " + ajaxIsAllComplete
							+ ":Is Document ready = " + javaScriptIsComplete);

					if (isSpecifiedActionComplete || (ajaxIsAllComplete && javaScriptIsComplete)) {
						resetMap();
						return true;
					} else {
						return false;
					}
				}
			});

		} catch (Exception e) {
			logger.error(e + "Fail to wait for Ajax Load");
		}

	}

	private void initializeMapAndSetEvent() {
		((JavascriptExecutor) driver).executeScript("actionMap = {}; var url=null; ");

		((JavascriptExecutor) driver).executeScript(
				"$j( document ).ajaxComplete(function( event, xhr, settings ) { url=settings.url.split('?')[0]; actionMap[url]=url; });");

	}

	private void resetMap() {
		((JavascriptExecutor) driver).executeScript("actionMap={}; url=null;");
	}

	private boolean isActionCompleted(String ajaxUrl) {
		boolean isGivenActionFound = (boolean) ((JavascriptExecutor) driver)
				.executeScript("return '" + ajaxUrl + "' in actionMap");
		if (isGivenActionFound) {
			logger.info("Given action " + ajaxUrl + " for Ajaxwait has been completed.");
			return true;
		} else {
			return false;
		}
	}

	private boolean isAllActionCompleted() {
		return (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0");
	}

	private boolean isDocumentReady() {
		return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
	}

	// Wait for element invisible
	public void waitForInvisibilityOfElement(By by) {
		wait = new WebDriverWait(driver, Duration.ofSeconds(Seconds));
		logger.info("Element is invisible ");
		wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
	}
}