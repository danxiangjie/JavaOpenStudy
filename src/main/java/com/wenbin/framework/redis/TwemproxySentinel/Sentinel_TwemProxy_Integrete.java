package com.wenbin.framework.redis.TwemproxySentinel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
/*
 *  利用 redis 的pubsub 监听 sentinel中  的+switch-master 通道，若发生 主从切换，则 监听到 主机 host：port 改变，与twemproxy 配置文件 监控的 主机信息比对，若二者不一致，
 *  则，重写 twemproxy配置文件，并利用shell 脚本重启.
 *  
 *  
 * */
public class Sentinel_TwemProxy_Integrete {

	private static final String NUTCRACKERYMLNAME = "F:/EclipseForJSE/A_redishigher/src/main/java/nutcracker.yml";
	private static final String CHANNEL = "+switch-master";// redis中主从切换时的 channel，即 主题
	private static Map<String, HostAndPort> curMap = new HashMap<String, HostAndPort>();
	private static final List<String> otherNutCrackerContent = new ArrayList<String>();
	private static boolean createNewNutcrackerYml = false;

	public static void main(String[] args) {

		final SubClient sb = new SubClient("192.168.20.103", 26379, curMap);

		// while(true){
		Thread sub = new Thread(new Runnable() {
			@Override
			public void run() {
				sb.subscribe(CHANNEL);
			}
		});
		sub.setDaemon(true);
		sub.start();

		 try {
		 Thread.sleep(80000);
		 } catch (Exception e) {
		 System.out.println(e);
		 } finally {
			 sb.unsubscribe(CHANNEL);
		 }
		curMap = sb.getMap();
		
		show(curMap);
		
		compareLastMapAndCurMapIsSame();

		if (createNewNutcrackerYml) {

			updateNutcrackerYml();
			// 重新启动
			executeShell0();
		}

	}

	public static void show(Map<?, ?> map) {

		Set<?> set = map.entrySet();
		Iterator<?> it = set.iterator();
		while (it.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) it.next();
			System.out.println("sentinel find  the master change to "
					+ entry.getKey() + "---" + entry.getValue());
		}
	}

	// 比较twemproxymproxy配置文件中 与 sentinel模块返回的 master 的host:port 是否一致

	public static Map<String, HostAndPort> getMapFromNutcrackerYml() {

		File f = new File(NUTCRACKERYMLNAME);
		BufferedReader in = null;
		Map<String, HostAndPort> lastMap = null;

		try {
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String len = "";
		try {
			while ((len = in.readLine()) != null) {
				if (!(len.startsWith("-"))) {
					otherNutCrackerContent.add(len);
				} else {
					len = len.replaceAll("-", "");
					System.out.println(len);
					String[] tmp = len.split(":");
					if (tmp.length == 3) {
						String[] ttmp = tmp[2].split(" ");
						curMap.put(
								ttmp[1],
								new HostAndPort(tmp[0], Integer
										.parseInt(tmp[1])));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(" 配置文件中的lastMap 是否为空"+(null==lastMap));
		return lastMap;
	}

	public static void compareLastMapAndCurMapIsSame() {

		HashMap<String, HostAndPort> lastMap = (HashMap<String, HostAndPort>) getMapFromNutcrackerYml();
		if (null != lastMap) {
			if (lastMap.size() == curMap.size()) {
				Set<Entry<String, HostAndPort>> lastVset = lastMap.entrySet();
				Set<Entry<String, HostAndPort>> curVset = curMap.entrySet();
				Iterator<Entry<String, HostAndPort>> lvit = lastVset.iterator();
				Iterator<Entry<String, HostAndPort>> cvit = curVset.iterator();

				while (lvit.hasNext()) {
					Entry<String, HostAndPort> lentry = lvit.next();
					Entry<String, HostAndPort> centry = cvit.next();
					if (!lentry.getValue().equals(centry.getValue())) {
						System.out.println("二者map不相同 ");
						createNewNutcrackerYml = true;
						otherNutCrackerContent.add(" - " + centry.getValue()
								+ ":1 " + centry.getKey());
					}
				}
			}
		}
	}

	// 若上面返回 true ，则 更新tewmproxy配置文件中的 server值并重启Twemproxy
	public static void updateNutcrackerYml() {
		File f = new File(NUTCRACKERYMLNAME);
		if (f.exists()) {
			f.delete();
		}
		f = new File(NUTCRACKERYMLNAME);
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (int i = 0, len = otherNutCrackerContent.size(); i < len; i++) {
			try {
				System.out.println("新配置文件 有  "+len+" 行");
				out.write(otherNutCrackerContent.get(i)+"\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void executeShell0() {
		System.out.println("成功重啟");
	}

	// 重新调用脚本-重启TwemProxy
	public static void executeShell() {
		String shellPath = "F:/EclipseForJSE/A_redishigher/src/main/java/restartTwemProxy.sh";
		Runtime run = Runtime.getRuntime();
		try {
			run.exec(shellPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}