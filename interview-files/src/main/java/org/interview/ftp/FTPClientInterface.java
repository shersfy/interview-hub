package org.interview.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.interview.beans.FTPProtocolType;
import org.interview.beans.FileType;
import org.interview.beans.FtpFileMeta;
import org.interview.beans.FtpMeta;
import org.interview.exception.StandardException;
import org.interview.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class FTPClientInterface {
	
	protected static Logger logger  = LoggerFactory.getLogger(FTPClientInterface.class);
	
	private String name;
	protected FtpMeta server;

	/**
	 * 获取ftp对象
	 * 
	 * @param server ftp服务信息
	 * @return ftp连接对象
	 */
	public static FTPClientInterface getInstance(FtpMeta server){
		FTPClientInterface client = null;
		if(server == null){
			return null;
		}
		if(FTPProtocolType.SFTP == server.getProtocolType()){
			client = new SFTPAdapter();
		} else {
			client = new FTPAdapter();
		}
		client.setServer(server);
		client.setName(server.getName());
		return client;
	}

	/**
	 * 登录FTP server, 返回ftp客户端对象
	 * 
	 * @author PengYang
	 * @date 2017-08-28
	 * 
	 * @param info ftp 连接信息
	 * @return ftp客户端对象
	 * @throws StandardException
	 */
	public abstract FTPClientInterface connect() throws StandardException;
	/**
	 * 退出登录
	 * 
	 * @throws StandardException
	 */
	public abstract void logout();

	/**
	 * 关闭连接
	 *
	 */
	public abstract void disconnect() throws StandardException;
	/**连接已经关闭**/
	public abstract boolean isClosed();
	/**
	 * 获取ftp文件输入流, 使用完毕必须通过closeFtpInputStream(InputStream input) 关闭流;
	 * 
	 * @author PengYang
	 * @date 2017-08-28
	 * 
	 * @param client ftp客户端对象
	 * @param filePath 文件路径
	 * @return ftp文件输入流
	 * @throws StandardException
	 */
	public abstract InputStream getFtpInputStream(String filePath) throws StandardException;
	/**
	 * 下载文件到本地
	 * 
	 * @param remotePath 远程ftp文件路径
	 * @param localPath 本地文件路径
	 * @return 本地文件
	 * @throws StandardException
	 */
	public File downloadToLocal(String remotePath, String localPath) throws StandardException{
		if(StringUtils.isBlank(remotePath) || StringUtils.isBlank(localPath)){
			throw new StandardException("file name can not empty");
		}
		
		InputStream input   = null;
		OutputStream output = null;
		File file = new File(localPath);
		try {
			
			if(!file.getParentFile().isDirectory()){
				file.getParentFile().mkdirs();
			}
			
			input  = getFtpInputStream(remotePath);
			output = new FileOutputStream(file, false);
			logger.debug("download to local, {}-->{}", remotePath, localPath);
			IOUtils.copyLarge(input, output, new byte[2048]);
			
		} catch (Exception e) {
			throw new StandardException(e);
		} finally {
			closeFtpInputStream(input);
			IOUtils.closeQuietly(output);
		}
		
		return null;
	}
	/**
	 * 关闭ftp文件流
	 * 
	 * @author PengYang
	 * @date 2017-12-01
	 * 
	 * @param input 输入流
	 * @return 
	 */
	public abstract void closeFtpInputStream(InputStream input);
	/**
	 * 列出指定目录下的所有ftp文件
	 * 
	 * @author PengYang
	 * @date 2017-08-28
	 * 
	 * @param ftpClient ftp客户端对象
	 * @param workDir 工作目录
	 * @return 指定目录下的所有ftp文件
	 * @throws StandardException
	 */
	public List<FtpFileMeta> listFiles(String workDir) throws StandardException{
		return this.listFiles(workDir, null, false, FileType.All);
	}

	/**
	 * 列出指定目录下的匹配的所有ftp文件或文件夹或全部
	 * 
	 * @author PengYang
	 * @date 2017-08-28
	 * 
	 * @param client ftp客户端对象
	 * @param workDir 工作目录
	 * @param pathPattern 路径匹配规则, 空表示匹配所有
	 * @param recursive 是否递归文件夹
	 * @param type 文件类型
	 * @return 匹配的ftp文件或文件夹
	 * @throws StandardException
	 */
	public List<FtpFileMeta> listFiles(String workDir, String pathPattern, boolean recursive, FileType type) throws StandardException{
		return this.listFiles(workDir, pathPattern, recursive, type, null);
	}
	/**
	 * 列出指定目录下的匹配的所有ftp文件或文件夹或全部
	 * 
	 * @author PengYang
	 * @date 2017-08-28
	 * 
	 * @param ftpClient ftp客户端对象
	 * @param workDir 工作目录
	 * @param pathPattern 路径匹配规则
	 * @param recursive 是否递归文件夹
	 * @param type 文件类型
	 * @param container 返回对象容器
	 * @return 匹配的ftp文件或文件夹
	 * @throws StandardException
	 */
	public List<FtpFileMeta> listFiles(String workDir, String pathPattern, boolean recursive, 
			FileType type, List<FtpFileMeta> container) throws StandardException{
		
		if(StringUtils.isNotBlank(pathPattern)){
			try {
				Pattern.compile(pathPattern);
			} catch (Exception e) {
				String err = "compile regular expression error: %s";
				err = String.format(err, pathPattern);
				throw new StandardException(err);
			}
		}
		
		return null;
	}

	/**
	 * 重命名ftp文件, 没有发生异常操作成功
	 * 
	 * @param oldname 旧文件名
	 * @param newname 新文件名
	 * @throws StandardException
	 */
	public abstract void rename(String oldname, String newname) throws StandardException;
	/**
	 * 删除ftp文件, 没有发生异常操作成功
	 * 
	 * @param filename ftp文件名
	 * @throws StandardException
	 */
	public abstract void delete(String filename) throws StandardException;
	/**
	 * 移动ftp文件, 没有发生异常操作成功
	 * 
	 * @param srcFilename ftp文件名
	 * @param directory ftp目录
	 * @throws StandardException
	 */
	public void move(String srcFilename, String directory) throws StandardException {
		String newname = FilenameUtils.getName(srcFilename);
		newname = FileUtil.concat(directory, newname);
		this.rename(srcFilename, newname);
	}
	
	public abstract void mkdir(String path)  throws StandardException;
	/**
	 * 文件或目录是否存在
	 * 
	 * @param path 文件或目录路径
	 * @return
	 * @throws StandardException
	 */
	public boolean exist(String path) throws StandardException{
		if(StringUtils.isBlank(path)){
			return false;
		}
		File file = new File(path);
		String parent = file.getParent();
		List<FtpFileMeta> list = this.listFiles(parent, null, false, FileType.All);
		for(FtpFileMeta obj: list){
			if(file.getAbsolutePath().equals(new File(obj.getPath()).getAbsolutePath())){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 返回执行结果
	 * 
	 * @return
	 */
	public abstract String getReplyString();
	
	public void printReplyString(String...params){
		String name = server.getName();
		String info = this.getReplyString();
		info = info==null?"":info.replaceAll("\n|\r\n", "");
		logger.info("{}, {} {}", name, info, 
				params==null||params.length==0?"": Arrays.toString(params).replaceAll("\\[|\\]", ""));
	}
	
	public FtpMeta getServer() {
		return server;
	}

	public void setServer(FtpMeta server) {
		this.server = server;
	}

	public abstract String getHomeDir();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
