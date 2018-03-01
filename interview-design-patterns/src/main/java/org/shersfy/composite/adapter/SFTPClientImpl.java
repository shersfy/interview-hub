package org.shersfy.composite.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.interview.exception.StandardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPClientImpl {
	
	private static final Logger LOGGER  = LoggerFactory.getLogger(SFTPClientImpl.class);
	
	private ChannelSftp chnSftp;
	private Session session;
	private String replyString;
	
	
	public SFTPClientImpl connectSFTP(String host, int port, String user, String password) 
			throws StandardException {
		
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, port);
	        session.setPassword(password);
	        
	        Properties sshConfig = new Properties();
	        sshConfig.put("StrictHostKeyChecking", "no");
	       
	        session.setConfig(sshConfig);
	        session.connect();
	        
	        Channel channel = session.openChannel("sftp");
	        channel.connect();
	        chnSftp = (ChannelSftp) channel;
	        replyString = "Connected to " + host;
	        LOGGER.info("Connected to {}:{}", host, port);
		} catch (Exception e) {
			throw new StandardException(e, "FTP login failed, please check configuration parameter");
		}
		return this;
	}
	
	public List<String> listFiles(String path) {
		List<String> list = new ArrayList<>();
		try {
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> vector = chnSftp.ls(path);
			for(ChannelSftp.LsEntry file : vector){
				list.add(file.getFilename());
			}
			
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return list;
	}

	public ChannelSftp getChnSftp() {
		return chnSftp;
	}

	public Session getSession() {
		return session;
	}

	public String getReplyString() {
		return replyString;
	}
	
}
