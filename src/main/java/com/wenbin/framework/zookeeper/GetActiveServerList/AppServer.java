package com.serverList;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

public class AppServer {
	private String groupNode = "sgroup";
	private String subNode = "sub";
	private static CountDownLatch  cdl =new CountDownLatch(1);

	/**
	 * ����zookeeper
	 * @param address server�ĵ�ַ
	 */
	public void connectZookeeper(String address) throws Exception {
		ZooKeeper zk = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183", 5000, new Watcher() {
			public void process(WatchedEvent event) {
				// ��������
			}
			
		});
		
//		ҲӦ��ȷ�������ϣ��ٴ����ӽڵ�
		
		if(zk.getState()==States.CONNECTED){
			cdl.countDown();
		}
		// ��"/sgroup"�´����ӽڵ�
		// �ӽڵ����������ΪEPHEMERAL_SEQUENTIAL, ��������һ����ʱ�ڵ�, �����ӽڵ�����ƺ������һ�����ֺ�׺
		// ��server�ĵ�ַ���ݹ������´������ӽڵ���
		String createdPath = zk.create("/" + groupNode + "/" + subNode, address.getBytes("utf-8"), 
			Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		System.out.println("create: " + createdPath);
	}
	
	/**
	 * server�Ĺ����߼�д�����������
	 * �˴������κδ���, ֻ��server sleep
	 */
	public void handle() throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}
	
	public static void main(String[] args) throws Exception {
		// �ڲ�����ָ��server�ĵ�ַ
//		if (args.length == 0) {
//			System.err.println("The first argument must be server address");
//			System.exit(1);
//		}
		
		AppServer as = new AppServer();
		as.connectZookeeper("127.0.0.1��8888;127.0.0.2:9999");
		as.handle();
	}
}