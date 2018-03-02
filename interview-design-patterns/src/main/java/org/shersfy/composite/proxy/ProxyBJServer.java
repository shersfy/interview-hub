package org.shersfy.composite.proxy;

/**
 * 北京服务器, 充当代理服务器访问美国服务器，已授权可访问
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class ProxyBJServer implements IServer{

	private USServer usServer;
	
	public ProxyBJServer() {
		usServer = new USServer("bjfewoffe");
	}
	
	public String accessResource() {
		// USServer为真实实现类
		return usServer.accessResource();
	}

}
