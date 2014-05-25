package com.synconfig;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;


/***
 * Zookeeperʵ�ֲַ�ʽ����ͬ��
 * 
 * @author �ض���
 * 
 * ***/
public class SyscConfig   implements Watcher{
  
  //Zookeeperʵ��
  private ZooKeeper zk;
  private CountDownLatch countDown=new CountDownLatch(1);//ͬ������
  private static final int TIMIOUT=5000;//��ʱʱ��
  private static final String PATH="/sanxian";
  public SyscConfig(String hosts) {
     
  try{
    zk=new ZooKeeper(hosts, TIMIOUT, new Watcher() {
      
      @Override
      public void process(WatchedEvent event) {
         
        if(event.getState().SyncConnected==Event.KeeperState.SyncConnected){
          //��ֹ��δ����Zookeeper������ǰ��ִ����ص�CURD����
          countDown.countDown();//���ӳ�ʼ������ɣ���ռ�����
        }
        
      }
    });
    
  }catch(Exception e){
    e.printStackTrace();
  }
  }
  
  
  
  /***
   * д������
   * ����
   * @param path д��·��
   * @param value д���ֵ
   * **/
  public void addOrUpdateData(String path,String data)throws Exception {
    
    
    Stat stat=zk.exists(path, false);
    if(stat==null){
        //û�оʹ�������д��		
      zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    System.out.println("�½�����д�����ݳɹ�.. ");
    }else{  
      //���ڣ��͸���
      zk.setData(path, data.getBytes(), -1);
      System.out.println("���³ɹ�!");
    }
  }
  
  /**
   * ��ȡ����
   * @param path ��ȡ��·��
   * @return ��ȡ���ݵ�����
   * 
   * **/
  public String readData()throws Exception{
    
    String s=new String(zk.getData(PATH, this, null));
    
    return s;  
  }
  
  
  /**
   * �ر�zookeeper����
   * �ͷ���Դ
   * 
   * **/
  public void close(){
    
    try{
      
      zk.close();
    }catch(Exception e){
      e.printStackTrace();
    }
    
  }

 
public static void main(String[] args)throws Exception {
  
  SyscConfig conf=new SyscConfig("10.2.143.5:2181");
   
     conf.addOrUpdateData(PATH, "������٣�����һ����");
     conf.addOrUpdateData(PATH, "ʥ��֮�£���Ϊ���ϣ������ٴ�����ϣ���������.");
     conf.addOrUpdateData(PATH, "Ŭ���ܶ���ʵ������������ ");
  
  //System.out.println("��������ʼ����........");
  // conf.readData();
  // Thread.sleep(Long.MAX_VALUE);
  //conf.readData();
  conf.close();
  
}

  @Override
  public void process(WatchedEvent event){
     try{
    if(event.getType()==Event.EventType.NodeDataChanged){
      System.out.println("�仯����:  "+readData());
    }
     }catch(Exception e){
       e.printStackTrace();
     }
    
  }
}