package org.interview.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.function.Predicate;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.interview.beans.FileType;
import org.interview.beans.FtpFileMeta;
import org.interview.exception.StandardException;
import org.interview.utils.AesUtil;
import org.interview.utils.FileUtil;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SFTPAdaptor extends FTPClientAdaptor {
	
	private ChannelSftp chnSftp;
	private Session session;
	private String replyString;
	
	public SFTPAdaptor(){}
	
	@Override
	public FTPClientAdaptor connect() throws StandardException {
		
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(server.getUserName(), server.getHost(), server.getPort());
	        session.setPassword(AesUtil.decryptStr(server.getPassword(), AesUtil.AES_SEED));
	        
	        Properties sshConfig = new Properties();
	        sshConfig.put("StrictHostKeyChecking", "no");
	       
	        session.setConfig(sshConfig);
	        session.connect();
	        
	        Channel channel = session.openChannel("sftp");
	        channel.connect();
	        chnSftp = (ChannelSftp) channel;
	        replyString = "Connected to " + server.getHost();
	        printReplyString();
		} catch (Exception e) {
			throw new StandardException(e, "FTP login failed, please check configuration parameter");
		}
		return this;
	}

	@Override
	public void logout() {
		if(session!=null){
			try {
				chnSftp.disconnect();
				session.disconnect();
				chnSftp.exit();
				replyString = "logout successful";
				printReplyString();
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}

	@Override
	public void disconnect() throws StandardException {
		if(session!=null){
			chnSftp.disconnect();
			session.disconnect();
			replyString = "disconnected to " + server.getHost();
			printReplyString();
		}
	}

	@Override
	public InputStream getFtpInputStream(String filePath) throws StandardException {
		InputStream instream = null;
		if(chnSftp == null || StringUtils.isBlank(filePath)){
			return null;
		}
		
		try {
			replyString = "Opening BINARY mode data connection for " + filePath;
			printReplyString();
			instream = chnSftp.get(filePath);
		} catch (SftpException e) {
			throw new StandardException(e, "get ftp file input stream exception[%s]", filePath);
		}
		
    	return instream;
	}

	@Override
	public List<FtpFileMeta> listFiles(String workDir, String pathPattern, boolean recursive, FileType type,
			List<FtpFileMeta> container) throws StandardException {
		
		super.listFiles(workDir, pathPattern, recursive, type, container);
		
		if(container == null){
			container = new ArrayList<FtpFileMeta>();
		}
		if(StringUtils.isBlank(workDir)){
			return container;
		}
		
		try {
			workDir = workDir.replace(File.separatorChar, '/');
			chnSftp.cd(workDir);
			
			replyString = "Listing directory " + workDir;
			printReplyString();
			
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> vector = chnSftp.ls(workDir);
			if(vector == null){
				return container;
			}
			
			vector.removeIf(new Predicate<ChannelSftp.LsEntry>() {

				@Override
				public boolean test(LsEntry entry) {
					String filename = entry.getFilename();
					SftpATTRS attrs = entry.getAttrs();
					if(filename.startsWith(".")){
						return true;
					}
					
					if(attrs.isDir()){
						return false;
					}
					
					if(StringUtils.isBlank(pathPattern) ){
						return false;
					}
//					name = StringEscapeUtils.unescapeJava(name);
//					// .号原样处理
//					String pattern = pathPattern.replace(".", "\\.");
//					// *号表示任意字符任意长度
//					pattern = pattern.replace("*", ".*");
//					// ?号表示任意字符1个字符长度
//					pattern = pattern.replace("*", ".*");
					return !filename.matches(pathPattern);
				}
			});
			

			for(ChannelSftp.LsEntry file : vector){
				String path = FileUtil.concat(workDir, file.getFilename());
				if(recursive && file.getAttrs().isDir()){
					if(FileType.File != type){
						addToContainer(path, file, container);
					}
					listFiles(path, pathPattern, recursive, type, container);
					continue;
				}
				
				if(FileType.Directory == type && !file.getAttrs().isDir()){
					continue;
				}
				if(FileType.File == type && file.getAttrs().isDir()){
					continue;
				}
				
				addToContainer(path, file, container);
			}

		} catch (Exception e) {
			throw new StandardException("list FTP files and directory exception", e);
			//throw new StandardException("列举FTP文件和目录异常", e);
		}
		container.sort(new Comparator<FtpFileMeta>() {

			@Override
			public int compare(FtpFileMeta o1, FtpFileMeta o2) {
				return o1.getPath().compareTo(o2.getPath());
			}
		});
		return container;
	}
	
	private void addToContainer(String path, LsEntry entry, List<FtpFileMeta> container){
		
		FtpFileMeta fileMeta = new FtpFileMeta();
		fileMeta.setName(entry.getFilename());
		fileMeta.setPath(path);
		fileMeta.setSize(entry.getAttrs().getSize());
		fileMeta.setDirectory(entry.getAttrs().isDir());
		fileMeta.setFile(!entry.getAttrs().isDir());
		
		fileMeta.setGroup(String.valueOf(entry.getAttrs().getGId()));
		fileMeta.setUser(String.valueOf(entry.getAttrs().getUId()));
		fileMeta.setRawListing(entry.getAttrs().getPermissionsString());
		
		container.add(fileMeta);
	}

	@Override
	public String getHomeDir() {
		String home = "/";
		try {
			home = chnSftp.getHome();
			replyString = "home " + home;
			printReplyString();
		} catch (Exception e) {
			logger.error("", e);
		}
		return home;
	}

	@Override
	public void rename(String oldname, String newname) throws StandardException {
		try {
			String tardir = FilenameUtils.getFullPath(newname);
			if(!this.exist(tardir)){
				String err = String.format("directory not exist %s", tardir);
				throw new IOException(err);
			}
			replyString = String.format("rename %s to %s", oldname, newname);
			printReplyString();
			chnSftp.rename(oldname, newname);
		} catch (Exception e) {
			if(e instanceof StandardException){
				throw (StandardException)e;
			}
			throw new StandardException(e);
		}
		
	}

	@Override
	public void delete(String filename) throws StandardException {
		try {
			replyString = String.format("remove %s", filename);
			printReplyString();
			chnSftp.rm(filename);
		} catch (SftpException e) {
			throw new StandardException(e);
		}
	}

	@Override
	public String getReplyString() {
		return replyString;
	}

	@Override
	public void mkdir(String path) throws StandardException {

		try {
			if(StringUtils.isBlank(path)){
				return;
			}
			File file = new File(path);
			String parent = file.getParent();
			try {
				this.chnSftp.cd(encodingPath(parent));
			} catch (SftpException e) {
				if(!StringUtils.containsIgnoreCase(e.getMessage(), "No such file")){
					throw e;
				}
				this.mkdir(encodingPath(parent));
			}
			replyString = String.format("mkdir %s", path);
			printReplyString();
			this.chnSftp.mkdir(encodingPath(path));
			
		} catch (Exception e) {
			if(e instanceof StandardException){
				throw (StandardException)e;
			}
			throw new StandardException(e);
		}
	
	}
	
	private String encodingPath(String path){
		if(path!=null){
			path = path.replace(File.separatorChar, '/');
		}
		return path;
	}

	@Override
	public boolean isClosed() {
		return this.chnSftp.isClosed();
	}

	@Override
	public void closeFtpInputStream(InputStream input) {
		IOUtils.closeQuietly(input);
	}


}
