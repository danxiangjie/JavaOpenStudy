package com.wenbin.framework.redis.jedis;

import redis.clients.jedis.Jedis;
/*
 *  ��ȫ��ȡJedisʵ��Ĺ�����
 */
public class GetJedisSafeLy_Used {
	/*
	 * 首先通过jedis pool，获取一个jedis 实例，然后 调用此类中的setJedis（）方法，然后再使用getJedis（）方法，获取jedis实例里，完成后续的处理
	 */
	private ThreadLocal<JedisStatus> threadLocal = new ThreadLocal<JedisStatus>();
/*
 * ��ȡJedisʵ��
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
 * ����Jedis
 */
	public void setJedis(Jedis jedis) {
		JedisStatus jedisStatus = new JedisStatus();
		jedisStatus.setJedis(jedis);
		jedisStatus.setUsed(false);
		this.threadLocal.set(jedisStatus);
	}

	/*
	 * �ڲ���
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