package com.wenbin.framework.redis.TwemproxySentinel;

import java.util.Map;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPubSub;

public class Listener extends JedisPubSub {

	private Map<String, HostAndPort> map;

	public Listener(Map<String, HostAndPort> map) {
		this.map = map;
	}

	@Override
	public void onMessage(String channel, String message) {
		System.out.println("get message: " + message + " from " + channel);
		String[] tmp = null;
		if (null != message) {
			tmp = message.split(" ");
			if (message.length() > 3) {
				map.put(tmp[0],
						new HostAndPort(tmp[3], Integer.parseInt(tmp[4])));
			}
		}
	}


	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out.println("subcrible  chanenel: " + channel
				+ ", total channels: " + subscribedChannels);
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		System.out.println("unsubcrible chanenel: " + channel
				+ ",total channels: " + subscribedChannels);
		// unsubscribe(channel);
	}


	public Map<String, HostAndPort> getMap() {
		return map;
	}

	public void setMap(Map<String, HostAndPort> map) {
		this.map = map;
	}
	
	@Override
	public void onPMessage(String pattern, String channel, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPUnsubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPSubscribe(String pattern, int subscribedChannels) {
		// TODO Auto-generated method stub
	}


}
