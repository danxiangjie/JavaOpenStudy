package com.serverList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class AppClient {
	private static String groupNode = "sgroup";
	private static ZooKeeper zk;
	private static Stat stat = new Stat();
	private static volatile List<String> serverList;
	static CountDownLatch connectedLatch = new CountDownLatch(1);

//	��֤zookeeper���ӵ������ٿ�ʼ��ȡ����
	public static void waitUntilConnected(ZooKeeper zooKeeper) {

		Watcher watcher = new ConnectedWatcher(connectedLatch);
		zooKeeper.register(watcher);
//		�������ӣ���δ������
		if (States.CONNECTING == zooKeeper.getState()) {
			try {
				connectedLatch.await();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	// ����zookeeper
	public void connectZookeeper() throws Exception {
		zk = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183",
				5000, new ConnectedWatcher(connectedLatch));
		waitUntilConnected(zk);
		updateServerList();
	}

	// ����server�б�
	private static void updateServerList() throws Exception {
		List<String> newServerList = new ArrayList<String>();

		// ��ȡ������groupNode���ӽڵ�仯  // watch����Ϊtrue, ��ʾ�����ӽڵ�仯�¼�.
		
		// ÿ�ζ���Ҫ����ע�����, ��Ϊһ��ע��, ֻ�ܼ���һ���¼�, �������������ּ���, ��������ע��
		
		List<String> subList = zk.getChildren("/" + groupNode, true);
		for (String subNode : subList) {
			// ��ȡÿ���ӽڵ��¹�����server��ַ
			byte[] data = zk.getData("/" + groupNode + "/" + subNode, false,
					stat);
			newServerList.add(new String(data, "utf-8"));
		}

		// �滻server�б�
		serverList = newServerList;

		System.out.println("server list updated: " + serverList);
	}

	private static class ConnectedWatcher implements Watcher {

		private CountDownLatch connectedLatch;

		ConnectedWatcher(CountDownLatch connectedLatch) {
			this.connectedLatch = connectedLatch;
		}

		@Override
		public void process(WatchedEvent event) {
			if (event.getState() == KeeperState.SyncConnected) {
//			  ����   KeeperErrorCode = ConnectionLoss�e�`
				{ connectedLatch.countDown();
				// ���������"/sgroup"�ڵ��µ��ӽڵ�仯�¼�, ����server�б�, ������ע�����
				if (event.getType() == EventType.NodeChildrenChanged
						&& ("/" + groupNode).equals(event.getPath())) {
					try {
						updateServerList();
					} catch (Exception e) {
						e.printStackTrace();
					}
				 }
				}
			}
		}
	}
	 
	// client�Ĺ����߼�д����������� �˴������κδ���, ֻ��client sleep
	public void handle() throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}

	public static void main(String[] args) throws Exception {
		AppClient ac = new AppClient();
		ac.connectZookeeper();
		ac.handle();
	}

}