package com.first.test;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class first_Demo {
	 private static final int SESSION_TIMEOUT=30000;
	 ZooKeeper zk;// ZooKeeperʵ��
	 //	 ����Watcherʵ��
	 Watcher wh=new Watcher(){

		@Override
		public void process(WatchedEvent event) {
			// TODO Auto-generated method stub
			System.out.println(event.toString());
		}
		 
	 };
	 // ��ʼ�� ZooKeeper ʵ��
	 private void createZKInstance() throws IOException{
		 zk=new ZooKeeper("127.0.0.1:2181",SESSION_TIMEOUT,wh);
	 }
     private void ZKOperations() throws KeeperException, InterruptedException{
    	 
//    	  System.out.println("/n1. ���� ZooKeeper �ڵ� (znode �� zoo2, ���ݣ� myData2 ��Ȩ�ޣ�OPEN_ACL_UNSAFE ���ڵ����ͣ� Persistent");
    	            zk.create("/zoo2","myData2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    	            
//    	             System.out.println("/n2. �鿴�Ƿ񴴽��ɹ��� ");
    	              System.out.println(new String(zk.getData("/zoo2",false,null)));
    	                           
//    	           System.out.println("/n3. �޸Ľڵ����� ");
    	             zk.setData("/zoo2", "shenlan211314".getBytes(), -1);
    	         
//                System.out.println("/n4. �鿴�Ƿ��޸ĳɹ��� ");
    	             System.out.println(new String(zk.getData("/zoo2", false, null)));
    	                          
//    	            System.out.println("/n5. ɾ���ڵ� ");
    	            zk.delete("/zoo2", -1);
    	         
//               System.out.println("/n6. �鿴�ڵ��Ƿ�ɾ���� ");
    	           System.out.println(" �ڵ�״̬�� ["+zk.exists("/zoo2", false)+"]");
    	 
     }
     
     
     
     private void ZKClose() throws  InterruptedException
          {
                 zk.close();
        }
     
     public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
    	 first_Demo dm=new first_Demo();
                     dm.createZKInstance( );
                     dm.ZKOperations();
                     dm.ZKClose();
	}
	
}
