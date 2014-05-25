package com.javacodegeeks.snippets.enterprise;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	static String cacheName = "456";
	static String key = "A";
	static String value = "1";
	static int expireSecond = 1;
	private static String redisConfigPath = "redis.properties";

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");

		SimpleService simpleService = (SimpleService) context
				.getBean("simpleServiceBean");

		simpleService.testt();
		context.close();
	}
}