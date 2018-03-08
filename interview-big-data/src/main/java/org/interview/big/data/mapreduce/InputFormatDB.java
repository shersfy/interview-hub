package org.interview.big.data.mapreduce;

import org.interview.beans.DBMeta;

public class InputFormatDB implements Cloneable{
	
	private String key;
	private String tableName;
	private String splitSql;
	private DBMeta dbinfo;
	
	public InputFormatDB() {}
	public InputFormatDB(DBMeta dbinfo, String tableName) {
		super();
		this.dbinfo = dbinfo;
		this.tableName = tableName;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getKey() {
		return key;
	}

	public String getTableName() {
		return tableName;
	}

	public String getSplitSql() {
		return splitSql;
	}

	public DBMeta getDbinfo() {
		return dbinfo;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setSplitSql(String splitSql) {
		this.splitSql = splitSql;
	}

	public void setDbinfo(DBMeta dbinfo) {
		this.dbinfo = dbinfo;
	}


}
