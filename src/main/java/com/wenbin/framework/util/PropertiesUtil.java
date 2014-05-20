package com.wenbin.framework.util;


import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
	public static Properties loadProperties(String path) {

		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(path));
			return properties;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

}
