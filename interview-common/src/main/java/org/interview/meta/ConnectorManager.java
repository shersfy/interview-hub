package org.interview.meta;

/**
 * 连接器管理器接口
 * @author shersfy
 * @date 2018-02-27
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public interface ConnectorManager {
	
	/**默认最大连接数**/
	public static int DEFAULT_MAX_CONNECTIONS = 1000;
	
	/**
	 * 获取允许访问的最大连接数
	 * 
	 * @author PengYang
	 * @date 2017-11-07
	 * 
	 * @return 最大连接数
	 */
	public int getMaxConnections();

	/**
	 * 使用连接
	 * 
	 * @author PengYang
	 * @date 2017-11-07
	 *
	 */
	public boolean useConnection();
	/**
	 * 释放连接
	 * 
	 * @author PengYang
	 * @date 2017-11-07
	 *
	 */
	public boolean releaseConnection();

}
