package org.interview.common;

import com.alibaba.fastjson.JSONObject;

/**
 * 常量
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Const {
	private Const(){
	}
	
	/**系统换行符**/
	public static final String LINE_SEP 	= System.getProperty("line.separator", "\n");
	/**默认列分隔符**/
	public static final String COLUMN_SEP 	= "\u0001";
	
	/**年份类型: yyyy**/
	public static final String yyyy		= "yyyy";
	/**年月类型: yyyyMM**/
	public static final String yyyyMM 	= "yyyy/MM";
	/**日期统一格式:yyyy/MM/dd**/
	public static final String yyyyMMdd 		= "yyyy/MM/dd";
	/**文件夹时间戳格式**/
	public static final String FOLDER_TIMESTAMP = "yyyyMMddHHmmssSSS";
	/**数据转string日期格式**/
	public static final String FORMAT_DATE		= "yyyy-MM-dd";
	/**数据转string日期时间格式**/
	public static final String FORMAT_DATETIME	= "yyyy-MM-dd HH:mm:ss";
	/**数据转string日期时间格式**/
	public static final String FORMAT_TIMESTAMP	= "yyyy-MM-dd HH:mm:ss.SSS";
	/**日期时间统一格式:yyyy/MM/dd HH:mm:ss**/
	public static final String yyyyMMddHHmmss 	= "yyyy/MM/dd HH:mm:ss";
	/**时间统一格式:HH:mm:ss**/
	public static final String HHmmss 			= "HH:mm:ss";
	
	/**Hadoop认证类型**/
	public static enum HadoopAuthTypes{
		SIMPLE("simple"),
		KERBEROS("kerberos"),
		SENTRY("sentry");
		
		private JSONObject json;
		private String alias;
		
		HadoopAuthTypes(String alias){
			this.alias = alias;
			this.json = new JSONObject();
		}
		
		public static HadoopAuthTypes indexOf(int index){
			switch (index) {
			case 1:
				return SIMPLE;
			case 2:
				return KERBEROS;
			case 3:
				return SENTRY;
			default:
				return SIMPLE;
			}
		}
		
		public String alias() {
			return alias;
		}
		
		public int index(){
			return this.ordinal()+1;
		}
		
		@Override
		public String toString() {
			json.put("index", this.index());
			json.put("alias", this.alias);
			json.put("name", this.name());
			return json.toJSONString();
		}
		
	}
	
	/**Hive表存储格式类型**/
	public static enum HiveTableFormat {
		
		TEXT,
		ORC,
		PARQUET,
		RCFILE,
		SEQUENCEFILE;
		
		public static HiveTableFormat indexOf(Integer index) {
			if (index == null) {
				return TEXT;
			}
			switch (index) {
			case 0:
				return TEXT;
			case 1:
				return ORC;
			case 2:
				return PARQUET;
			case 3:
				return RCFILE;
			case 4:
				return SEQUENCEFILE;
			default:
				return TEXT;
			}
		}
		
		public int index() {
			return this.ordinal();
		}
	}
}
