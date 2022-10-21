package com.cheney.central.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends ParentPage {
    By inp_username = By.xpath("//input[@placeholder='Email']");
    By inp_password = By.xpath("//input[@placeholder='Password']");
    By btn_login = By.xpath("//button[contains(.,'Login')]");
    By lnk_login = By.xpath("//a[contains(text(),'Login »')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void doLogin(String user, String pwd) {
        waitForElementToAppear(inp_username).sendKeys(user);
        waitForElementToAppear(inp_password).sendKeys(pwd);
        waitForElementToAppear(btn_login).click();
    }

    public void invokeLogin() {
        driver.get("https://www.cheneycentral.com/Web/#/default");
        waitForElementToAppear(lnk_login).click();
    }

    public boolean isLoginSuccess() {
        return driver.getCurrentUrl().contains("home");
    }
}
