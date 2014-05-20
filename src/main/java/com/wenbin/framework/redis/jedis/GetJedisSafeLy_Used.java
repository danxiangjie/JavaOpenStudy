package com.wenbin.framework.redis.jedis;

import redis.clients.jedis.Jedis;
/*
 *  ��ȫ��ȡJedisʵ���Ĺ�����
 */
public class GetJedisSafeLy_Used {
	/*
	 * ʹ��ThreadLocal������֤��ȡ����Jedis�Ķ����ԣ�ͨ���ڲ���װ��JedisStatus����֤��ǰ�߳� ֻ��ȡ��һ��Jedisʵ��
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