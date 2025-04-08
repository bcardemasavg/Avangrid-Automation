package com.nttdata.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends WebBasePage {

	@FindBy(xpath = "//p[text()='Usuario']/following-sibling::div//input[@type='text']")
	private WebElement inputUsuario;

	@FindBy(xpath = "//p[text()='Contraseña']/following-sibling::div//input[@type='password']")
	private WebElement inputContrasena;

	@FindBy(xpath = "(//button[text()='Ingresar al sistema'])[last()]")
	private WebElement buttonIngresarAlSistema;

	@FindBy(xpath = "(//button[text()='Ingresar como administrador'])[last()]")
	private WebElement buttonIngresarComoAdministrador;

	@FindBy(xpath = "//span[text()='Iniciar sesión']")
	private WebElement buttonIniciarSesionCorporativa;

	@FindBy(xpath = "//input[@type='email']")
	private WebElement inputCorreoCorporativo;

	@FindBy(xpath = "//span[text()='Siguiente']")
	private WebElement botonSiguiente;

	@FindBy(xpath = "//input[@type='password']")
	private WebElement inputContrasenaCorporativa;

	@FindBy(xpath = "//div[@id='passwordNext']")
	private WebElement botonSiguienteContrasena;

	@FindBy(xpath = "//div[@role='dialog']")
	private WebElement dialogLoginCorporativo;

	public LoginPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public void sendKeysInputUsuario(String usuario) {
		sendKeys(inputUsuario, usuario);
	}

	public void sendKeysInputContrasena(String contrasena) {
		sendKeys(inputContrasena, contrasena);
	}

	public void clickButtonIngresarAlSistema() {
		click(buttonIngresarAlSistema);
	}

	public void clickButtonIngresarComoAdministrador() {
		click(buttonIngresarComoAdministrador);
	}

	public void clickButtonIniciarSesionCorporativa() {
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

}
