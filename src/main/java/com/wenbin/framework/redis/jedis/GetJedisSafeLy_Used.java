package com.wenbin.framework.redis.jedis;

import redis.clients.jedis.Jedis;
/*
 *  安全获取Jedis实例的工具类
 */
public class GetJedisSafeLy_Used {
	/*
	 * 使用ThreadLocal类来保证获取到的Jedis的独立性，通过内部封装的JedisStatus来保证当前线程 只可取的一次Jedis实例
	 */
	private ThreadLocal<JedisStatus> threadLocal = new ThreadLocal<JedisStatus>();
/*
 * 获取Jedis实例
 */
	public Jedis getJedis() {
		JedisStatus jedisStatus = this.threadLocal.get();
		if (jedisStatus == null)
			throw new RuntimeException("must first setJedis method");

		if (!jedisStatus.isUsed()) {
			jedisStatus.setUsed(true);
		} else {
			this.threadLocal.remove();
		}
		return jedisStatus.getJedis();
	}
/*
 * 设置Jedis
 */
	public void setJedis(Jedis jedis) {
		JedisStatus jedisStatus = new JedisStatus();
		jedisStatus.setJedis(jedis);
		jedisStatus.setUsed(false);
		this.threadLocal.set(jedisStatus);
	}

	/*
	 * 内部类
	 */
	private static class JedisStatus {
		private boolean used;
		private Jedis jedis;

		public boolean isUsed() {
			return used;
		}

		public void setUsed(boolean used) {
			this.used = used;
		}

		public Jedis getJedis() {
			return jedis;
		}

		public void setJedis(Jedis jedis) {
			this.jedis = jedis;
		}
	}

}