package com.framework.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class RandomAction {
	
	private static final Logger logger = Logger.getLogger(RandomAction.class);

//	public static File WaitForNewFile(Path folder, String extension, int timeout_sec)
//			throws InterruptedException, IOException {
//		long end_time = System.currentTimeMillis() + timeout_sec * 1000;
//		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
//			folder.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
//			for (WatchKey key; null != (key = watcher.poll(end_time - System.currentTimeMillis(),
//					TimeUnit.MILLISECONDS)); key.reset()) {
//				for (WatchEvent<?> event : key.pollEvents()) {
//					File file = folder.resolve(((WatchEvent<Path>) event).context()).toFile();
//					if (file.toString().toLowerCase().endsWith(extension.toLowerCase()))
//						return file;
//				}
//			}
//		}
//		return null;
//	}
//
//	public static void DownloadOG(Robot robot) {
//
//		try {
//			robot = new Robot();
//			Thread.sleep(2000); // Thread.sleep throws InterruptedException
//			robot.keyPress(KeyEvent.VK_DOWN); // press arrow down key of
//												// keyboard to navigate and
//												// select Save radio button
//
//			Thread.sleep(2000); // sleep has only been used to showcase each
//								// event separately
//			robot.keyPress(KeyEvent.VK_TAB);
//			Thread.sleep(2000);
//			robot.keyPress(KeyEvent.VK_TAB);
//			Thread.sleep(2000);
//			robot.keyPress(KeyEvent.VK_TAB);
//			Thread.sleep(2000);
//			robot.keyPress(KeyEvent.VK_ENTER);
//			// press enter key of keyboard to perform above selected action
//			logger.info("File is downloaded");
//
//		} catch (AWTException e) {
//			ErrRemedy.ErrReportingMail();
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			ErrRemedy.ErrReportingMail();
//			e.printStackTrace();
//		}
//	}

	public static boolean isIframePresent(WebDriver driver) {
		//
		// driver.findElement(By.xpath("//html/body/table/tbody/tr[2]/td[1]/div/div[2]/table/tbody/tr[1]/td/input")).click();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// List to get & store frame
		List<WebElement> ele = driver.findElements(By.tagName("iframe"));
		logger.info("Number of frames in a page :" + ele.size()); // ele.size
																			// -
																			// size
																			// of
																			// frame
																			// list

		if (ele.size() == 0) {
			logger.info("No iframes on this page");
			return false; // No frames
		} else {
			logger.info("iFrames present on this page, Below are the details -");

			for (WebElement el : ele) {
				// Returns the Id of a frame
				logger.info("iFrame Id :" + el.getAttribute("id"));
				// Returns the Name of a frame.
			//	logger.info("iFrame name :" + el.getAttribute("name"));
			}
			return true; // frames present
		}

	}

	public static String getIframeID(WebDriver driver){
		String id ="";
		List<WebElement> ele = driver.findElements(By.tagName("iframe"));
		if (ele!=null && ele.size()==1){
			id= ele.get(0).getAttribute("id");
		}
		return id;
	}

	public void checkAllCheckBoxes(WebDriver driver) {

		List<WebElement> chk_ItemList = driver.findElements(By.xpath("//input[@class='CheckBox']"));
		for (WebElement chk_CurrentItem : chk_ItemList) {
			if (!chk_CurrentItem.isSelected()) {
				chk_CurrentItem.click();
			}
		}

	}

}
