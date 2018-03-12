package org.interview.big.data.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.interview.exception.StandardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperConnector implements Watcher{

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperConnector.class);
	private ZKMeta meta;
	private ZooKeeper zk;
	private CountDownLatch waitConnect;
	private ZookeeperConnector() {}

	private ZookeeperConnector(ZKMeta meta) throws IOException, InterruptedException {
		this.meta = meta;
		/**用于停止（等待）主进程，直到客户端与ZooKeeper集合连接**/
		waitConnect = new CountDownLatch(1);
		zk = new ZooKeeper(meta.getConnectionString(), meta.getSessionTimeout(), this);
		// 阻塞， 等待主线程
		waitConnect.await();
	}

	/**
	 * 创建实例
	 * 
	 * @author shersfy
	 * @date 2018-03-09
	 * 
	 * @param meta
	 * @return
	 * @throws StandardException
	 */
	public static ZookeeperConnector getInstance(ZKMeta meta) throws StandardException {
		ZookeeperConnector connector = null;
		try {
			connector = new ZookeeperConnector(meta);
		} catch (IOException | InterruptedException e) {
			throw new StandardException(e);
		}

		return connector;
	}
	
	@Override
	public void process(WatchedEvent event) {
		
		if(waitConnect.getCount()>0 && event.getState() == KeeperState.SyncConnected) {
			waitConnect.countDown();
		}
		
		LOGGER.info("======event: {}", event);
		
	}

	
	/**
	 * 创建节点
	 * 
	 * @author shersfy
	 * @date 2018-03-09
	 * 
	 * @param path 要创建节点的路径
	 * @param data 要创建节点的存储数据
	 * @param mode 节点的类型，即临时，顺序或两者
	 * @return 已创建节点实际路径
	 * @throws StandardException
	 */
	public String createNode(String path, String data, CreateMode mode) throws StandardException {
		String child = null;
		try {
			data = StringUtils.isBlank(data)?"":data;
			mode = mode==null?mode=CreateMode.PERSISTENT:mode;
			child = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
		} catch (KeeperException | InterruptedException e) {
			throw new StandardException(e);
		}
		return child;
	}
	
	/**
	 * 删除节点， 版本匹配才能被删除
	 * 
	 * @author shersfy
	 * @date 2018-03-09
	 * 
	 * @param path 指定待删除节点路径
	 * @param version 指定版本
	 * @throws StandardException
	 */
	public void deleteNode(String path, boolean recursion) throws StandardException {
		try {
			// -1 任意版本
			if(recursion) {
				List<String> childen = listNodes(path);
				for(String child :childen) {
					deleteNode(path+"/"+child, recursion);
				}
				LOGGER.debug("delete: {}", path);
				zk.delete(path, -1);
			} else {
				LOGGER.debug("delete: {}", path);
				zk.delete(path, -1);
			}
			
		} catch (KeeperException | InterruptedException e) {
			throw new StandardException(e);
		}
	}
	
	
	/**
	 * 列出指定节点下所有节点
	 * 
	 * @author shersfy
	 * @date 2018-03-09
	 * 
	 * @param path 指定节点路径
	 * @return 节点列表
	 * @throws StandardException
	 */
	public List<String> listNodes(String path) throws StandardException {
		try {
			path = StringUtils.isBlank(path)?"/":path;
			return zk.getChildren(path, this);
		} catch (KeeperException | InterruptedException e) {
			throw new StandardException(e);
		}
	}
	
	/**
	 * 给存在的节点设置数据
	 * 
	 * @author shersfy
	 * @date 2018-03-12
	 * 
	 * @param path 节点路径
	 * @param data 数据
	 * @throws StandardException
	 */
	public void setData(String path, byte[] data) throws StandardException {
		try {
			if(StringUtils.isBlank(path)) {
				return;
			}
			// version=-1 匹配任意版本
			zk.setData(path, data, -1);
		} catch (KeeperException | InterruptedException e) {
			throw new StandardException(e);
		}
	}
	
	/**
	 * 获取指定节点的数据
	 * 
	 * @author shersfy
	 * @date 2018-03-09
	 * 
	 * @param path 指定节点路径, 为空返回null
	 * @return 节点数据
	 * @throws StandardException
	 */
	public byte[] getData(String path) throws StandardException {
		byte[] data = null;
		try {
			if(StringUtils.isBlank(path)) {
				return null;
			}
			// watch=true, 使用构造方法中传入的watcher监听
			data = zk.getData(path, true, null);
		} catch (KeeperException | InterruptedException e) {
			throw new StandardException(e);
		}
		return data;
	}
	
	public void close() throws StandardException {
		try {
			this.zk.close();
		} catch (Exception e) {
			throw new StandardException(e);
		}
	}

	public ZKMeta getMeta() {
		return meta;
	}

	public void setMeta(ZKMeta meta) {
		this.meta = meta;
	}

	public ZooKeeper getZk() {
		return zk;
	}


}
