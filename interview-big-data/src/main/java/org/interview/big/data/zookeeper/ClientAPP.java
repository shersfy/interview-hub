package org.interview.big.data.zookeeper;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.interview.exception.StandardException;

public class ClientAPP {

	static ZKMeta meta = new ZKMeta("demo10.leap.com:2181,demo8.leap.com:2181,demo9.leap.com:2181", 5000);
	
	public static void main(String[] args) throws StandardException, InterruptedException {
		ZookeeperConnector connector = ZookeeperConnector.getInstance(meta);
		connector.createNode("/datahub/"+args[0], "datahub client", CreateMode.EPHEMERAL);
		CountDownLatch latch = new CountDownLatch(1);
		latch.await();
	}

}
