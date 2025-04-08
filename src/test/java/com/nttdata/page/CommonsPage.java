package com.nttdata.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CommonsPage extends WebBasePage {

	@FindBy(xpath = "//button[text()='Siguiente']")
	private WebElement buttonSiguiente;

	@FindBy(xpath = "//button[text()='Aceptar']")
	private WebElement buttonAceptar;

	@FindBy(xpath = "//button[text()='Continuar']")
	private WebElement buttonContinuar;

	@FindBy(xpath = "//button[text()='Grabar']")
	private WebElement buttonGrabar;

	@FindBy(xpath = "//button[text()='Sí']")
	private WebElement buttonSi;

	@FindBy(xpath = "//button[text()='cerrar sesión']")
	private WebElement buttonCerrarSesion;

	public CommonsPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public void clickButtonSiguiente() {
		click(buttonSiguiente);
	}

	public void clickButtonGrabar() {
		click(buttonGrabar);
	}

	public void clickButtonSi() {
		click(buttonSi);
	}

	public void clickButtonCerrarSesion() {
		click(buttonCerrarSesion);
	}

	public void clickButtonAceptar() {
		click(buttonAceptar);
	}

	public void clickButtonContiuar() {
		click(buttonContinuar);
	}

}
