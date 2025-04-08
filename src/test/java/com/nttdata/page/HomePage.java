package com.nttdata.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class HomePage extends WebBasePage {

	private String XPATH_MENU = "//li[contains(.,'%s')]";
	private String XPATH_SUB_MENU = "//div[./li[contains(.,'%s')]]//span[contains(.,'%s')]";
	private String XPATH_OPCION_RECETAS = "//li[contains(text(), '%s')]";

	@FindBy(name = "search")
	private WebElement inputBusquedaReceta;

	@FindBy(xpath = "//div[contains(.,'Buscar por nombre de la especialidad')]//button[contains(text(), 'Filtrar')]")
	private WebElement buttonFiltrar;

	public HomePage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public void navegarAMenu(String menu) throws Exception {
		By locator = By.xpath(String.format(XPATH_MENU, menu));
		click(locator);
	}

	public void navegarAMenuYSubMenu(String menu, String subMenu) throws Exception {
		navegarAMenu(menu);
		waitFor(1);
		By locator = By.xpath(String.format(XPATH_SUB_MENU, menu, subMenu));
		click(locator);
	}

	public void sendeKeysInputBusquedaReceta(String tipoReceta) throws Exception {
		sendKeys(inputBusquedaReceta, tipoReceta);
		waitFor(1);
	}

	public void clickOpcionInputBusquedaRecetas(String tipoReceta) throws Exception {
		By locator = By.xpath(String.format(XPATH_OPCION_RECETAS, tipoReceta));
		click(locator);
	}
	
	public void clickButtonFiltrar() throws Exception {
		click(buttonFiltrar);
	}

}