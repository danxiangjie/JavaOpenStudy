package com.wenbin.framework.redis.TwemproxySentinel;

import java.util.Map;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

public class SubClient {

	private Jedis jedis;//
	private Listener listener;        // 单listener

	public SubClient(String host, int port,Map<String, HostAndPort> map) {
		jedis = new Jedis(host, port);
		listener = new Listener(map);
	}

	public void subscribe(String channel) {
		jedis.subscribe(listener, channel);
	}

	public void unsubscribe(String channel) {
		listener.unsubscribe(channel);
	}

	public Map<String, HostAndPort> getMap() {
		return listener.getMap();
	}
}
