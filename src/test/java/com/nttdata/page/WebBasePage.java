package com.nttdata.page;

import static org.awaitility.Awaitility.await;
import static org.openqa.selenium.By.tagName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.gson.Gson;
import com.nttdata.utils.driver.DriverFactory;
import com.nttdata.utils.driver.LocalBrowserFactory;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.SupportsContextSwitching;

public class WebBasePage {

	protected static final int DEFAULT_WAIT_TIMEOUT = 30;
	protected static final int POLLING = 1;
	protected static final int MAX_TRIES = 10;

	// Maximizar ventana
	public static void maximizarVentana() {
		DriverFactory.getDriver().manage().window().maximize();
	}

	// Valida el titulo de la pagina web
	public static void validarTituloWeb(String titulo) {
		DriverFactory.getDriver().getTitle();
	}

	protected static void waitFor(int segundos) {
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException ignored) {

		}
	}

	protected Logger log = LogManager.getLogger(this.getClass());

	protected WebDriver driver;

	protected final WebDriverWait wait;

	private By overlaybusyicon = By.xpath("//span[contains(@class,'MuiCircularProgress')]");

	protected WebBasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT), Duration.ofSeconds(POLLING));
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, DEFAULT_WAIT_TIMEOUT), this);
	}

	protected WebBasePage(WebDriver driver, int timeOutSec) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutSec), Duration.ofSeconds(POLLING));
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, DEFAULT_WAIT_TIMEOUT), this);
	}

	protected WebBasePage(WebDriver driver, int timeOutSec, int pollingSec) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeOutSec), Duration.ofSeconds(pollingSec));
		PageFactory.initElements(new AjaxElementLocatorFactory(driver, DEFAULT_WAIT_TIMEOUT), this);
	}

	public File getFileDownloaded(String fileName) throws Exception {
		File result = null;
		String lab = System.getProperty("labExecution");
		switch (lab) {
		case "local":
			result = getFileDownloadedLocal(fileName);
			break;
		case "browserstack":
			result = getFileDownloadedBrowserStack(fileName);
			break;
		}
		return result;
	}

	private File getFileDownloadedLocal(String fileName) {
		final File folderDownload = new File(LocalBrowserFactory.getDownloadsPath());
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(fileName) && !name.endsWith(".crdownload");
			}
		};
		Awaitility.await().atMost(Duration.ofSeconds(15)).until(() -> {
			return folderDownload.listFiles(filter).length > 0;
		});
		return folderDownload.listFiles(filter)[0];
	}

	private File getFileDownloadedBrowserStack(String fileName) throws Exception {
		File folderDestino = new File(LocalBrowserFactory.getDownloadsPath());
		if (DriverFactory.getDriver().getClass() == RemoteWebDriver.class) {
			String fileNameBS = getFileNameDescargadoEnBrowserStack();
			descargarArchivoDesdeBS(folderDestino.getAbsolutePath(), fileNameBS);
			final File fileEnDownloads = new File(folderDestino.getAbsoluteFile() + "/" + fileNameBS);
			return fileEnDownloads;
		}

		return null;
	}

	private String getFileNameDescargadoEnBrowserStack() {
		JavascriptExecutor jse = (JavascriptExecutor) this.getDriver();
		String jsonObject = new Gson()
				.toJson(jse.executeScript("browserstack_executor: {\"action\": \"getFileProperties\"}"));
		JSONObject jsonObjectParsed = new JSONObject(jsonObject);
		return jsonObjectParsed.getString("file_name");
	}

	public File descargarArchivoDesdeBS(String folderDestino, String filnameInRemote) throws Exception {
		JavascriptExecutor jse = (JavascriptExecutor) this.getDriver();
		String base64EncodedFile = (String) jse.executeScript(
				"browserstack_executor: {\"action\": \"getFileContent\", \"arguments\": {\"fileName\": \""
						+ filnameInRemote + "\"}}");
		new File(folderDestino).mkdirs();
		// decode the content to Base64
		byte[] data = Base64.getDecoder().decode(base64EncodedFile);
		File fileTemp = new File(folderDestino + "/" + filnameInRemote);
		try (OutputStream stream = new FileOutputStream(fileTemp);) {
			stream.write(data);
		}

		if (!fileTemp.exists()) {
			throw new Exception(
					"No se pudo descargar archvio desde BrowserStack a Local en " + fileTemp.getAbsolutePath());
		}
		return fileTemp;
	}

	public void ClearBrowserCache() {
		driver.manage().deleteAllCookies();
	}

	protected void click(By selector) throws Exception {
		waitUntilElementIsClickeable(selector);
		WebElement element = getDriver().findElement(selector);
		element.click();
	}

	protected void click(WebElement webElement) {
		waitUntilElementIsClickeable(webElement);
		webElement.click();
	}

	protected void doubleClick(WebElement webElement) {
		waitUntilElementIsClickeable(webElement);
		new Actions(this.getDriver()).doubleClick(webElement).perform();
	}

	protected void clickNoHabilitado(WebElement webElement) {
		webElement.click();
	}

	protected void closeCurrentTab() {
		driver.close();
	}

	protected boolean estaDescargadoArchivoNombreEnBrowserStack(String nombreArchivo) throws Exception {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		boolean existFile = (Boolean) jse
				.executeScript("browserstack_executor: {\"action\": \"fileExists\", \"arguments\": {\"fileName\":\""
						+ nombreArchivo + "\"}}");
		waitFor(3);
		return existFile;
	}

	protected boolean estaDescargadoInformeEnBrowserStack() throws Exception {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		boolean existFile = (boolean) jse.executeScript("browserstack_executor: {\"action\": \"fileExists\"}");
		waitFor(5);
		return existFile;
	}

	protected boolean estaDescargadoInformePDFEnBrowserStack(String nombreArchivo) throws Exception {
		JavascriptExecutor jse = (JavascriptExecutor) getDriver();
		boolean existFile = (Boolean) jse
				.executeScript("browserstack_executor: {\"action\": \"fileExists\", \"arguments\": {\"fileName\":\""
						+ nombreArchivo + ".pdf\"}}");
		return existFile;
	}

	protected String getAttribute(WebElement webElement, String attribute) {
		waitUntilElementIsVisible(webElement);
		return webElement.getAttribute(attribute);
	}

	protected String getAttribute(By selector, String attribute) {
		WebElement element = getDriver().findElement(selector);
		waitUntilElementIsVisible(element);
		return element.getAttribute(attribute);
	}

	protected WebDriver getDriver() {
		return driver;
	}

	protected WebElement getShadowRoot(WebElement root) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		WebElement shadowRoot = (WebElement) js.executeScript("return arguments[0].shadowRoot", root);
		return shadowRoot.findElement(tagName("embed"));
	}

	protected String getText(WebElement webElement) {
		waitUntilElementIsVisible(webElement);
		return webElement.getText();
	}

	protected String getText(By by) {
		WebElement webElement = getDriver().findElement(by);
		waitUntilElementIsVisible(webElement);
		return webElement.getText();
	}

	protected String getText(WebElement webElement, int timeOut) {
		waitUntilElementIsVisibleNonThrow(webElement, timeOut);
		return webElement.getText();
	}

	protected boolean isClickable(WebElement element, int timeOutSeconds) {
		try {
			wait.withTimeout(Duration.ofSeconds(timeOutSeconds))
					.until(ExpectedConditions.elementToBeClickable(element));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void isVisible(By byElement, int timeOutSeconds) {
		try {
			this.wait.withTimeout(Duration.ofSeconds(timeOutSeconds))
					.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(byElement));
		} catch (Exception e) {
		}
	}

	protected boolean isInvisible(WebElement element) {
		try {
			return !element.isDisplayed();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isVisible(By MobileElement) {
		try {
			return this.getDriver().findElement(MobileElement).isDisplayed();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isSelected(By MobileElement) {
		try {
			return this.getDriver().findElement(MobileElement).isSelected();
		} catch (NoSuchElementException | StaleElementReferenceException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isVisible(WebElement webElement) {
		try {
			return isVisible(webElement, DEFAULT_WAIT_TIMEOUT);
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isVisible(WebElement webElement, int timeOutSeconds) {
		try {
			this.wait.withTimeout(Duration.ofSeconds(timeOutSeconds))
					.until(ExpectedConditions.visibilityOf(webElement));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isVisibleElement(WebElement webElement) {
		waitUntilElementIsVisibleNonThrow(webElement);
		return isVisible(webElement);
	}

	protected boolean isVisibleElement(WebElement webElement, int timeOut) {
		waitUntilElementIsVisibleNonThrow(webElement, timeOut);
		return isVisible(webElement);
	}

	protected boolean isVisibleElement(String xpath, int timeOut) {
		WebElement webElement = getDriver().findElement(By.xpath(xpath));
		waitUntilElementIsVisibleNonThrow(webElement, timeOut);
		return isVisible(webElement);
	}

	protected boolean isEnabled(WebElement webElement) {
		try {
			return webElement.isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isEnable(By by) {
		WebElement webElement = getDriver().findElement(by);
		try {
			return webElement.isEnabled();
		} catch (Exception e) {
			return false;
		}
	}

	protected void moveToElement(WebElement element) throws Exception {
		waitUntilElementIsVisible(element);
		new Actions(this.getDriver()).moveToElement(element).perform();
	}

	protected void moveToElement(By element) throws Exception {
		waitUntilElementIsVisible(element);
		new Actions(this.getDriver()).moveToElement(driver.findElement(element)).perform();
	}

	protected void returnToMainFrame() {
		driver.switchTo().defaultContent();
	}

	protected void returnToParentFrame() {
		driver.switchTo().parentFrame();
	}

	protected void scrollDown() {
		Dimension size = getDriver().manage().window().getSize();
		int startPoint = (int) ((double) size.getHeight() * 0.7D);
		int endPoint = (int) ((double) size.getHeight() * 0.4D);
		((JavascriptExecutor) driver).executeScript("scroll(" + startPoint + "," + endPoint + ")");
	}

	protected void scrollDown2() {
		Dimension size = this.getDriver().manage().window().getSize();
		int startPoint = (int) ((double) size.getHeight() * 0.11D);
		int endPoint = (int) ((double) size.getHeight() * 0.7D);
		((JavascriptExecutor) driver).executeScript("scroll(" + startPoint + "," + endPoint + ")");
	}

	protected void scrollDownBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollBy(0,document.body.scrollHeight)");
	}

	protected void scrollDownToElement(WebElement element) {
		waitUntilElementIsVisible(element);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
	}

	protected void scrollDownToElement(By element) throws Exception {
		WebElement webElement = getDriver().findElement(element);
		waitUntilElementIsVisible(webElement);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", webElement);
	}

	protected void scrollUp() {
		try {
			((JavascriptExecutor) driver).executeScript("scroll(0,-500)");
			((JavascriptExecutor) driver).executeScript("scroll(0,-500)");
		} catch (Exception ignored) {

		}
	}

	protected void sendKeys(WebElement webElement, String text) {
		waitUntilElementIsVisible(webElement);
		webElement.clear();
		webElement.sendKeys(text);
	}

	protected void sendKeysValorSinClear(WebElement webElement, String text) {
		waitUntilElementIsVisible(webElement);
		webElement.sendKeys(text);
	}

	protected void sendKeysByKeyboard(String text) {
		Actions actions = new Actions(driver);
		String key;
		for (int i = 0; i < text.length(); ++i) {
			key = String.valueOf(text.charAt(i));
			actions.keyDown(key).keyUp(key);
		}
		actions.setActiveKeyboard("keyboard");
		actions.perform();
	}

	protected void sendKeysBackspace(By by) {
		WebElement webElement = getDriver().findElement(by);
		waitUntilElementIsVisible(webElement);
		webElement.sendKeys(Keys.BACK_SPACE);
	}

	protected void sendKeys(WebElement webElement, String text, Keys keys) {
		waitUntilElementIsVisible(webElement);
		if (webElement.isEnabled()) {
			webElement.sendKeys(text, keys);
		}
	}

	protected void sendKeys(By by, String text, Keys keys) {
		WebElement webElement = getDriver().findElement(by);
		waitUntilElementIsVisible(webElement);
		if (webElement.isEnabled()) {
			webElement.sendKeys(text, keys);
		}
	}

	protected void sendKeys(By by, String text) {
		WebElement webElement = getDriver().findElement(by);
		waitUntilElementIsVisible(webElement);
		if (webElement.isEnabled()) {
			webElement.sendKeys(text);
		}
	}

	protected void sendKeysValue(WebElement webElement, String text) {
		waitUntilElementIsVisible(webElement);
		// Usar JavaScript para establecer el valor
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("arguments[0].value='" + text + "';", webElement);
	}

	protected void sendKeysValue(By by, String text) {
		WebElement webElement = getDriver().findElement(by);
		sendKeysValue(webElement, text);
	}

	protected void sendKeysConClear(By by, String text) {
		WebElement webElement = getDriver().findElement(by);
		waitUntilElementIsVisible(webElement);
		if (webElement.isEnabled()) {
			webElement.clear();
			webElement.sendKeys(text);
		}
	}

	protected void sendKeys(WebElement webElement, String attribute, String text) {
		waitUntilElementIsVisible(webElement);
		webElement.sendKeys(attribute, text);
	}

	protected void sendKeysControlSinWebElement(String text) {
		Actions act = new Actions(driver);
		act.keyDown(Keys.CONTROL);
		act.sendKeys(text);
		act.keyUp(Keys.CONTROL);
		act.build().perform();
	}

	protected void sendKeysESC() {
		Actions act = new Actions(driver);
		act.sendKeys(Keys.ESCAPE);
		act.build().perform();
	}

	public void sendKeysTAB() {
		Actions act = new Actions(driver);
		act.sendKeys(Keys.TAB);
		act.build().perform();
	}

	public void switchNewWindow() {
		for (String winHandle : driver.getWindowHandles()) {
			driver.switchTo().window(winHandle);
		}
	}

	public void createNewTAB() {
		driver.switchTo().newWindow(WindowType.TAB);
	}

	public void switchToDefaultTab() {
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(tabs.get(0));
	}

	public void switchToIframe(WebElement iframe) throws Exception {
		waitUntilElementIsVisible(iframe);
		driver.switchTo().frame(iframe);
	}

	public void switchToIframe(By by) throws Exception {
		WebElement webElement = getDriver().findElement(by);
		waitUntilElementIsVisible(webElement);
		driver.switchTo().frame(webElement);
	}

	public void switchToParentIframe() throws Exception {
		driver.switchTo().parentFrame();
	}

	public void switchToLastTab() {
		for (String windowsHandle : this.driver.getWindowHandles()) {
			driver.switchTo().window(windowsHandle);
		}
	}

	public void refreshBrowser() {
		driver.navigate().refresh();
		waitFor(5);
	}

	public byte[] takeScreenshot() {
		return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
	}

	// Valida Texto Web
	protected boolean validarTextoWeb(String mensaje, boolean ignoreEspacios) throws Exception {
		if (ignoreEspacios) {
			mensaje = "//*[normalize-space(text()) = '" + mensaje + "']";
		} else {
			mensaje = "//*[contains(text(),'" + mensaje + "')]";
		}
		WebElement element;
		waitUntilElementIsVisibleNonThrow(By.xpath(mensaje), 10);
		moveToElement(By.xpath(mensaje));
		element = ((WebDriver) DriverFactory.getDriver()).findElement(By.xpath(mensaje));
		return isVisible(element);
	}

	protected boolean validarTextoWeb(String mensaje) throws Exception {
		return validarTextoWeb(mensaje, false);
	}

	// click boton texto
	protected void clickBotonText(String mensaje) {
		mensaje = "//button//*[contains(text(),'" + mensaje + "')]";
		WebElement element;
		waitUntilElementIsVisibleNonThrow(By.xpath(mensaje), 20);
		element = ((WebDriver) DriverFactory.getDriver()).findElement(By.xpath(mensaje));
		element.click();
	}

	protected void scrollDownToElement(String mensaje) {
		mensaje = "//*[contains(text(),'" + mensaje + "')]";
		WebElement element;
		waitUntilElementIsVisibleNonThrow(By.xpath(mensaje), 20);
		element = ((WebDriver) DriverFactory.getDriver()).findElement(By.xpath(mensaje));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
	}

	protected void scrollDownToElementXpath(String xpath, String mensaje) {
		mensaje = xpath + "//*[contains(text(),'" + mensaje + "')]";
		WebElement element;
		waitUntilElementIsVisibleNonThrow(By.xpath(mensaje), 20);
		element = ((WebDriver) DriverFactory.getDriver()).findElement(By.xpath(mensaje));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
	}

	protected void waitForElementToAppear(WebElement element) {
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	protected void waitForElementToDisappear(WebElement element) {
		wait.until(ExpectedConditions.invisibilityOf(element));
	}

	protected void waitForTextToDisappear(WebElement element, String text) {
		wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElement(element, text)));
	}

	public void waitUnitlPageLoadedWithBussyIcon() {
		try {
			waitFor(1);
			new WebDriverWait(driver, Duration.ofSeconds(120), Duration.ofSeconds(POLLING))
					.until(ExpectedConditions.invisibilityOfElementLocated(overlaybusyicon));
		} catch (Exception e) {
			System.err.println("No se pudo esperar por que desaparesca el Bussy Icon");
		}
	}

	protected void waitUntilElementInterative(By wElement) throws Exception {
		try {
			this.wait.until(ExpectedConditions.elementToBeClickable(wElement));
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

	protected void waitUntilElementInterative(WebElement wElement) throws Exception {
		try {
			this.wait.until(ExpectedConditions.elementToBeClickable(wElement));
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

	protected void waitUntilElementIsClickeable(By element) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (ConditionTimeoutException e) {
			throw new ConditionTimeoutException(String.format("El elemento: %s no es clickeable", element));
		}
	}

	protected void waitUntilElementIsClickeable(WebElement element) {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (ConditionTimeoutException e) {
			throw new ConditionTimeoutException(String.format("El elemento: %s no es clickeable", element));
		}
	}

	protected void waitUntilElementIsInvisible(WebElement wElement) throws Exception {
		waitUntilElementIsInvisible(wElement, DEFAULT_WAIT_TIMEOUT);
	}

	protected void waitUntilElementIsInvisible(WebElement wElement, int timeOut) throws Exception {
		try {
			this.wait.withTimeout(Duration.ofSeconds(timeOut)).until(ExpectedConditions.invisibilityOf(wElement));
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

	protected void waitUntilElementIsInVisible(WebElement element) {
		try {
			await().atMost(Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT)).until(() -> isInvisible(element));
		} catch (ConditionTimeoutException e) {
			throw new ConditionTimeoutException(
					String.format("El elemento no desaparece despues de 30 segundos\nElemento: %s", element));
		}
	}

	protected void waitUntilElementIsInVisibleNonThrow(WebElement element, int WAIT_TIMEOUT) {
		try {
			await().atMost(Duration.ofSeconds(WAIT_TIMEOUT)).until(() -> isInvisible(element));
		} catch (ConditionTimeoutException e) {
		}
	}

	protected void waitUntilElementIsVisible(By element) throws Exception {
		try {
			this.wait.withTimeout(Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT))
					.until(ExpectedConditions.visibilityOfElementLocated(element));
		} catch (Exception e) {
			throw new ConditionTimeoutException(String.format(
					"No se encuentra el elemento despues de " + DEFAULT_WAIT_TIMEOUT + " segundos\nElemento: %s",
					element));
		}
	}

	protected void waitUntilElementIsVisible(WebElement element, int WAIT_TIMEOUT) {
		try {
			this.wait.withTimeout(Duration.ofSeconds(WAIT_TIMEOUT)).until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {
			throw new ConditionTimeoutException(String.format(
					"No se encuentra el elemento despues de " + WAIT_TIMEOUT + " segundos\nElemento: %s", element));
		}
	}

	protected void waitUntilElementIsVisible(WebElement element) {
		try {
			this.wait.withTimeout(Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT))
					.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {
			throw new ConditionTimeoutException(String.format(
					"No se encuentra el elemento despues de " + DEFAULT_WAIT_TIMEOUT + " segundos\nElemento: %s",
					element));
		}
	}

	protected void waitUntilElementIsVisibleNonThrow(WebElement element) {
		try {
			this.wait.withTimeout(Duration.ofSeconds(DEFAULT_WAIT_TIMEOUT))
					.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {

		}
	}

	protected void waitUntilElementIsVisibleNonThrow(By element, int WAIT_TIMEOUT) {
		try {
			this.wait.withTimeout(Duration.ofSeconds(WAIT_TIMEOUT))
					.until(ExpectedConditions.visibilityOfElementLocated(element));
		} catch (Exception e) {

		}
	}

	protected void waitUntilElementIsVisibleNonThrow(WebElement element, int WAIT_TIMEOUT) {
		try {
			this.wait.withTimeout(Duration.ofSeconds(WAIT_TIMEOUT)).until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {

		}
	}

	public boolean isVisibleOverlaybusyicon() {
		waitUntilElementIsVisibleNonThrow(overlaybusyicon, 10);
		return isVisible(overlaybusyicon);
	}

	public String getCurrentURL() {
		return driver.getCurrentUrl();
	}

	public void navigateTo(String url) {
		driver.get(url);
	}

	public void closeAndSwitchLastTab() {
		ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());
		if (tabs.size() > 1) {
			driver.close();
			switchToLastTab();
		}

	}

	public void clickAndHold(By selector) throws Exception {
		WebElement element = getDriver().findElement(selector);
		moveToElement(By.xpath("(//app-root//div)[last()]"));
		Actions act = new Actions(driver);
		act.clickAndHold(element).build().perform();
	}

	public void dragAndDrop(WebElement from, WebElement to) {
		Actions act = new Actions(driver);
		act.dragAndDrop(from, to).build().perform();
	}

	public void dragAndDrop(WebElement from, int x, int y) {
		Actions act = new Actions(driver);
		act.dragAndDropBy(from, x, y).build().perform();
	}

	public void slideHorizont(WebElement elementToSlide) {
		Actions act = new Actions(driver);
		int xOffset = elementToSlide.getSize().getWidth() / 2;
		act.dragAndDropBy(elementToSlide, -xOffset, 0).build().perform();
	}

	public void slideHorizont(By elementToSlide) {
		WebElement webElement = getDriver().findElement(elementToSlide);
		Actions act = new Actions(driver);
		int xOffset = webElement.getSize().getHeight() / 2;
		act.dragAndDropBy(webElement, -xOffset, 0).build().perform();
	}

	public void slideVertical(By elementToSlide) {
		WebElement webElement = getDriver().findElement(elementToSlide);
		Actions act = new Actions(driver);
		int yOffset = webElement.getSize().getHeight() / 2;
		act.dragAndDropBy(webElement, 0, yOffset).build().perform();
	}

	public void slideVertical(WebElement elementToSlide) {
		Actions act = new Actions(driver);
		int yOffset = elementToSlide.getSize().getHeight() / 2;
		act.dragAndDropBy(elementToSlide, 0, yOffset).build().perform();
	}

	protected int getCount(By selector) throws Exception {
		List<WebElement> elements = driver.findElements(selector);
		int elementsCount = elements.size();
		return elementsCount;
	}

	protected Point getPosition(WebElement webElement) {
		return webElement.getLocation();
	}

	protected void moveSlide(WebElement priceSlider, int xCoord) {
		Actions builder = new Actions(driver);
		builder.moveToElement(priceSlider).dragAndDropBy(priceSlider, xCoord, 0).build().perform();
	}

	public void switchToContext(String contextname) throws Exception {
		waitForContext(contextname, 10);
		Set<String> contextNames = ((SupportsContextSwitching) driver).getContextHandles();
		for (String context : contextNames) {
			if (context.contains(contextname)) {
				((SupportsContextSwitching) driver).context(context);
				return;
			}
		}
	}

	public void waitForContext(String contextNameToFind, int timeOut) {
		Set<String> contextNames;
		int tries = 0;
		do {
			waitFor(1);
			contextNames = ((SupportsContextSwitching) driver).getContextHandles();
			tries++;
		} while (!contextNames.contains(contextNameToFind) && tries < timeOut);
	}

	public boolean isAndroidDevice() {
		return driver instanceof AndroidDriver;
	}

	public boolean isIOSDevice() {
		return driver instanceof IOSDriver;
	}

	public String getWebViewIOS() {
		return ((SupportsContextSwitching) driver).getContext();
	}

	public void hideKeyboard() {
		try {
			if (isAndroidDevice() && ((AndroidDriver) driver).isKeyboardShown()) {
				((AndroidDriver) driver).hideKeyboard();
			} else if (isIOSDevice() && ((IOSDriver) driver).isKeyboardShown()) {
				((IOSDriver) driver).hideKeyboard();
			}
		} catch (Exception e) {

		}
	}

	public void rotarHorizontal() {
		if (isAndroidDevice()) {
			System.out.println(((AndroidDriver) driver).getOrientation());
			((AndroidDriver) driver).rotate(ScreenOrientation.LANDSCAPE);
		} else if (isIOSDevice()) {
			System.out.println(((IOSDriver) driver).getOrientation());
			((IOSDriver) driver).rotate(ScreenOrientation.LANDSCAPE);
		}
	}

	public void rotarVertical() {
		if (isAndroidDevice()) {
			System.out.println(((AndroidDriver) driver).getOrientation());
			((AndroidDriver) driver).rotate(ScreenOrientation.PORTRAIT);
		} else if (isIOSDevice()) {
			System.out.println(((IOSDriver) driver).getOrientation());
			((IOSDriver) driver).rotate(ScreenOrientation.PORTRAIT);
		}
	}

	public int getBrowserSizeHeight() {
		return driver.manage().window().getSize().getHeight();
	}

	public int getBrowserSizeWidth() {
		return driver.manage().window().getSize().getWidth();
	}

	protected void select(WebElement webElement, String type, String data) {
		Select select = new Select(webElement);
		waitUntilElementIsVisible(webElement);
		switch (type) {
		case "text":
			select.selectByVisibleText(data);
			break;
		case "index":
			select.selectByIndex(Integer.parseInt(data));
			break;
		case "value":
			select.selectByValue(data);
			break;
		}
	}

	protected void select(By by, String type, String data) {
		WebElement webElement = getDriver().findElement(by);
		Select select = new Select(webElement);
		waitUntilElementIsVisible(webElement);
		switch (type) {
		case "text":
			select.selectByVisibleText(data);
			break;
		case "index":
			select.selectByIndex(Integer.parseInt(data));
			break;
		case "value":
			select.selectByValue(data);
			break;
		}
	}

	protected void calendario(By btnAbrirCalendario, String data) {
		int tries = 0;
		driver.findElement(btnAbrirCalendario).click();
		By btnYearCabecera = By.xpath("//mat-calendar-header//button[contains(@aria-label, 'year')]");
		JavascriptExecutor jse = (JavascriptExecutor) this.getDriver();
		By btnAnteriorCabecera = By.xpath("//mat-calendar-header//button[contains(@aria-label, 'Previous')]");
		By btnSiguienteCabecera = By.xpath("//mat-calendar-header//button[contains(@aria-label, 'Next')]");
		driver.findElement(btnYearCabecera).click();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fecha = null;
		try {
			fecha = formatoFecha.parse(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
		String year = simpleDateFormat.format(fecha);

		By yearASeleccionar = By.xpath("//mat-multi-year-view//td/div[normalize-space() = '" + year + "']");
		while (!isVisible(yearASeleccionar) && tries < 10) {
			if (year.compareTo(simpleDateFormat.format(new Date())) > 0) {
				jse.executeScript("arguments[0].click()", getDriver().findElement(btnSiguienteCabecera));
			} else {
				jse.executeScript("arguments[0].click()", getDriver().findElement(btnAnteriorCabecera));
			}
			tries++;
		}
		driver.findElement(yearASeleccionar).click();

		simpleDateFormat = new SimpleDateFormat("MM");
		String[] meses = { "ENE.", "FEB.", "MAR.", "ABR.", "MAY.", "JUN.", "JUL.", "AGO.", "SEP.", "OCT.", "NOV.",
				"DIC." };
		String mes = meses[Integer.parseInt(simpleDateFormat.format(fecha)) - 1];
		By btnMes = By.xpath("//mat-calendar//td/div[normalize-space()='" + mes + "']");
		driver.findElement(btnMes).click();

		String dia = new SimpleDateFormat("dd").format(fecha);
		By btnDia = By.xpath("//mat-month-view//td/div[normalize-space()='" + Integer.parseInt(dia) + "']");
		driver.findElement(btnDia).click();
	}

	public String getTextFromTooltip() throws Exception {
		WebElement element = driver.findElement(By.xpath("//div[contains(@class, 'app-tooltip-detail')]"));
		return (String) ((JavascriptExecutor) getDriver()).executeScript("return arguments[0].innerHTML", element);
	}

}
