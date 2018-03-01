package org.shersfy.adapter;

import java.util.List;

import org.interview.exception.StandardException;

/**
 * 适配器模式（Adapter Pattern）<br/>
 * 适配器模式是作为两个不兼容的接口之间的桥梁。 这种类型的设计模式属于结构型模式，它结合了两个独立接口的功能。<br/>
 * 
 * 例：<br/>
 * 1. ftp有两种不同的协议类型，ftp和sftp，ftp默认端口号21，sftp默认端口号22;<br/>
 * 2. 两种协议获取连接的接口不兼容，有着不同的实现类FTPClientImpl, SFTPClientImpl;<br/>
 * 3. 统一定义一个接口FTPAdapterInterface.connect()，使得同时兼容两种不同的ftp协议，支持两种协议类型;<br/>
 * 4. 使用不同的方式去实现接口，即使用不同方法去适配该接口 ，这就是适配器。可以写多个适配器，只要实现接口即可。
 * 
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class FTPAdapter implements FTPAdapterInterface{
	
	private FTPClientImpl ftp;
	private SFTPClientImpl sftp;
	
	@Override
	public void connect(String host, int port, String user, String password) 
			throws StandardException {
		
		if(port == 22) {
			sftp = new SFTPClientImpl();
			sftp.connectSFTP(host, port, user, password);
		} else {
			ftp = new FTPClientImpl();
			ftp.connectFTP(host, port, user, password);
		}
	}

	@Override
	public List<String> listFiles(String path) {
		if(sftp!=null) {
			return sftp.listFiles(path);
		}
		
		return ftp.listFiles(path);
	}

}
