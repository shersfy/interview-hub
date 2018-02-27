package org.interview.meta;

/**
 * 数据库连接类型
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public enum DBAccessType {
	/**空**/
	Dummy,
	/**JDBC连接**/
	JDBC,
	/**ODBC开放数据库连接**/
	ODBC,
	/**JNDI Java命名和目录接口连接**/
	JNDI,
	/**OCI Oracle调用接口连接**/
	OCI;
	
	/**
	 * 获取索引
	 * 
	 * @author PengYang
	 * @date 2016-10-26
	 * 
	 * @return int
	 */
	public int index(){
		return this.ordinal();
	}
	
	public static DBAccessType valueOf(int index){
		switch (index) {
		case 1:
			return JDBC;
		case 2:
			return ODBC;
		case 3:
			return JNDI;
		case 4:
			return OCI;
		default:
			return Dummy;
		}
	}
}
