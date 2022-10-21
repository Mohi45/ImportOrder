package com.cheney.central.ui.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class ImportPage extends ParentPage {
    private WebDriver driver;
    private static final Logger logger = Logger.getLogger(ImportPage.class);

    By inp_chooseFile = By.xpath("//input[@type='file']");
    By btn_submit = By.xpath("//button[@type='submit']");

    By txt_success = By.xpath("//div[@class='alert alert-success']");
    By txt_failed = By.xpath("//li[contains(.,'Failed ')]");
    By chk_header = By.xpath("//input[@placeholder='HEADERS IN FIRST ROW?']/following::button[1]");
    By btn_col1 = By.xpath("//button[contains(@title,'COLUMN WITH ITEM NO')]");
    By btn_col2 = By.xpath("//button[contains(@title,'COLUMN WITH ORDER NO')]");
    By btn_col3 = By.xpath("//button[contains(@title,'COLUMN WITH ITEM QTY')]");
    By btn_col4 = By.xpath("//button[contains(@title,'COLUMN WITH WANTED DATE')]");
    By li_col1 = By.xpath("//button[contains(@title,'COLUMN WITH ITEM NO')]/following-sibling::*//span[contains(.,'[Column 01]')]");
    By li_col2 = By.xpath("//button[contains(@title,'COLUMN WITH ORDER NO')]/following-sibling::*//span[contains(.,'[Column 02]')]");
    By li_col3 = By.xpath("//button[contains(@title,'COLUMN WITH ITEM QTY')]/following-sibling::*//span[contains(.,'[Column 03]')]");
    By li_col4 = By.xpath("//button[contains(@title,'COLUMN WITH WANTED DATE')]/following-sibling::*//span[contains(.,'[Column 04]')]");
//button[contains(@title,'COLUMN WITH ITEM NO')]/following-sibling::*//span[contains(.,'[Column 01]')]
    //ul/span[text()='[Column 01] - Item'] - select from ddl
//[Column 02] - Order Number
// [Column 03] - Quantity
//[Column 04] - Order Date

    public ImportPage(WebDriver driver) {
        super(driver);
    }

    public void uploadFile(String filename) {
        waitForElementToAppear(inp_chooseFile).sendKeys(filename);
        waitForElementToAppear(btn_submit);
    }

    public void verifyUpload(String orderId) {
        try {
            String succ_text = waitForElementToAppear(txt_success).getText();
            logger.info(succ_text);
            String fail_text = waitForElementToAppear(txt_failed).getText();
            logger.info(fail_text);
        } catch (Exception ex) {
            logger.error(ex.getLocalizedMessage());
        }
    }

    public void checkHeader() {
        logger.info("checking header for file");
        waitForElementToAppear(chk_header).click();
    }

        public void selectColumns() throws InterruptedException {
            logger.info("selecting columns for order");
            waitForElementToAppear(btn_col1).click();
            waitForElementToAppear(li_col1).
                    click();
            Thread.sleep(200);

            waitForElementToAppear(btn_col2).click();
            waitForElementToAppear(li_col2).
                    click();
            Thread.sleep(200);

            waitForElementToAppear(btn_col3).click();
            waitForElementToAppear(li_col3).
                    click();
            Thread.sleep(200);

            waitForElementToAppear(btn_col4).click();
            waitForElementToAppear(li_col4).
                    click();
            Thread.sleep(200);
        }

    public void submitFile() {
        logger.info("submitted file after upload");
        waitForElementToAppear(btn_submit).click();
    }
}

