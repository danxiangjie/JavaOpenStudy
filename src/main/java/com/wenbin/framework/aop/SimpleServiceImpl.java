package com.javacodegeeks.snippets.enterprise.impl;

//import org.springframework.cache.annotation.Cacheable;
import com.javacodegeeks.snippets.enterprise.SimpleService;
import com.whaty.framework.cache.aopcache.annotation.Cacheable;
import com.whaty.framework.cache.core.model.EvictionPolicy;

public class SimpleServiceImpl implements SimpleService {

	private String name;

	private int id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void printNameId() {
		System.out.println("SimpleService : Method printNameId() : My name is "
				+ name + " and my id is " + id);
	}

	public void checkName() {
		if (name.length() < 20) {
			throw new IllegalArgumentException();
		}
	}

	public String sayHello(String name) {
		return name;
	}

	// @Cacheable
	public void test(String cacheName, int maxSize, boolean eternal,
			int tTLSeconds, EvictionPolicy evictionPolicy) {
	}

//	@Cacheable(useDefaultCache=true)
	@Cacheable
	public String  testt() {
		return "testtt---wenbin";
	}

}