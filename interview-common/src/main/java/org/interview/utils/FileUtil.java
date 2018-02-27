package org.interview.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.interview.common.Const;
import org.interview.exception.StandardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 文件工具类
 * @author shersfy
 * @date 2018-02-27
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class FileUtil {

	private static Logger LOGGER    = LoggerFactory.getLogger(FileUtil.class);
	public static final int BUFSIZE = 1024 * 8;
	
	private FileUtil(){}
	
	/***
	 * 文件名重命名类型
	 *
	 * @copyright Copyright Lenovo Corporation 2017 All Rights Reserved.
	 */
	public static enum RenameType{
		
		None(""),
		Number("_1"),
		Timestamp("_yyyyMMdd_HHmmss");
		
		private String format;
		
		RenameType (){
		}
		RenameType (String format){
			this.format = format;
		}
		
		public static RenameType indexOf(int index){
			switch (index) {
			case 1:
				return None;
			case 2:
				return Number;
			case 3:
				return Timestamp;
			default:
				break;
			}
			return None;
		}
		
		public int index(){
			return this.ordinal()+1;
		}
		
		public String getFormat() {
			return format;
		}
		public void setFormat(String format) {
			this.format = format;
		}
	}

	public static File createDirIfNotExists(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 文件名追加时间戳
	 * 
	 * @param fileName
	 * @return
	 */
	public synchronized static String appendTimestamp(String fileName) {
		int idx = fileName.lastIndexOf(".");
		String filePrefix = "";
		String fileSuffix = "";
		if (idx != -1) {
			filePrefix = fileName.substring(0, idx);
			fileSuffix = fileName.substring(idx);
		} else {
			filePrefix = fileName;
		}
		String format = "_" + Const.FOLDER_TIMESTAMP;
		String timestamp = FastDateFormat.getInstance(format).format(new Date());
		//String timestamp = FastDateFormat.getInstance("_yyyyMMddHHmmssSSS").format(new Date());
		String chars = RandomStringUtils.randomNumeric(3);
		return filePrefix + timestamp + chars + fileSuffix;
	}
	
	/**
	 * 创建带随机数的时间戳字符串
	 * 
	 * @param randomCnt 随机数位数
	 * @return 字符串
	 */
	public synchronized static String createTimestampWithRandom(int randomCnt){
		return DateUtil.format(Const.FOLDER_TIMESTAMP)+RandomStringUtils.randomNumeric(randomCnt);
	}
	
	/**
	 * 创建带时间戳+随机数的文件夹
	 * 
	 * @author PengYang
	 * @date 2017-03-13
	 * 
	 * @param parent 创建文件夹的父目录
	 * @param count 文件存在重试次数
	 * @return String
	 */
	public synchronized static String createFolderWithTimestamp(String parent, int count){
		if(StringUtils.isBlank(parent)){
			return StringUtils.EMPTY;
		}
		String folder = DateUtil.format(Const.FOLDER_TIMESTAMP)+RandomStringUtils.randomNumeric(3);
		File dir = new File(parent, folder);
		int i=1;
		while(i<count){
			if(dir.isDirectory()){
				folder = DateUtil.format(Const.FOLDER_TIMESTAMP)+RandomStringUtils.randomNumeric(3);
				dir = new File(parent, folder);
			}
			else{
				dir.mkdirs();
				break;
			}
			i++;
		}
		return dir.getPath();
	}
	
	/**
	 * 
	 * 
	 * @author PengYang
	 * @date 2017年3月16日
	 * 
	 * @param fileName
	 * @param count
	 * @return
	 */
	public synchronized static File renameFileWithTimestamp(String fileName, int count){
		
		if(StringUtils.isBlank(fileName)){
			return null;
		}
		
		StringBuffer name = new StringBuffer(0);
		name.append(FilenameUtils.getBaseName(fileName));
		name.append(DateUtil.format(Const.FOLDER_TIMESTAMP));
		name.append(RandomStringUtils.randomNumeric(count));
		name.append(".");
		name.append(FilenameUtils.getExtension(fileName));
		
		File newFile = new File(FilenameUtils.getFullPath(fileName), name.toString());
		if(newFile.exists()){
			int i=1;
			while(i<count){
				name.setLength(0);
				name.append(FilenameUtils.getBaseName(fileName));
				name.append(DateUtil.format(Const.FOLDER_TIMESTAMP));
				name.append(RandomStringUtils.randomNumeric(count));
				name.append(".");
				name.append(FilenameUtils.getExtension(fileName));
				newFile = new File(FilenameUtils.getFullPath(fileName), name.toString());
				if(!newFile.exists()){
					break;
				}
			}
		}
		return newFile;
	}
	
	/**
	 * 重命名文件, 没有发生异常操作成功
	 * 
	 * @param srcFile 源文件
	 * @param type 重命名类型
	 * @return 重命名后的文件
	 * @throws StandardException
	 */
	public static File renameFile(File srcFile, RenameType type) throws StandardException{
		
		String newname = renameFilename(srcFile.getName(), type);
		File rename	= new File(srcFile.getParentFile(), newname);
		if(!srcFile.renameTo(rename)){
			String err = String.format("rename error %s-->%s", srcFile.getAbsolutePath(), rename.getAbsolutePath());
			throw new StandardException(err);
		}
		
		return rename;
	}
	
	/**
	 * 重命名文件名, 返回新的文件名
	 * 
	 * @param srcFilename 源文件名
	 * @param type 重命名类型
	 * @return 重命名后新的文件名
	 * @throws StandardException
	 */
	public static String renameFilename(String srcFilename, RenameType type){
		String suffix = "";
		switch (type) {
		case None:
			return srcFilename;
		case Number:
			suffix = type.getFormat();
			break;
		case Timestamp:
			suffix = DateUtil.format(type.getFormat());
			break;
		default:
			return srcFilename;
		}
		
		StringBuffer newname = new StringBuffer(0);
		newname.append(FilenameUtils.getBaseName(srcFilename));
		newname.append(suffix);
		String ext = FilenameUtils.getExtension(srcFilename);
		if(StringUtils.isNotBlank(ext)){
			newname.append(".").append(ext);
		}
		
		return newname.toString();
	}
	
	/**
	 * 删除文件夹及文件夹下的所有文件
	 * 
	 * @author SunQinglinwen
	 * @date 2017年5月31日
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteDirectoryAndFiles(File file){
		boolean flag = false;
		if(!file.exists()){
			return flag;
		}
		if(!file.isDirectory()){
			return flag;
		}
		
		deleteFiles(file);
		flag = file.delete();
		
		return flag;
	}
	
	/**
	 * 删除文件夹下的所有文件
	 * 
	 * @author SunQinglinwen
	 * @date 2017年5月31日
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteFiles(File file){
		boolean flag = false;
		if(!file.exists()){
			return flag;
		}
		if(!file.isDirectory()){
			return flag;
		}
		File[] files = file.listFiles();
		for(File f : files ){
			flag = f.delete();
		}
		return flag;
	}

	public static enum FileSizeUnit{
		Byte, KB, MB, GB, TB, PB, Auto;
		
		/**
		 * 计算单位
		 * @param len 文件大小, 单位byte(B)
		 * @return 返回单位
		 */
		public static FileSizeUnit countUnit(long len){
			
			if(len >= Math.pow(1024, PB.ordinal())){
				return PB;
			}
			
			if(len >= Math.pow(1024, TB.ordinal())){
				return TB;
			}
			
			if(len >= Math.pow(1024, GB.ordinal())){
				return GB;
			}
			
			if(len >= Math.pow(1024, MB.ordinal())){
				return MB;
			}
			
			if(len >= Math.pow(1024, KB.ordinal())){
				return KB;
			}
			
			return Byte;
		}
		
		/**
		 * 计算字节数
		 * 
		 * @param len 文件大小
		 * @param unit 单位
		 * @return 返回字节数(单位byte)
		 */
		public static long countBytes(long len, FileSizeUnit unit){
			if(unit == null || unit == FileSizeUnit.Auto){
				return len;
			}
			
			long blen = (long) Math.pow(1024, unit.ordinal());
			blen = len * blen;
			return blen;
		}
	}
	
	/**
	 * 文件大小单位换算
	 * 
	 * @param file 文件
	 * @param unit 换算单位
	 * @return 返回指定单位换算后大小
	 */
	public static String getLength(File file, FileSizeUnit unit){
		if(file == null){
			return "0";
		}
		
		long len = file.length();
		return getLength(len, unit);
	}
	
	/**
	 * 文件大小单位换算
	 * 
	 * @param len 文件大小
	 * @param unit 单位
	 * @return 返回指定单位换算后大小
	 */
	public static String getLength(long len, FileSizeUnit unit){
		
		if(unit == null){
			return String.valueOf(len);
		}
		
		String lenStr = "0";
		if(FileSizeUnit.Auto == unit){
			unit = FileSizeUnit.countUnit(len);
		}
		lenStr = String.format("%.3f", len/Math.pow(1024, unit.ordinal()));
		
		return lenStr;
	}
	
	/**
	 * 文件大小单位换算
	 * 
	 * @param file 文件
	 * @param unit 换算单位
	 * @return 返回换算后带单位的大小
	 */
	public static String getLengthWithUnit(File file){
		if(file == null){
			return "0";
		}
		
		long len = file.length();
		
		FileSizeUnit unit = FileSizeUnit.countUnit(len);
		String lenStr = getLength(file, unit);
		lenStr = String.format("%s %s", lenStr, unit.name());
		return lenStr;
	}
	/**
	 * 文件大小单位换算
	 * 
	 * @param len 文件大小byte
	 * @return 返回换算后带单位的大小
	 */
	public static String getLengthWithUnit(long len){
		FileSizeUnit unit = FileSizeUnit.countUnit(len);
		String lenStr = getLength(len, unit);
		lenStr = String.format("%s %s", lenStr, unit.name());
		return lenStr;
	}
	/**
	 * Concatenates a filename to a base path using normal command line style rules.
	 * 
	 * @param basePath the base path to attach to, always treated as a path
	 * @param fullFilenameToAdd the filename (or path) to attach to the base
	 * @return the concatenated path, or null if invalid
	 */
	public static String concat(String basePath, String fullFilenameToAdd){
		String path = null;
		if(basePath!=null && fullFilenameToAdd!=null && 
				(fullFilenameToAdd.contains("~")
				|| fullFilenameToAdd.equals(".")
				|| fullFilenameToAdd.equals("..")
				)){
			if(basePath.endsWith("\\") || basePath.endsWith("/")){
				path = basePath + fullFilenameToAdd;
			} else {
				path = basePath + "/"+ fullFilenameToAdd;
			}
			return path;
		}
		if(StringUtils.startsWith(fullFilenameToAdd, "/") 
				|| StringUtils.startsWith(fullFilenameToAdd, "\\")){
			fullFilenameToAdd = fullFilenameToAdd.substring(1);
		}
		path = FilenameUtils.concat(basePath, fullFilenameToAdd);
		return replaceLinuxPath(path);
	}
	/***
	 * 替换为linux路径分隔符
	 * 
	 * @author PengYang
	 * @date 2017-09-28
	 * 
	 * @param path 文件路径
	 * @return 
	 */
	public static String replaceLinuxPath(String path){
		if(path == null){
			return null;
		}
		return path.replace(File.separatorChar, '/');
	}
	
	/**
	 * 按行合并文件, 兼容不同编码的文件输入, 输出utf8编码的文件
	 * 
	 * @author PengYang
	 * @date 2017-12-07
	 * 
	 * @param files 待合并文件列表, 兼容不同编码的文件, 为空返回null
	 * @param mergeFilename 合并的文件名,  为空返回null
	 * @return 返回合并后的文件
	 */
	public static File mergeFile(List<File> files, String mergeFilename) throws StandardException{
		File merge = null;
		if(files==null||files.isEmpty()||StringUtils.isBlank(mergeFilename)){
			return merge;
		}
		
		OutputStream output = null;
		try {
			
			merge  = new File(mergeFilename);
			output = new FileOutputStream(merge, false);
			
			String cs = CharsetUtil.getUTF8().name();
			for(File file :files){
				LOGGER.info("merge file starting:  {}-->{}", file, merge);
				cs = CharsetUtil.detectCode(file.getPath()).name();
				if ("void".equals(cs)) {
					cs = CharsetUtil.getUTF8().name();
				}
				
				InputStreamReader in  = new InputStreamReader(new FileInputStream(file), cs);
				BufferedReader reader = new BufferedReader(in);
				
				while(reader.ready()){
					String line = reader.readLine();
					if(line == null){
						break;
					}
					IOUtils.write(line+"\n", output);
				}
				
				IOUtils.closeQuietly(reader);
				IOUtils.closeQuietly(in);
				LOGGER.info("merge file finished:  {}-->{}", file, merge);
			}
			
		} catch (Exception e) {
			throw new StandardException(e);
		} finally {
			IOUtils.closeQuietly(output);
		}
		
		return merge;
	}
	
	/**
	 * 保存文件
	 * 
	 * @param dir 目录
	 * @param filename 文件名, Bytes<=255, 1个中文字符=3个英文字符=3 Bytes
	 * @param input 输入流
	 * @return File
	 * @throws StandardException 
	 */
	public static File saveFile(String dir, String filename, InputStream input) throws StandardException{
		
		if(StringUtils.isBlank(dir)
				|| StringUtils.isBlank(filename)
				|| input == null){
			return null;
		}
		
		if(filename.getBytes().length > 255){
			throw new StandardException("file name is exceeded: limit=255 length="+filename.getBytes().length);
		}
		
		File file = null;
		try {
			String name = FilenameUtils.getBaseName(filename);
			String ext  = FilenameUtils.getExtension(filename);
			if(StringUtils.isNotBlank(ext)){
				filename = name + "." + ext.toLowerCase();
			}
			file = new File(dir, filename);
			FileUtils.copyInputStreamToFile(input, file);
		} catch (Exception e) {
			LOGGER.error("", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
		return file;
	}
	
}
