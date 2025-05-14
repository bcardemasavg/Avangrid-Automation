package com.nttdata.page;

import io.cucumber.datatable.DataTable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class LoginPage extends WebBasePage {

	@FindBy(xpath = "//input[@aria-labelledby='EmailorUsername']")
	private WebElement inputEmail;

	@FindBy(xpath = "//input[@aria-labelledby='Password']")
	private WebElement inputPassword;

	@FindBy(xpath = "(//div[@class='row bottom-header-content-wrapper justify-content-between align-items-center m-auto w-100']//a[text()='Sign in / Register'])[1]")
	private WebElement buttonSingsInRegister;

	@FindBy(xpath = "//div[@class='button-holder ']//button[@type='submit']")
	private WebElement buttonSingsIn;

	@FindBy(xpath = "//div[@class='alert alert-dismissible alert-danger']")
	private WebElement alertErrorLogin;

	@FindBy(xpath = "//button[text()=' My Account ']")
	private WebElement buttonMyAccount;

	@FindBy(xpath = "(//button[@class='account-number-cell d-flex flex-column justify-content-end agr-link button-link ng-star-inserted'])[1]")
	private WebElement buttonAccount;


	public LoginPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public void sendKeysInputUser(String user) {
		sendKeys(inputEmail, user);
	}

	public void sendKeysInputPassword(String password) {
		sendKeys(inputPassword, password);
	}

	public void clickButtonSingsInRegister() {
		click(buttonSingsInRegister);
	}

	public void clickButtonbuttonSingsIn() {
		click(buttonSingsIn);
	}

	public void ValidateLoginErrorText() throws Exception {
		validarTextoWeb("Error:The information you have entered does not match our records. Please try again or click below to reset your password or to receive your UserID.",true);
	}

	public void isVisibleButtonAccount() {
		isVisible(buttonMyAccount, 60);
	}

	public void validateMenu(DataTable menu) {
		List<String> data = menu.asList();
		click(buttonAccount);
		System.out.println(data.get(0));
		isVisible(By.id("//button[text()=' "+data.get(0)+" ']"),20);
	}

	/*public void clickButtonIniciarSesionCorporativa() {
		click(buttonIniciarSesionCorporativa);
	}

	public void sendKeysInputCorreoCorporativo(String usuario) {
		sendKeys(inputCorreoCorporativo, usuario);
	}

	public void clickButtonNext() {
		click(botonSiguiente);
	}

	public void sendKeysInputContrasenaCorporativa(String pswd) {
		sendKeys(inputContrasenaCorporativa, pswd);
	}

	public boolean isVisibleDialogLoginCorporativo() {
		return isVisible(dialogLoginCorporativo, 60);
	}

	public boolean isVisibleInputCorreoCorporativo() {
		return isVisible(inputCorreoCorporativo, 2);
	}

	 */

}
