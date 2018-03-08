package org.interview.jgroups.cluster;

public interface ClusterListener {
	
	/**
	 * 监听收到的信息
	 * 
	 * @author shersfy
	 * @date 2018-03-07
	 * 
	 * @param message
	 */
	public void listen(String message);
	
	/**
	 * 使用完监听器，移除监听
	 * 
	 * @author shersfy
	 * @date 2018-03-07
	 *
	 */
	public void destroy();
	
}
