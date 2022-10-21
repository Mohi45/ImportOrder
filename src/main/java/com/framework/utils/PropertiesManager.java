package com.framework.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesManager {

	public static String purveyorPropertiesFile;
	public static String locationPropertiesFile;

	private static Properties purveyorProperties;
	private static Properties locationProperties;

	private PropertiesManager() {

	}

	public static Properties getPurveyorProperties() throws IOException {
		if (purveyorProperties == null) {
			synchronized (PropertiesManager.class) {
				if (purveyorProperties == null) {
					purveyorProperties = new Properties();
					InputStream inputStream = PropertiesManager.class.getClassLoader()
							.getResourceAsStream(purveyorPropertiesFile);
					purveyorProperties.load(inputStream);
				}
			}
		}
		return purveyorProperties;
	}

	public static Properties getLocationProperties() throws IOException {
		if (locationProperties == null) {
			synchronized (PropertiesManager.class) {
				if (locationProperties == null) {
					locationProperties = new Properties();
//					InputStream inputStream = PropertiesManager.class.getClassLoader()
//							.getResourceAsStream(locationPropertiesFile);
					locationProperties.load(new FileInputStream(locationPropertiesFile));
				}
			}
		}
		return locationProperties;
	}

}
