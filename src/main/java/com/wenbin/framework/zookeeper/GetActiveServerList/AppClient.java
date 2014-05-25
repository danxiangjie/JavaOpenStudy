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

//	保证zookeeper连接到服务，再开始存取操作
	public static void waitUntilConnected(ZooKeeper zooKeeper) {

		Watcher watcher = new ConnectedWatcher(connectedLatch);
		zooKeeper.register(watcher);
//		正在连接，还未连接上
		if (States.CONNECTING == zooKeeper.getState()) {
			try {
				connectedLatch.await();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	// 连接zookeeper
	public void connectZookeeper() throws Exception {
		zk = new ZooKeeper("localhost:2181,localhost:2182,localhost:2183",
				5000, new ConnectedWatcher(connectedLatch));
		waitUntilConnected(zk);
		updateServerList();
	}

	// 更新server列表
	private static void updateServerList() throws Exception {
		List<String> newServerList = new ArrayList<String>();

		// 获取并监听groupNode的子节点变化  // watch参数为true, 表示监听子节点变化事件.
		
		// 每次都需要重新注册监听, 因为一次注册, 只能监听一次事件, 如果还想继续保持监听, 必须重新注册
		
		List<String> subList = zk.getChildren("/" + groupNode, true);
		for (String subNode : subList) {
			// 获取每个子节点下关联的server地址
			byte[] data = zk.getData("/" + groupNode + "/" + subNode, false,
					stat);
			newServerList.add(new String(data, "utf-8"));
		}

		// 替换server列表
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
//			  避免   KeeperErrorCode = ConnectionLoss錯誤
				{ connectedLatch.countDown();
				// 如果发生了"/sgroup"节点下的子节点变化事件, 更新server列表, 并重新注册监听
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
	 
	// client的工作逻辑写在这个方法中 此处不做任何处理, 只让client sleep
	public void handle() throws InterruptedException {
		Thread.sleep(Long.MAX_VALUE);
	}

	public static void main(String[] args) throws Exception {
		AppClient ac = new AppClient();
		ac.connectZookeeper();
		ac.handle();
	}

}