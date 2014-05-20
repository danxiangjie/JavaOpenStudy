package com.wenbin.framework.redis.jedis;

import java.util.Properties;

import com.wenbin.framework.util.PropertiesUtil;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class GetJedisSafeLy {
	
	private static  String configName="JedisConfig.properties";
	public static String getConfigName() {
		return configName;
	}

	public static void setConfigName(String configName) {
		GetJedisSafeLy.configName = configName;
	}

	private static JedisPool pool=null;
	
	public static JedisPool getPool(){
		if(null==pool){	
			
	    Properties con=PropertiesUtil.loadProperties(getConfigName());
		JedisPoolConfig config=new JedisPoolConfig();
		config.setMaxActive(Integer.valueOf((String) con.get("redis.pool.maxActive")));
		config.setMaxIdle(Integer.valueOf( (String) con.get("redis.pool.maxIdel")));
		config.setMaxWait(Integer.valueOf( (String) con.get("redis.pool.maxWait")));
		config.setTestOnBorrow(Boolean.valueOf((String) con.get("redis.pool.testOnBorrow")));
		pool=new JedisPool(config, (String)con.get("redis.pool.ip"), Integer.valueOf((String)con.get("redis.pool.port")));
		
		}
		
		return pool;
	}
	
	public static Jedis getJedis(){
		Jedis j=null;
		try{
			j=getPool().getResource();
		}catch(Exception e){
			returnBrokenJedis(j);
		}finally{
			j=getPool().getResource();
		}
		return j;
		
	}

	public static void returnJedis(Jedis j){
		getPool().returnResource(j);
	}
	
	public static void returnBrokenJedis(Jedis j){
		getPool().returnBrokenResource(j);
	}
	
	public static void main(String[] args) {
		Jedis j=getJedis();
		if(null!=j){
		    j.set("1", "wenbin");
		    System.out.println(j.get("1"));
//		            Ïú»ÙjedisÊµÀý
		    returnJedis(j);   
		}
	}
}
