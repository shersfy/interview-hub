package org.interview.common;

import org.apache.commons.lang.StringUtils;


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
	
	/**
	 * 从excel获取单元格日期时间格式解析, 统一日期时间格式<br/>
	 * 返回统一的日期时间格式<br/>
	 * 
	 * @author PengYang
	 * @date 2017-03-30
	 * 
	 * @param xlsFormat 来自excel的单元日期格式
	 * @return String
	 */
	public static String toDateFormatFromXls(String xlsFormat){

		if(StringUtils.isBlank(xlsFormat)){
			return yyyyMMddHHmmss;
		}
		
		if(StringUtils.containsIgnoreCase(xlsFormat, "[DBNum")){
			xlsFormat = xlsFormat.substring(xlsFormat.lastIndexOf("]")+"]".length());
		}
		if(StringUtils.containsIgnoreCase(xlsFormat, "[$-")){
			xlsFormat = xlsFormat.substring(xlsFormat.lastIndexOf("]")+"]".length());
		}
		// 年份yy
		boolean yy = StringUtils.containsIgnoreCase(xlsFormat, "yy");
		yy = yy && !StringUtils.containsIgnoreCase(xlsFormat, "m");
		yy = yy && !StringUtils.containsIgnoreCase(xlsFormat, "d");
		yy = yy || xlsFormat.matches("[y]{1,}");
		// 年月yy&&m
		boolean ym = StringUtils.containsIgnoreCase(xlsFormat, "yy");
		ym = ym && StringUtils.containsIgnoreCase(xlsFormat, "m");
		ym = ym && !StringUtils.containsIgnoreCase(xlsFormat, "d");
		ym = ym || xlsFormat.matches("[m]{1,}");
		// 日期yy&&m&d
		boolean ymd = StringUtils.containsIgnoreCase(xlsFormat, "yy");
		ymd = ymd && StringUtils.containsIgnoreCase(xlsFormat, "m");
		ymd = ymd && StringUtils.containsIgnoreCase(xlsFormat, "d");
		// d...型
		ymd = ymd || xlsFormat.matches("[d]{1,}");
		// aaa...型
		ymd = ymd || xlsFormat.matches("[a]{3,}");
		// [h]型, 取消24小时限制
		ymd = ymd || StringUtils.containsIgnoreCase(xlsFormat, "[h]");
		
		// 月日m&d
		boolean md = StringUtils.containsIgnoreCase(xlsFormat, "m");
		md = md && StringUtils.containsIgnoreCase(xlsFormat, "d");
		md = md && !StringUtils.containsIgnoreCase(xlsFormat, "yy");
		// 时分秒
		boolean hms = StringUtils.containsIgnoreCase(xlsFormat, "h");
		hms = hms && StringUtils.containsIgnoreCase(xlsFormat, "m");
		hms = hms && StringUtils.containsIgnoreCase(xlsFormat, "s");
		// 时分
		boolean hm = StringUtils.containsIgnoreCase(xlsFormat, "h");
		hm = hm && StringUtils.containsIgnoreCase(xlsFormat, "m");
		hm = hm && !StringUtils.containsIgnoreCase(xlsFormat, "s");
		// 分秒
		boolean ms = StringUtils.containsIgnoreCase(xlsFormat, "m");
		ms = ms && StringUtils.containsIgnoreCase(xlsFormat, "s");
		ms = ms && !StringUtils.containsIgnoreCase(xlsFormat, "h");

		// 年月日时分秒
		boolean yMdHms1 = (ymd&&hms);
		yMdHms1 = yMdHms1 || (ymd&&hm);
		yMdHms1 = yMdHms1 || (ymd&&ms);

		boolean yMdHms2 = (ym&&hms);
		yMdHms2 = yMdHms2 || (ym&&hm);
		yMdHms2 = yMdHms2 || (ym&&ms);

		boolean yMdHms3 = (md&&hms);
		yMdHms3 = yMdHms3 || (md&&hm);
		yMdHms3 = yMdHms3 || (md&&ms);

		// y年...型
		if(yy){
			return yyyy;
		}
		// M月...型
		else if(ym){
			return yyyyMM;
		}
		// 年月日时分秒
		else if(yMdHms1 || yMdHms2 || yMdHms3){
			xlsFormat = yyyyMMddHHmmss;
		}
		// 年月日
		else if(ymd || md){
			xlsFormat = yyyyMMdd;
		}
		// 时分秒
		else if(hms || hm || ms){
			xlsFormat = HHmmss;
		}


		return xlsFormat;
	}
}
