package org.interview.big.data.sqoop;

import org.interview.beans.BaseMeta;
import org.interview.beans.DBMeta;
import org.interview.beans.TableMeta;

public class InputParams extends BaseMeta {

	
	/**sqoop服务访问url**/
	private String sqoopServerUrl;
	private DBMeta srcDBInfo;
	private TableMeta srcTable;
	
	public InputParams() {
		super();
	}
	
	public InputParams(String sqoopServerUrl, DBMeta srcDBInfo) {
		super();
		this.sqoopServerUrl = sqoopServerUrl;
		this.srcDBInfo = srcDBInfo;
	}
	
	public DBMeta getSrcDBInfo() {
		return srcDBInfo;
	}
	
	public void setSrcDBInfo(DBMeta srcDBInfo) {
		this.srcDBInfo = srcDBInfo;
	}
	
	public String getSqoopServerUrl() {
		return sqoopServerUrl;
	}
	
	public void setSqoopServerUrl(String sqoopServerUrl) {
		this.sqoopServerUrl = sqoopServerUrl;
	}

	public TableMeta getSrcTable() {
		return srcTable;
	}

	public void setSrcTable(TableMeta srcTable) {
		this.srcTable = srcTable;
	}

	

}
