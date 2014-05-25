package com.javacodegeeks.snippets.enterprise;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
//import org.springframework.cache.annotation.Cacheable;
import com.whaty.framework.cache.aopcache.annotation.Cacheable;

@Aspect
public class DoAroundAspect {

//	@Around("execution(* com.javacodegeeks.snippets.enterprise.SimpleService.sayHello(..))")
//	@Around("@annotation(com.whaty.framework.cache.aopcache.annotation.Cacheable)")
//	@Around("@annotation(cacheable)  && (args(joinPoint,cacheable))")

	
	@Around("@annotation(cacheable)")
	public void doAround(ProceedingJoinPoint joinPoint,Cacheable cacheable) throws Throwable {

		System.out.println("***AspectJ*** DoAround() is running!! intercepted :  " +  joinPoint.getSignature().getName());
		

		System.out.println("***AspectJ*** DoAround() before is running!");
		joinPoint.proceed(); // continue on the intercepted method
		System.out.println("***AspectJ*** DoAround() after is running!");

	}

}