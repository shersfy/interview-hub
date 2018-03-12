package org.interview.big.data.zookeeper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.interview.exception.StandardException;
import org.junit.Test;

/**
 * ZooKeeper是一种分布式协调服务，用于管理大型主机。
 * 在分布式环境中协调和管理服务是一个复杂的过程。
 * ZooKeeper通过其简单的架构和API解决了这个问题。
 * ZooKeeper允许开发人员专注于核心应用程序逻辑，而不必担心应用程序的分布式特性。<br/>
 * https://www.w3cschool.cn/zookeeper/zookeeper_api.html<br/>
 *
 * 主要用途：<br/>
 * 1. 集中式配置管理<br/>
 * 2. 节点数据共享<br/>
 * 
 * Apache Solr是一个用Java编写的快速，开源的搜索平台。<br/>
 * 1. 
 */
public class APP {
	
	static ZKMeta meta = new ZKMeta("demo10.leap.com:2181,demo8.leap.com:2181,demo9.leap.com:2181", 5000);
	
	public static void main( String[] args ) throws StandardException, InterruptedException{
		final ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		final CountDownLatch latch = new CountDownLatch(2);
		final String path = "/datahub";
		Thread th1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String data = "datahub";
				try {
					for(int i=0; i<5; i++) {
						connector.setData(path, (data+i).getBytes());
						System.out.println("set data: "+data+i);
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				latch.countDown();
			}
		});
		
		Thread th2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					for(int i=0; i<5; i++) {
						System.out.println("get data:"+new String(connector.getData(path)));
						Thread.sleep(1000);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				latch.countDown();
			}
		});
		
		th1.start();
		th2.start();
		
		latch.await();
		
		connector.close();
	}
	
	@Test
	public void test0() throws StandardException {
		ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		List<String> list = connector.listNodes("/");
		System.out.println("========start============");
		for(String node : list) {
			System.out.println(node);
		}
		System.out.println("========end============");
		connector.close();
	}
	
	
	@Test
	public void test01() throws StandardException {
		ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		String data = new String(connector.getData("/datahub"));
		System.out.println("========start============");
		System.out.println(data);
		System.out.println("========end============");
		connector.close();
	}
	
	@Test
	public void test02() throws StandardException {
		ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		System.out.println("========start============");
		System.out.println(connector.createNode("/datahub/node", "datahub", CreateMode.PERSISTENT_SEQUENTIAL));
		System.out.println("========end============");
		connector.close();
	}
	
	@Test
	public void test03() throws StandardException {
		ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		System.out.println("========start============");
		connector.deleteNode("/datahub", true);
		System.out.println("========end============");
		connector.close();
	}
	
	@Test
	public void test04() throws StandardException {
		ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		System.out.println("========start============");
		String data = "key=baidu\nurl=https://www.baidu.com1";
		connector.setData("/datahub", data.getBytes());
		System.out.println("========end============");
		connector.close();
	}
	
}
