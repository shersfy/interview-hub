package org.interview.jgroups.action;

import java.io.File;
import java.io.Serializable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class DeleteFileAction extends Action implements Serializable{

	private static final long serialVersionUID = 1L;
	private String[] pathes;
	
	public DeleteFileAction() {
		super();
	}
	
	public DeleteFileAction(String[] pathes) {
		this();
		this.pathes = pathes==null?new String[0]:pathes;
	}
	
	@Override
	public void doAction() {
		int index = 0;
		for(String path : pathes) {
			if(StringUtils.isBlank(path)) {
				LOGGER.error("element is empty: index={}", index++);
				return ;
			}
			if(FileUtils.deleteQuietly(new File(path))) {
				LOGGER.info("delete: {} by node {}", path);
			}
			index++;
		}
	}

	public String[] getPathes() {
		return pathes;
	}

	public void setPathes(String[] pathes) {
		this.pathes = pathes;
	}

}
