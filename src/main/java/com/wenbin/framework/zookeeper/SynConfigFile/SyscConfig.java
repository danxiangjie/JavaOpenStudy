package com.synconfig;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;


/***
 * Zookeeper实现分布式配置同步
 * 
 * @author 秦东亮
 * 
 * ***/
public class SyscConfig   implements Watcher{
  
  //Zookeeper实例
  private ZooKeeper zk;
  private CountDownLatch countDown=new CountDownLatch(1);//同步工具
  private static final int TIMIOUT=5000;//超时时间
  private static final String PATH="/sanxian";
  public SyscConfig(String hosts) {
     
  try{
    zk=new ZooKeeper(hosts, TIMIOUT, new Watcher() {
      
      @Override
      public void process(WatchedEvent event) {
         
        if(event.getState().SyncConnected==Event.KeeperState.SyncConnected){
          //防止在未连接Zookeeper服务器前，执行相关的CURD操作
          countDown.countDown();//连接初始化，完成，清空计数器
        }
        
      }
    });
    
  }catch(Exception e){
    e.printStackTrace();
  }
  }
  
  
  
  /***
   * 写入或更新
   * 数据
   * @param path 写入路径
   * @param value 写入的值
   * **/
  public void addOrUpdateData(String path,String data)throws Exception {
    
    
    Stat stat=zk.exists(path, false);
    if(stat==null){
        //没有就创建，并写入		
      zk.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    System.out.println("新建，并写入数据成功.. ");
    }else{  
      //存在，就更新
      zk.setData(path, data.getBytes(), -1);
      System.out.println("更新成功!");
    }
  }
  
  /**
   * 读取数据
   * @param path 读取的路径
   * @return 读取数据的内容
   * 
   * **/
  public String readData()throws Exception{
    
    String s=new String(zk.getData(PATH, this, null));
    
    return s;  
  }
  
  
  /**
   * 关闭zookeeper连接
   * 释放资源
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
   
     conf.addOrUpdateData(PATH, "修真天劫，九死一生。");
     conf.addOrUpdateData(PATH, "圣人之下，皆为蝼蚁，就算再大的蝼蚁，还是蝼蚁.");
     conf.addOrUpdateData(PATH, "努力奋斗，实力才是王道！ ");
  
  //System.out.println("监听器开始监听........");
  // conf.readData();
  // Thread.sleep(Long.MAX_VALUE);
  //conf.readData();
  conf.close();
  
}

  @Override
  public void process(WatchedEvent event){
     try{
    if(event.getType()==Event.EventType.NodeDataChanged){
      System.out.println("变化数据:  "+readData());
    }
     }catch(Exception e){
       e.printStackTrace();
     }
    
  }
}