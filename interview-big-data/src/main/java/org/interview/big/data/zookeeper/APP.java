package org.interview.big.data.zookeeper;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.interview.exception.StandardException;
import org.junit.Test;

/**
 * ZooKeeper是一种分布式协调服务，用于管理大型主机。
 * 在分布式环境中协调和管理服务是一个复杂的过程。
 * ZooKeeper通过其简单的架构和API解决了这个问题。
 * ZooKeeper允许开发人员专注于核心应用程序逻辑，而不必担心应用程序的分布式特性。
 * https://www.w3cschool.cn/zookeeper/zookeeper_api.html
 *
 */
public class APP {
	
	static ZKMeta meta = new ZKMeta("demo10.leap.com:2181,demo8.leap.com:2181,demo9.leap.com:2181", 5000);
	
	public static void main( String[] args ) throws StandardException{
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
		String data = new String(connector.getData("/leapid"));
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
	
}
