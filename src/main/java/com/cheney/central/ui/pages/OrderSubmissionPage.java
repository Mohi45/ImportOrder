package com.cheney.central.ui.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class OrderSubmissionPage extends ParentPage{
    private static final Logger logger = Logger.getLogger(OrderSubmissionPage.class);
    By txt_success= By.xpath("//div[@ng-show='orderSubmitted']");

    public OrderSubmissionPage(WebDriver driver) {
        super(driver);
    }

    public String getOrderMessage() {
        String succ_text = waitForElementToAppear(txt_success).getText();
        logger.info(succ_text);

        return succ_text;
    }


}
