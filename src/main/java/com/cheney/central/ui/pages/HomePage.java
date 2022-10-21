package com.cheney.central.ui.pages;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends ParentPage {
    private static final Logger logger = Logger.getLogger(HomePage.class);

    By btn_cart = By.xpath("//div[@id='myNavbar']//a[contains(.,'Cart')][@role='button']");
    By lnk_logout = By.xpath("//a[contains(.,'Logout')]");
    By lnk_goToCart = By.xpath("//div[@id='myNavbar']//a[contains(.,'Go to Cart')]");
    String cartUrl = "https://www.cheneycentral.com/Web/#/cart";
    By lnk_importOrder = By.xpath("//div[@id='myNavbar']//a[contains(.,'Import Order')]");
    String importUrl = "https://www.cheneycentral.com/Web/#/importcart";
    String btn_AcNum = "//button[contains(.,'acNum')]";

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void goToCart() {
        try {
            waitForElementToAppear(btn_cart).click();
            waitForElementToAppear(lnk_goToCart).click();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.info("launching cart url ..");
            driver.get(cartUrl);
        }
    }

    public void goToImportOrder() {
        try {
            waitForElementToAppear(btn_cart).click();
            waitForElementToAppear(lnk_importOrder).click();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.info("launching import order url ..");
            driver.get(importUrl);
        }
    }

    public void selectAccount(String acNum) {
        if (StringUtils.isEmpty(acNum)) {
            return;
        }
        String loc = btn_AcNum.replace("acNum", acNum);
        try {
            waitForElementToAppear(By.xpath(loc)).click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logOut() {
        logger.info("logging out");
        waitForElementToAppear(lnk_logout).click();
    }


}
