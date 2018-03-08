package org.interview.big.data.hadoop;


import java.util.List;

import org.apache.hadoop.fs.FileSystem;
import org.interview.beans.FileMeta;
import org.interview.beans.FileType;
import org.interview.beans.HdfsMeta;
import org.junit.Test;

public class APP 
{
	@Test
    public void test01()
    {
		// URI方式连接
    	try {
			
    		HdfsMeta meta = new HdfsMeta();
    		meta.setUserName("hdfs");
    		meta.setUrl("hdfs://demo8.leap.com:8020");
    		
    		FileSystem fs   	= HdfsUtil.getFileSystem(meta);
			List<FileMeta> list = HdfsUtil.listPath(fs, "/user/hdfs", FileType.All);
			for(FileMeta p :list) {
				System.out.println(String.format("%s\t%s\t%s\t\t%s", 
						p.getPermission(), p.isDirectory()?"d":"f", p.getSize(), p.getPath()));
			}
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	@Test
	public void test02() {
		
		// 配置文件方式连接
		
		HdfsMeta meta = new HdfsMeta();
		meta.setCoreSiteXml("C:/hdfs/208_210/core-site.xml");
		meta.setHdfsSiteXml("C:/hdfs/208_210/hdfs-site.xml");
		meta.setUserName("hdfs");
		
		try {
			FileSystem fs = HdfsUtil.getFileSystem(meta);
			List<FileMeta> list = HdfsUtil.listPath(fs, "/user/hdfs", FileType.All);
			for(FileMeta p: list){
				System.out.println(String.format("%s\t%s\t%s\t\t%s", 
						p.getPermission(), p.isDirectory()?"d":"f", p.getSize(), p.getPath()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
