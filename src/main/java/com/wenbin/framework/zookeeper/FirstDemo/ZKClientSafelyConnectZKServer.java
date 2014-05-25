package com.wenbin.framework.zookeeper.FirstDemo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.data.Stat;

public class ZKClientSafelyConnectZKServer {
    private static String NodeName="/sgroup";
	private static CountDownLatch cd = new CountDownLatch(1);
	private static ZooKeeper zk = null;
	
	public static  void connect(String host,  int ttl) {

		try {
			zk = new ZooKeeper(host , ttl, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					if (event.getState() == KeeperState.SyncConnected) {
						cd.countDown();// ������֮���ͷż�����
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readData() {
		try {
			zk.getData(NodeName, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					try {
						System.out.println("ԭ���Ϊ*****************"+zk.getData(NodeName, false, null));
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (event.getType() == EventType.NodeDataChanged) {
						System.out.println("��ݷ���ı�*****************");
					}
				}
			}, null);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}	
	
	
	 public static String readData2()throws Exception{
	      
	      String s=new String(zk.getData(NodeName, new Watcher(){

			@Override
			public void process(WatchedEvent event) {
				
				         try{
				        if(event.getType()==Event.EventType.NodeDataChanged){
				            System.out.println("�仯���:  "+readData2());
				        }
				         }catch(Exception e){
				             e.printStackTrace();
				         }
				        
			}
	    	  
	      } ,null));
	      
	    return s;  
	  }
	
	 /***
     * д������
     * ���
     * @param path д��·��
     * @param value д���ֵ
     * **/
  public static void addOrUpdateData(String path,String data)throws Exception {
      
      
      Stat stat=zk.exists(path, false);
      if(stat==null){
            //û�оʹ�������д��        
          zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
         System.out.println("�½�����д����ݳɹ�.. ");
      }else{  
          //���ڣ��͸���
          zk.setData(path, data.getBytes(), -1);
          System.out.println("���³ɹ�!");
      }
  }
  
	 public void close(){
	        
	        try{
	            zk.close();
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	        
	    }
	 
	 
	public static void main(String[] args) throws Exception {
		String host="localhost:2181,localhost:2182,localhost:2183";
	    int ttl = 5000;

	    String data="944488ww899";
        connect(host,  ttl);// ���ӷ���
       
        if (States.CONNECTING == zk.getState()) {// δ�����ϣ�����ȴ�  ���������Ⲽ��ûȷ���Ƿ����ӵ�����ˣ��ᱨ��
			try {
				cd.await();
			} catch (InterruptedException e) {
				throw new IllegalStateException(e);
			}
		}
        addOrUpdateData(NodeName,data);// 
       
        
        
//        Thread.sleep(Long.MAX_VALUE);
        
        readData2();
        
        Thread.sleep(Long.MAX_VALUE);

	}
}
