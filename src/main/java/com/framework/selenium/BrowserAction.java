package com.framework.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BrowserAction {
	
	private static final Logger logger = Logger.getLogger(BrowserAction.class);
	
	public static void isAlertPresent(WebDriver driver) {
		try {
			driver.findElement(By.xpath("//button[@class='button gfsexperience-modal-close']")).click();
		} catch (Exception e) {
			logger.info("No Alert present !!" +e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public void ClearBrowserCache(WebDriver driver) {

		try {
			driver.manage().deleteAllCookies(); // delete all cookies
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} // wait 5 seconds to clear cookies.

	}

}
