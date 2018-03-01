package org.interview.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.interview.beans.FileType;
import org.interview.beans.FtpFileMeta;
import org.interview.exception.StandardException;
import org.interview.utils.AesUtil;
import org.interview.utils.CharsetUtil;
import org.interview.utils.FileUtil;


public class FTPAdapter extends FTPClientInterface {
	
	private FTPClient ftp;
	
	public FTPAdapter(){}
	
	@Override
	public FTPClientInterface connect() throws StandardException {

		try {
			if(ftp==null){
				ftp = new FTPClient();
			}
			// 连接超时
			ftp.setConnectTimeout(30000);
			ftp.connect(server.getHost(), server.getPort());
			printReplyString();
			String pwd = AesUtil.decryptStr(server.getPassword(), AesUtil.AES_SEED);
			if (!ftp.login(server.getUserName(), pwd)) {
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
			printReplyString();
		} catch (StandardException de) {
			throw de;
		} catch (Exception e) {
			throw new StandardException(e, "FTP login failed, please check configuration parameter");
		}
		return this;
	
	}
	

	@Override
	public void logout(){
		if(this.ftp!=null && this.ftp.isConnected()){
			try {
				ftp.logout();
				printReplyString();
			} catch (Exception e) {
				logger.error(this.getReplyString(), e);
			}
		}
		
	}

	@Override
	public InputStream getFtpInputStream(String filePath)
			throws StandardException {
		
		InputStream ftpInput = null;
		if(ftp == null || StringUtils.isBlank(filePath)){
			return null;
		}
		try {
			filePath = filePath.replace(File.separatorChar, '/');
			ftpInput = ftp.retrieveFileStream(this.encodingPath(filePath));
			printReplyString();
			if(ftp.getReplyCode() == FTPReply.FILE_UNAVAILABLE){
				String err = this.getReplyString();
				err = err + ", Permission denied";
				throw new StandardException(err);
				//throw new StandardException(String.format("ftp文件不存在[%s]", filePath));
			}
			// 550:Permission denied
			if(ftpInput == null){
				String err = this.getReplyString();
				err = err + String.format(", FTP file does not exist [%s]", filePath);
				throw new StandardException(err);
				//throw new StandardException(String.format("ftp文件不存在[%s]", filePath));
			}
			
		} catch (StandardException de) {
			throw de;
		} catch (Exception e) {
			String err = this.getReplyString();
			err = err + String.format(", get ftp file input stream exception[%s]", filePath);
			throw new StandardException(e, err);
		}

		return ftpInput;	
	}

	@Override
	public List<FtpFileMeta> listFiles(String workDir, String pathPattern,
			boolean recursive, FileType type, List<FtpFileMeta> container) throws StandardException {
		
		super.listFiles(workDir, pathPattern, recursive, type, container);
		
		if(container == null){
			container = new ArrayList<FtpFileMeta>();
		}
		if(StringUtils.isBlank(workDir)){
			return container;
		}
		
		try {
			workDir = workDir.replace(File.separatorChar, '/');
			if(!workDir.equals(ftp.printWorkingDirectory())){
				if(!ftp.changeWorkingDirectory(this.encodingPath(workDir))){
					logger.error(ftp.getReplyString());
					return container;
				}
			}
			printReplyString(workDir);
//			FTPFile[] ftpFiles = (pathPattern == null||"*".equals(pathPattern))?ftp.listFiles():ftp.listFiles(pathPattern);
			FTPFile[] ftpFiles = ftp.listFiles();
			printReplyString();
			if(ftpFiles == null){
				return container;
			}

			for(FTPFile file : ftpFiles){
				String path = FileUtil.concat(workDir, file.getName());
				if(recursive && file.isDirectory()){
					if(FileType.File != type){
						addToContainer(path, file, container);
					}
					listFiles(path, pathPattern, recursive, type, container);
					continue;
				}
				
				if(FileType.Directory == type && file.isFile()){
					continue;
				}
				if(FileType.File == type && file.isDirectory()){
					continue;
				}
				// 正则匹配
				if(StringUtils.isNotBlank(pathPattern) && !file.getName().matches(pathPattern)){
					continue;
				}
				
				addToContainer(path, file, container);
			}

		} catch (Exception e) {
			String err = this.getReplyString();
			err = err + ", list FTP files and directory exception";
			throw new StandardException(err, e);
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
	
	private void addToContainer(String path, FTPFile file, List<FtpFileMeta> container) throws IllegalAccessException, 
		InvocationTargetException{

		FtpFileMeta fileMeta = new FtpFileMeta();
		BeanUtils.copyProperties(fileMeta, file);
		fileMeta.setPath(path);
		container.add(fileMeta);
	}

	@Override
	public void disconnect() throws StandardException {
		if(this.ftp!=null){
			try {
				this.ftp.disconnect();
				printReplyString();
			} catch (IOException e) {
				logger.error(this.getReplyString());
				throw new StandardException(e);
			}
		}
	}

	@Override
	public String getHomeDir() {
		String home = "/";
		try {
			home = ftp.printWorkingDirectory();
			printReplyString();
		} catch (Exception e) {
			logger.error(this.getReplyString(), e);
		}
		return home;
	}
	
	@Override
	public void rename(String oldname, String newname) throws StandardException {
		try {
			String tardir = FilenameUtils.getFullPath(newname);
			if(!this.exist(tardir)){
				String err = String.format("directory not exist %s", tardir);
				throw new StandardException(err);
			}
			// 支持中文
			if(!this.ftp.rename(encodingPath(oldname), encodingPath(newname))){
				throw new StandardException(this.getReplyString());
			}
			printReplyString();
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
			if(!this.ftp.deleteFile(encodingPath(filename))){
				String err = String.format("%s %s", ftp.getReplyString(), filename);
				throw new StandardException(err);
			}
			printReplyString();
		} catch (Exception e) {
			if(e instanceof StandardException){
				throw (StandardException)e;
			}
			throw new StandardException(e);
		}
		
	}
	
	@Override
	public void mkdir(String path) throws StandardException {
		try {
			if(StringUtils.isBlank(path)){
				return;
			}
			File file = new File(path);
			String parent = file.getParent();
			if(!this.ftp.changeWorkingDirectory(encodingPath(parent))){
				this.mkdir(parent);
			}
			if(!this.ftp.makeDirectory(encodingPath(path))){
				String err = String.format("%s %s", ftp.getReplyString(), path);
				throw new StandardException(err);
			}
			printReplyString();
		} catch (Exception e) {
			if(e instanceof StandardException){
				throw (StandardException)e;
			}
			throw new StandardException(e);
		}
	}

	@Override
	public String getReplyString() {
		String replyString = ftp.getReplyString();
		if(StringUtils.isBlank(replyString)){
			replyString = "200, COMMAND_OK";
		}
		
		replyString = "code="+ replyString;
		
		try {
			replyString = new String(replyString.getBytes("ISO8859-1"));
		} catch (Exception e) {
		}
		
		return replyString;
//		String meaning = "";
//		switch (code) {
//		case FTPReply.RESTART_MARKER:
//			meaning = "110, RESTART_MARKER";
//		case FTPReply.SERVICE_NOT_READY:
//			meaning = "120, SERVICE_NOT_READY";
//		case FTPReply.DATA_CONNECTION_ALREADY_OPEN:
//			meaning = "125, DATA_CONNECTION_ALREADY_OPEN";
//		case FTPReply.FILE_STATUS_OK:
//			meaning = "150, FILE_STATUS_OK";
//		case FTPReply.COMMAND_OK:
//			meaning = "200, COMMAND_OK";
//		case FTPReply.COMMAND_IS_SUPERFLUOUS:
//			meaning = "202, COMMAND_IS_SUPERFLUOUS";
//		case FTPReply.SYSTEM_STATUS:
//			meaning = "211, SYSTEM_STATUS";
//		case FTPReply.DIRECTORY_STATUS:
//			meaning = "212, DIRECTORY_STATUS";
//		case FTPReply.FILE_STATUS:
//			meaning = "213, FILE_STATUS";
//		case FTPReply.HELP_MESSAGE:
//			meaning = "214, HELP_MESSAGE";
//		case FTPReply.NAME_SYSTEM_TYPE:
//			meaning = "215, NAME_SYSTEM_TYPE";
//		case FTPReply.SERVICE_READY:
//			meaning = "220, SERVICE_READY";
//		case FTPReply.SERVICE_CLOSING_CONTROL_CONNECTION:
//			meaning = "221, SERVICE_CLOSING_CONTROL_CONNECTION";
//		case FTPReply.DATA_CONNECTION_OPEN:
//			meaning = "225, DATA_CONNECTION_OPEN";
//		case FTPReply.CLOSING_DATA_CONNECTION:
//			meaning = "226, CLOSING_DATA_CONNECTION";
//		case FTPReply.ENTERING_PASSIVE_MODE:
//			meaning = "227, ENTERING_PASSIVE_MODE";
//		case FTPReply.ENTERING_EPSV_MODE:
//			meaning = "229, ENTERING_EPSV_MODE";
//		case FTPReply.USER_LOGGED_IN:
//			meaning = "230, USER_LOGGED_IN";
//		case FTPReply.FILE_ACTION_OK:
//			meaning = "250, FILE_ACTION_OK";
//		case FTPReply.PATHNAME_CREATED:
//			meaning = "257, PATHNAME_CREATED";
//		case FTPReply.NEED_PASSWORD:
//			meaning = "331, NEED_PASSWORD";
//		case FTPReply.NEED_ACCOUNT:
//			meaning = "332, NEED_ACCOUNT";
//		case FTPReply.FILE_ACTION_PENDING:
//			meaning = "350, FILE_ACTION_PENDING";
//		case FTPReply.SERVICE_NOT_AVAILABLE:
//			meaning = "421, SERVICE_NOT_AVAILABLE";
//		case FTPReply.CANNOT_OPEN_DATA_CONNECTION:
//			meaning = "425, CANNOT_OPEN_DATA_CONNECTION";
//		case FTPReply.TRANSFER_ABORTED:
//			meaning = "426, TRANSFER_ABORTED";
//		case FTPReply.FILE_ACTION_NOT_TAKEN:
//			meaning = "450, FILE_ACTION_NOT_TAKEN";
//		case FTPReply.ACTION_ABORTED:
//			meaning = "451, ACTION_ABORTED";
//		case FTPReply.INSUFFICIENT_STORAGE:
//			meaning = "452, INSUFFICIENT_STORAGE";
//		case FTPReply.UNRECOGNIZED_COMMAND:
//			meaning = "500, UNRECOGNIZED_COMMAND";
//		case FTPReply.SYNTAX_ERROR_IN_ARGUMENTS:
//			meaning = "501, SYNTAX_ERROR_IN_ARGUMENTS";
//		case FTPReply.COMMAND_NOT_IMPLEMENTED:
//			meaning = "502, COMMAND_NOT_IMPLEMENTED";
//		case FTPReply.BAD_COMMAND_SEQUENCE:
//			meaning = "503, BAD_COMMAND_SEQUENCE";
//		case FTPReply.COMMAND_NOT_IMPLEMENTED_FOR_PARAMETER:
//			meaning = "504, COMMAND_NOT_IMPLEMENTED_FOR_PARAMETER";
//		case FTPReply.NOT_LOGGED_IN:
//			meaning = "530, NOT_LOGGED_IN";
//		case FTPReply.NEED_ACCOUNT_FOR_STORING_FILES:
//			meaning = "532, NEED_ACCOUNT_FOR_STORING_FILES";
//		case FTPReply.FILE_UNAVAILABLE:
//			meaning = "550, FILE_UNAVAILABLE";
//		case FTPReply.PAGE_TYPE_UNKNOWN:
//			meaning = "551, PAGE_TYPE_UNKNOWN";
//		case FTPReply.STORAGE_ALLOCATION_EXCEEDED:
//			meaning = "552, STORAGE_ALLOCATION_EXCEEDED";
//		case FTPReply.FILE_NAME_NOT_ALLOWED:
//			meaning = "553, FILE_NAME_NOT_ALLOWED";
//		case FTPReply.SECURITY_DATA_EXCHANGE_COMPLETE:
//			meaning = "234, SECURITY_DATA_EXCHANGE_COMPLETE";
//		case FTPReply.SECURITY_DATA_EXCHANGE_SUCCESSFULLY:
//			meaning = "235, SECURITY_DATA_EXCHANGE_SUCCESSFULLY";
//		case FTPReply.SECURITY_MECHANISM_IS_OK:
//			meaning = "334, SECURITY_MECHANISM_IS_OK";
//		case FTPReply.SECURITY_DATA_IS_ACCEPTABLE:
//			meaning = "335, SECURITY_DATA_IS_ACCEPTABLE";
//		case FTPReply.UNAVAILABLE_RESOURCE:
//			meaning = "431, UNAVAILABLE_RESOURCE";
//		case FTPReply.BAD_TLS_NEGOTIATION_OR_DATA_ENCRYPTION_REQUIRED:
//			meaning = "522, BAD_TLS_NEGOTIATION_OR_DATA_ENCRYPTION_REQUIRED, EXTENDED_PORT_FAILURE";
//		case FTPReply.DENIED_FOR_POLICY_REASONS:
//			meaning = "533, DENIED_FOR_POLICY_REASONS";
//		case FTPReply.REQUEST_DENIED:
//			meaning = "534, REQUEST_DENIED";
//		case FTPReply.FAILED_SECURITY_CHECK:
//			meaning = "535, FAILED_SECURITY_CHECK";
//		case FTPReply.REQUESTED_PROT_LEVEL_NOT_SUPPORTED:
//			meaning = "536, REQUESTED_PROT_LEVEL_NOT_SUPPORTED";
//			break;
//
//		default:
//			break;
//		}
//		
//		return meaning;
	}

	private String encodingPath(String path){
		if(path!=null){
			try {
				path = path.replace(File.separatorChar, '/');
				path = new String(path.getBytes(), "ISO-8859-1");
			} catch (Exception e) {
				//nothing
			}
		}
		return path;
	}

	@Override
	public boolean isClosed() {
		return !ftp.isAvailable();
	}

	@Override
	public void closeFtpInputStream(InputStream input) {
		try {

			IOUtils.closeQuietly(input);
			ftp.completePendingCommand();
			printReplyString();

		} catch (Exception ex) {
			logger.error("close error", ex);
		}
	}

	@Override
	public File downloadToLocal(String remotePath, String localPath) throws StandardException {
		if(StringUtils.isBlank(remotePath) || StringUtils.isBlank(localPath)){
			throw new StandardException("file name can not empty");
		}
		File file = new File(localPath);
		OutputStream out = null;
		try {
			
			if(!file.getParentFile().isDirectory()){
				file.getParentFile().mkdirs();
			}
			
			out = new FileOutputStream(file, false);
			logger.debug("download to local, {}-->{}", remotePath, localPath);
			if(!ftp.retrieveFile(encodingPath(remotePath), out)){
				printReplyString(remotePath);
				String err = "retrieve file error : %s %s";
				err = String.format(err, ftp.getReplyString(), remotePath);
				throw new IOException(err);
			}
			
		} catch (Exception e) {
			throw new StandardException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
		
		return file;
	}

}
