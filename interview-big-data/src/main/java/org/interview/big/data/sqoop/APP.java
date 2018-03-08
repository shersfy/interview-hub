package org.interview.big.data.sqoop;

import org.interview.beans.DBMeta;
import org.interview.beans.HdfsMeta;
import org.interview.beans.TableMeta;

public class APP 
{
    public static void main( String[] args )
    {
    	/**
    	 * sqoop导数到hdfs
    	 * 1. 安装sqoop server，得到server url;
    	 * 2. 编写sqoop client代码, 调用sqoop server的API;
    	 * 	  1) 数据源数据库连接信息
    	 * 	  2) 数据源表信息
    	 *    3) 目标hdfs连接线信息
    	 *    4) 目标hdfs路径
    	 * 3. 等待执行结果
    	 */
    	
    	String sqoopServerUrl = "";
    	
		DBMeta srcDBInfo = new DBMeta();
		srcDBInfo.setCode("MySQL");
		srcDBInfo.setHost("demo9.leap.com");
		srcDBInfo.setPort(3306);
		srcDBInfo.setDbName("datahub_test");
		srcDBInfo.setUserName("priestuser");
		srcDBInfo.setPassword("123456");
		
		TableMeta srcTable = new TableMeta("datahub_test", null, "record");
		
		HdfsMeta hdfsInfo = new HdfsMeta();
		hdfsInfo.setUserName("hdfs");
		hdfsInfo.setUrl("hdfs://demo8.leap.com:8020");
		
		String tarHdfsPath = "/tmp/sqoop/test";
		
		InputParams inputParam  = new InputParams(sqoopServerUrl, srcDBInfo);
		OutputParam outputParam = new OutputParam(hdfsInfo, tarHdfsPath);
		inputParam.setSrcTable(srcTable);
    	SqoopClientUtil.submitJobForDb2Hdfs(inputParam, outputParam);
    }
}
