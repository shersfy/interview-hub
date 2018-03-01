package org.interview.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.interview.beans.DataFileMeta;
import org.interview.beans.FieldData;
import org.interview.beans.GridData;
import org.interview.beans.RowData;
import org.interview.exception.StandardException;

/**
 * 结构化数据文件工具类
 * @author shersfy
 * @date 2018-02-27
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class DataFileUtil {

	private DataFileUtil(){}
	
	/**
	 * 写数据到文件
	 * 
	 * @author PengYang
	 * @date 2017-06-22
	 * 
	 * @param dataFile 数据文件
	 * @param dataBlock 数据块
	 * @param outColSep 列分隔符
	 * @param isAppend 是否追加
	 * @throws StandardException 
	 */
	public static DataFileMeta writeToFile(final DataFileMeta dataFile, GridData dataBlock, String outColSep, boolean isAppend) 
			throws StandardException{
		
		return writeToFile(dataFile, dataBlock, outColSep, isAppend, " ");
	}
	/**
	 * 写数据到文件
	 * 
	 * @author PengYang
	 * @date 2017-06-22
	 * 
	 * @param dataFile 数据文件
	 * @param dataBlock 数据块
	 * @param outColSep 列分隔符
	 * @param isAppend 是否追加
	 * @param replaceEnter 替换换行符, 为null不替换
	 * @throws StandardException 
	 */
	public static DataFileMeta writeToFile(final DataFileMeta dataFile, GridData dataBlock, String outColSep, boolean isAppend, String replaceEnter) 
			throws StandardException{
		OutputStream output = null;
		try {
			if(dataFile ==null || dataFile.getFile()==null){
				return null;
			}
			if(dataBlock == null){
				return dataFile;
			}
			if(!dataBlock.getHeaders().isEmpty()){
				dataFile.getHeaders().addAll(dataBlock.getHeaders());
			}
			File file = dataFile.getFile();
			dataFile.setName(file.getName());
			StringBuffer content = new StringBuffer();
			for (RowData row : dataBlock.getRows()) {
				List<FieldData> fds = row.getFields();
				StringBuffer line 	= new StringBuffer();
				if(!fds.isEmpty()){
					dataFile.setCols(fds.size());
				}
				for (FieldData field : row.getFields()) {
					line.append(field.getValue()).append(outColSep);
				}
				if(line.toString().trim().length() == 0){
					content.append("\n");
					continue;
				}
				String data = line.substring(0, line.length()-outColSep.length());
				if(replaceEnter!=null){
					data = data.replaceAll("\r\n|\n", replaceEnter);
				}
				content.append(data).append("\n");
			}
			if(!file.getParentFile().isDirectory()){
				file.getParentFile().mkdirs();
			}
			
			output = new FileOutputStream(file, isAppend);
			IOUtils.write(content.toString(), output);
			
			dataFile.setLines(dataFile.getLines()+dataBlock.getRows().size());
			dataFile.setSize(file.length());
			dataFile.setColumnSep(outColSep);
			
			dataBlock.clearAll();
			
		} catch (Throwable e) {
			throw new StandardException(e, "write data to file error %s", dataFile);
		} finally {
			IOUtils.closeQuietly(output);
		}
		return dataFile;
	}
	
}