package org.interview.big.data.zookeeper;

import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.interview.exception.StandardException;

public class ClientAppWatcher {

	static ZKMeta meta = new ZKMeta("demo10.leap.com:2181,demo8.leap.com:2181,demo9.leap.com:2181", 5000);
	/**
	 * 客户端应用程序节点变化，把一个客户端app看做一个节点，监听节点上线或下线
	 * 1. 创建临时节点， 客户端APP zk保持长连接
	 * 2. 监控临时节点目录，获取数据
	 * 3. 若客户端app服务挂掉, 监控目录下节点数减少，触发watcher的process方法
	 * 
	 * @author shersfy
	 * @date 2018-03-16
	 * 
	 * @param args
	 * @throws StandardException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws StandardException, InterruptedException {
		
		
		ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		
		Watcher watcher = new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event);
			}
		};
		while(true) {
			List<String> nodes = connector.listNodes("/datahub", watcher);
			System.out.println("======nodes start========");
			for(String node:nodes) {
				System.out.println(node);
			}
			System.out.println("======nodes end========");
			Thread.sleep(2000);
		}
	}
}
