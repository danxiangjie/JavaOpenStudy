package com.javacodegeeks.snippets.enterprise;

import com.whaty.framework.cache.core.model.EvictionPolicy;

public interface SimpleService {

	public void printNameId();

	public void checkName();

	public String sayHello(String message);

	public void test(String cacheName, int maxSize, boolean eternal,
			int tTLSeconds, EvictionPolicy evictionPolicy);

	public String testt();

}