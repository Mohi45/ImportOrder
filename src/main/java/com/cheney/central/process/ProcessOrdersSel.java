package com.cheney.central.process;

import com.cheney.central.entities.Credential;
import com.cheney.central.entities.ImportEntity;
import com.cheney.central.entities.UserInfo;
import com.cheney.central.ui.pages.*;
import com.esave.entities.OrderDetails;
import com.esave.exception.ImportOrderException;
import com.framework.selenium.BasePage;
import com.framework.selenium.SendMailSSL;
import com.framework.utils.Utils;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.List;

public class ProcessOrdersSel extends BasePage {
    private static final Logger logger = Logger.getLogger(ProcessOrdersSel.class);
    private static final String loginFail = "failed due to : Incorrect Username/Password";

    private Credential credential;
    private UserInfo userInfo;
    private LoginPage loginPage;
    private HomePage homePage;
    private ImportPage importPage;
    private CartPage cartPage;
    private ParentPage parentPage;
    private OrderSubmissionPage orderSubmissionPage;

    private Utils utils;

    private final String fileName = "C:/orders/id.csv";
    private List<ImportEntity> entities;
    private final OrderDetails orderDetails;

    public ProcessOrdersSel(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

//    public ProcessOrdersSel(){
//    }

//    public static void main(String[] args) {
//        ProcessOrdersSel processOrdersSel = new ProcessOrdersSel();
//        processOrdersSel.start();
//    }

    public void start() {
        String userName = orderDetails.getUserName();
        String password = orderDetails.getPassword();
        String orderID = orderDetails.getOrderId();
//        String userName = "dvallone@loxclub.com";
//        String password = "Loxa1990";
//        String orderID = "385355";
        String filepath = "C:\\orders\\";
//        String date = orderDetails.getDeliverydate();
        // Actual File path ##
        String filename = filepath + orderID + ".csv";
        int importItemQty = 0;

        logger.info(userName + " : " + password + " and " + filename);
//        logger.info("Order delivery date is : " + date);

        try {
            // Launch setProperty for chrome, Launch, Implicit wait & maximize

            // #Step 1 - // Browser
            driver = Preconditions();
            loginPage = new LoginPage(driver);
            homePage = new HomePage(driver);
            importPage = new ImportPage(driver);
            cartPage = new CartPage(driver);
            orderSubmissionPage = new OrderSubmissionPage(driver);

            loginPage.invokeLogin();
            // Enter username, pwd and Return successful
            loginPage.doLogin(userName, password);
            Thread.sleep(10000);
            if (loginPage.isLoginSuccess()) {
                logger.info("Login successful !");
            } else {
                throw new ImportOrderException(loginFail, 1001);
            }


            Thread.sleep(2000);

            homePage.selectAccount(orderDetails.getAccountNumber());
            // Check and Empty all Items from Cart
            homePage.goToCart();
            emptyCart();

            Thread.sleep(2000);

            // #Step 4 - Upload btn click
            homePage.goToImportOrder();
            importPage.uploadFile(filename);

            importPage.checkHeader();
            importPage.selectColumns();
            importPage.submitFile();

            try {
                // get Link text
                Thread.sleep(8000);
                importPage.verifyUpload(orderID);
            } catch (WebDriverException e) {
                logger.info("Failed !!! at verifyUpload / UpDate Cart");
                e.printStackTrace();
                throw new ImportOrderException("failed to verify uploaded order file", 1002);
            }
            errorScreenshot(driver, orderID + "_import");

            homePage.goToCart();
            cartPage.submitItems();
            cartPage.confirmOrder();

            String message = orderSubmissionPage.getOrderMessage();

            if (message.toUpperCase().contains("SUCCESSFULLY")) {
                // notification
                logger.info("sending order success notification");
                sendOrderStatusMail(orderDetails, "Success");
            } else {
                logger.error("Failed !!!! missing keyword SUCCESSFULLY, sending order failed notification");
                throw new Exception();
            }

        } catch (ImportOrderException i) {
            logger.info("Failed !!!!" + i.getMessage());
            i.printStackTrace();
            SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), loginFail);
            errorScreenshot(driver, orderID);
        } catch (Exception ex) {
            logger.info("Failed !!!!" + ex.getMessage());
            ex.printStackTrace();
            // notification
            SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), "Order Import Failed");
            errorScreenshot(driver, orderID);
        } finally {
            // Choose Logout option
            driver.quit();
        }
    }

    private void updateCart(WebDriver driver) {
    }

    private void emptyCart() {

    }
}
