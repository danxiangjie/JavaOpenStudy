package com.wenbin.framework.redis.sharding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 基于Jedis的多机切片实现
 * 
 * @author wangwenbin
 * @date 2014.04.22
 * 
 */
public class redis_resharding {
	private static Map<String, Integer> ServerList = new HashMap<String, Integer>();
	private static Map<String, Integer> SrcList = new HashMap<String, Integer>();
	private static ShardedJedisPool pool = null;

	public static  void init() {
		SrcList.put("127.0.0.1", 6379);
		SrcList.put("127.0.0.1", 6380);
		SrcList.put("127.0.0.1", 6381);
	}

	class MonitoringServer extends TimerTask {

		@Override
		public void run() {
			Iterator<Entry<String, Integer>> it = SrcList.entrySet().iterator();
			int lastlen = ServerList.size();
			while (it.hasNext()) {
				Entry<String, Integer> tmp = it.next();
				String host = tmp.getKey();
				int port = tmp.getValue();
				if (check(host, port)) {
					ServerList.put(host, port);
				}
			}
			int currlen = ServerList.size();
			
			if (currlen != lastlen) {
				if (currlen > lastlen) { // 第一次 or 增加机器 ，重新哈希
					 if(lastlen==0){
							System.out.println("第一次哈希");
					 }else{
							System.out.println(" 重新哈希");
					 }
					    build(ServerList);
				} else {             
					// 若有机器宕机，即redis服务器 数目减少，则应启动其从机slave为主机（通过配置文件中的哨兵模块来实现），在此过程中将从机的端口 与 原主机的端口相互替换
					System.out.println("启动其从机slave为主机（通过配置文件中的哨兵模块来实现），在此过程中将从机的端口 与 原主机的端口相互替换");
				}
			}

		}

		public boolean check(String host, int port) {
			Jedis tmp = new Jedis(host, port);
			String res = tmp.ping();
			if (res.equals("PONG")) {
				return true;
			}
			return false;
		}

	

	}
	
	public  static void build(Map<String, Integer> serverList) {

		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		Iterator<Entry<String, Integer>> it = serverList.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, Integer> tmp = it.next();
			JedisShardInfo info = new JedisShardInfo(tmp.getKey(),
					tmp.getValue());
			shards.add(info);
		}
		
		JedisPoolConfig conf = new JedisPoolConfig();
		conf.maxActive = 8;
		conf.testOnBorrow = true;
		conf.testOnReturn = true;
	    pool = new ShardedJedisPool(conf, shards);
	    
	
		
	}
	public static void main(String[] args) {
		init();
		Timer t = new Timer();
		t.schedule(new redis_resharding().new MonitoringServer(), 0, 60*60*1000);
		
		if(null!=pool){
		    ShardedJedis jedis = (ShardedJedis)pool.getResource();
            jedis.set("1", "one");
            System.out.println( jedis.get("1"));
	   }

      

	 }
}
