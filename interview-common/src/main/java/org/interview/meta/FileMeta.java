package org.interview.meta;

import org.apache.commons.io.FilenameUtils;

/**
 * 文件元信息
 * @author PengYang
 * @date 2017-12-27
 *
 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
 */
public class FileMeta extends BaseMeta{
	
	/**文件大小**/
	private long size;
	/**是否是目录**/
	private boolean directory;
	/**是否是文件**/
	private boolean file;
	/**文件或目录路径**/
	private String path;
	/**所属用户**/
	private String owner;
	/**所属用户组**/
	private String group;
	/**权限**/
	private String permission;
	
	public FileMeta(){}
	public FileMeta(String path) {
		super();
		this.path = path;
		if(path!=null){
			setName(FilenameUtils.getName(path));
		}
	}
	public FileMeta(String path, long size) {
		this(path);
		this.size = size;
	}
	
	public long getSize() {
		return size;
	}
	public boolean isDirectory() {
		return directory;
	}
	public boolean isFile() {
		return file;
	}
	public String getPath() {
		return path;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public void setDirectory(boolean directory) {
		this.directory = directory;
	}
	public void setFile(boolean file) {
		this.file = file;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getOwner() {
		return owner;
	}
	public String getGroup() {
		return group;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
}
