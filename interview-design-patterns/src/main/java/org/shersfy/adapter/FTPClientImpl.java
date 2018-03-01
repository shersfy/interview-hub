package org.shersfy.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.interview.exception.StandardException;
import org.interview.utils.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPClientImpl {
	
	private static final Logger LOGGER  = LoggerFactory.getLogger(FTPClientImpl.class);

	private FTPClient ftp;
	
	public FTPClientImpl connectFTP(String host, int port, String user, String password)
			throws StandardException {
		try {
			
			if(ftp==null){
				ftp = new FTPClient();
			}
			// 连接超时
			ftp.setConnectTimeout(30000);
			ftp.connect(host, port);
			LOGGER.info(ftp.getReplyString());
			if (!ftp.login(user, password)) {
				throw new StandardException("FTP login failed, username or password is incorrect");
			}
			ftp.setControlEncoding(CharsetUtil.getUTF8().toString());
//			ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			// 客户端被动模式：服务器打开端口, 服务端去连接
			ftp.enterLocalPassiveMode();
			// ftp server 远程是否校验提交的IP地址和自己的IP地址相同
			ftp.setRemoteVerificationEnabled(false);
			// 使用外网IP或使用VPN时特殊配置, 被动模式可以通过ftp server端口
			ftp.setUseEPSVwithIPv4(true);
			LOGGER.info(ftp.getReplyString());
		} catch (Exception e) {
			throw new StandardException(e, "FTP login failed, please check configuration parameter");
		}
		
		return this;
	}
	
	public List<String> listFiles(String path) {
		List<String> list = new ArrayList<>();
		try {
			FTPFile[] files = ftp.listFiles(path);
			for(FTPFile file : files) {
				list.add(file.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public FTPClient getFtp() {
		return ftp;
	}
}
