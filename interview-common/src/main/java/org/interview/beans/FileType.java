package org.interview.beans;

public enum FileType {
	/**只获取文件**/
	File,
	/**只获取目录**/
	Directory,
	/**全都获取**/
	All;
	public static FileType indexOf(Integer index){
		if(index == null){
			return All;
		}
		switch (index) {
		case 1:
			return File;
		case 2:
			return Directory;
		case 3:
			return All;
		default:
			return All;
		}
	}

	public int index(){
		return this.ordinal()+1;
	}
}
