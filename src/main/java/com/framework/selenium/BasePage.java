package com.framework.selenium;

import com.esave.entities.OrderDetails;
import com.esave.exception.ImportOrderException;
import com.framework.api.NotificationEvent;
import com.framework.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public abstract class BasePage {
    public WebDriver driver;
    public  static final String home = System.getProperty("user.home");

    private static final Logger logger = Logger.getLogger(BasePage.class);


    public boolean browserAlert() throws InterruptedException {

        // Check the presence of alert
        try {
            Alert alert = driver.switchTo().alert();
            logger.info(alert.getText());
            alert.accept();
            logger.info("Alert pop up accepted - Items added to cart");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Browser Alert not present");
            return true;
        }
    }

    public void handleHtmlAlert(String action) {
        WebElement alert = driver.findElement(By.xpath("//span[text()='Alert Notification']"));
        if (alert.isDisplayed()) {
            logger.info(alert.getText());
            driver.findElement(By.xpath("//button[text()='" + action + "']")).click();
        } else {
            throw new ImportOrderException("HTML Alert to add items not present", 222);
        }
    }
    public boolean uploadFile(Transferable ss) throws InterruptedException {

        try {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            Robot robot = new Robot();
            Thread.sleep(2000);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_V);
            Thread.sleep(2000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            return true;
        } catch (HeadlessException e) {
            logger.info("Desktop window upload failed");

            return false;
        } catch (AWTException e) {
            logger.info("Desktop window upload failed");
            return false;
        }
    }

    public WebDriver Preconditions() {
        /*ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        System.setProperty("webdriver.chrome.driver",
                home + "/Downloads/chromedriver_win32/chromedriver.exe");
        driver = new ChromeDriver(options);*/
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("--headless");
	    options.addArguments("window-size=1200x600");
		WebDriverManager.chromedriver().setup();
		driver=new ChromeDriver(options);
		driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        return driver;

    }

    public void errorScreenshot(WebDriver driver, String orderID) {
        // Take screenshot and store as a file format
        // WaitForPageToLoad(30);

        try {
            // now copy the screenshot to desired location using copyFile
            // //method
            orderID = orderID != null ? orderID : new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            FileUtils.copyFile(src, new File("/var/jenkins_home/workspace/ImportOrder/src/main/resources/screeenShot/" + orderID + ".png")); // System.currentTimeMillis()
        }
        catch (Exception e) {
            logger.info("Screenshot failed " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public boolean PageExist(String expectedTitle) throws InterruptedException {

        try {
            for (int i = 0; i < 3; i++) {
                String act = getDriver().getTitle();
                if (act.equals(expectedTitle)) {
                    logger.info("current page - " + expectedTitle);
                    return true;
                } else {
                    Thread.sleep(2000);
                    logger.info("waiting for page.. ");
                }
            }
            logger.info("Not reached page - " + expectedTitle);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void WaitForPageToLoad(int... waitTime) {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
            }
        };

        if (waitTime.length > 0) {
            Wait(waitTime).until(expectation);
        } else {
            Wait(30).until(expectation);
        }

    }

    public Wait<WebDriver> Wait(int... waitTime) {
        int waitTimeInSeconds;
        if (waitTime.length > 0) {
            waitTimeInSeconds = waitTime[0];
        } else {
            waitTimeInSeconds = 5;
        }
        return new FluentWait<WebDriver>(getDriver()).withTimeout(waitTimeInSeconds, TimeUnit.SECONDS)
                .pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void sendOrderStatusMail(OrderDetails orderDetails, String status) {
        if (orderDetails != null) {
            try {
                if (!status.equalsIgnoreCase("Success")) {
                    new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
                            NotificationEvent.FAILURE);
                    SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), status);
                } else {
                    new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
                            NotificationEvent.SUCCESS);
                    SendMailSSL.sendMailAction(orderDetails.getOrderId(), "Success");
                }
            } catch (Exception e1) {
                logger.info("Communication failure occured while sending " + status + " notification");
                e1.printStackTrace();
            }
        }
    }

    public void pressEscape() {
        try {
            Actions action = new Actions(driver);
            action.sendKeys(Keys.ESCAPE).build().perform();
            // driver.findElement(By.xpath("//button[@title='close']/span")).click();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
