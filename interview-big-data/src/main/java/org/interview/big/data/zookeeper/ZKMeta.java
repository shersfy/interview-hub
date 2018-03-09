package org.interview.big.data.zookeeper;

import org.interview.beans.BaseMeta;

public class ZKMeta extends BaseMeta{
	
	/**ZooKeeper集合主机**/
	private String connectionString;
	/**会话超时（以毫秒为单位）**/
	private int sessionTimeout;
	
	public ZKMeta() {
		super();
	}
	
	public ZKMeta(String connectionString, int sessionTimeout) {
		super();
		this.connectionString = connectionString;
		this.sessionTimeout = sessionTimeout;
	}
	public String getConnectionString() {
		return connectionString;
	}
	public int getSessionTimeout() {
		return sessionTimeout;
	}
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	/**
	 * 设置会话超时（以毫秒为单位）时间
	 * 
	 * @param sessionTimeout
	 */
	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
}
