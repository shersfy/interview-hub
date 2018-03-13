package org.interview.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 数据库元信息
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class DBMeta extends BaseMeta{
    
	/** 数据库类型 **/
	private String code;
	
	/** 主机名称 **/
	private String host;
	
	/** 端口号 **/
	private Integer port;
	
	/** 数据库名称 **/
	private String dbName;
	
    /** catalog名称 **/
    private String catalog;
    
    /** schem名称 **/
    private String schema;

    /** 用户名 **/
    private String userName;

    /** 密码 **/
    private String password;

    /** 连接url **/
    private String url;
    
    /** 编码 **/
    private String encoding;
    
    /** 访问类型 **/
    private DBAccessType accessType;

    /** 连接参数 **/
    private Properties params = null;
	/**所有的表**/
	private List<TableMeta> tables;
	/**所有视图**/
	private List<TableMeta> views;
	/**所有存储过程**/
	private List<StoredProc> storedProces;
	/**所有功能函数**/
	private List<FunctionDef> functions;
    /** 绑定的hdfs连接信息**/
    private HdfsMeta bundledHdfs;
    /**是否等待重连(默认true)**/
    private boolean await;
    /**是否检查有效性(默认true)**/
    private boolean checkValid;
	/**重连超时时间(秒)**/
    private long retryTimes;
	/**重连次数**/
    private long retryTimeout;
    
    public DBMeta(){
    	userName    = "";
    	password    = "";
    	params 		= new Properties();
    	accessType 	= DBAccessType.JDBC;
    	tables 		= new ArrayList<TableMeta>();
    	views 		= new ArrayList<TableMeta>();
    	storedProces= new ArrayList<StoredProc>();
    	functions 	= new ArrayList<FunctionDef>();
    	await		= true;
    	checkValid	= true;
    	retryTimes  = 5;
    	retryTimeout = 1*3600*1000;
    }

	public String getCode() {
		return code;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getDbName() {
		return dbName;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getUrl() {
		return url;
	}

	public DBAccessType getAccessType() {
		return accessType;
	}

	public Properties getParams() {
		return params;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setAccessType(DBAccessType accessType) {
		this.accessType = accessType;
	}

	public final boolean isAwait() {
		return await;
	}

	public boolean isCheckValid() {
		return checkValid;
	}

	public void setCheckValid(boolean checkValid) {
		this.checkValid = checkValid;
	}

	public long getRetryTimes() {
		return retryTimes;
	}

	public long getRetryTimeout() {
		return retryTimeout;
	}

	public void setAwait(boolean await) {
		this.await = await;
	}

	public void setRetryTimes(long retryTimes) {
		this.retryTimes = retryTimes;
	}

	/**
	 * 设置超时时间(毫秒)
	 * 
	 * @param retryTimeout
	 */
	public void setRetryTimeout(long retryTimeout) {
		this.retryTimeout = retryTimeout;
	}

	public void setParams(Properties params) {
		this.params = params;
	}

	public List<TableMeta> getTables() {
		return tables;
	}

	public List<TableMeta> getViews() {
		return views;
	}

	public List<StoredProc> getStoredProces() {
		return storedProces;
	}

	public List<FunctionDef> getFunctions() {
		return functions;
	}

	public HdfsMeta getBundledHdfs() {
		return bundledHdfs;
	}

	public void setBundledHdfs(HdfsMeta bundledHdfs) {
		this.bundledHdfs = bundledHdfs;
	}

	public void setTables(List<TableMeta> tables) {
		this.tables = tables;
	}

	public void setViews(List<TableMeta> views) {
		this.views = views;
	}

	public void setStoredProces(List<StoredProc> storedProces) {
		this.storedProces = storedProces;
	}

	public void setFunctions(List<FunctionDef> functions) {
		this.functions = functions;
	}

}