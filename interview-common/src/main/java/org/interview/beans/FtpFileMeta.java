package org.interview.beans;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTPFile;

/**
 * ftp文件或目录对象类
 * @author PengYang
 * @date 2017-05-03
 *
 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
 */
public class FtpFileMeta extends FTPFile{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean directory;
	private Boolean file;

	/**文件或目录路径**/
	private String path;
	
	public FtpFileMeta() {
		super();
	}
	
	public FtpFileMeta(String path) {
		super();
		this.path = path;
		if(path!=null){
			this.setName(FilenameUtils.getName(path));
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public boolean isDirectory() {
		if(this.directory!=null){
			return this.directory;
		}
		return super.isDirectory();
	}

	@Override
	public boolean isFile() {
		if(this.file!=null){
			return this.file;
		}
		return super.isFile();
	}

	public final void setDirectory(Boolean directory) {
		this.directory = directory;
	}

	public final void setFile(Boolean file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return this.path;
	}
}
