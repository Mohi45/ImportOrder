package com.cheney.central.ui.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends ParentPage{
    private static final Logger logger = Logger.getLogger(CartPage.class);

    By btn_checkout = By.xpath("//button[contains(.,'PROCEED TO CHECKOUT')]");
    By btn_submitOrder = By.xpath("//button[contains(.,'SUBMIT ORDER')]");
    By txt_success= By.xpath("//div[@ng-show='orderSubmitted']");

    public CartPage(WebDriver driver){
        super(driver);
    }

    public void submitItems() {
        logger.info("checking out cart items");
        waitForElementToAppear(btn_checkout).click();
    }

    public void confirmOrder() {
        logger.info("submitting cart items");
        waitForElementToAppear(btn_submitOrder).click();
    }
}