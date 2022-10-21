package com.framework.selenium;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;

public class ErrRemedy {
	
	public static void ErrScreenshotCapture(WebDriver driver) {
		// Take screenshot and store as a file format
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			// now copy the screenshot to desired location using copyFile
			// //method
			FileUtils.copyFile(src, new File("C:\\Users\\ashsaxen\\Downloads\\errorScreenshotGFS\\error.png"));
		} catch (Exception e) {
			System.out.println("take screenshot failure");
			e.printStackTrace();
		}

	}
	
	public static void ErrReportingMail() {
		// TODO Auto-generated method stub

	}

}
