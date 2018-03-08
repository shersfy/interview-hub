package org.interview.utils;

/**系统内置功能函数**/
public enum SystemFunctions {
	/**系统当前年份(自定义格式)**/
	CURRENT_YEAR("current_year"),
	/**系统当前月份(自定义格式)**/
	CURRENT_MONTH("current_month"),
	/**系统当天日期(自定义格式)**/
	CURRENT_DATE("current_date"),
	/**系统当天时间0点0分0秒(自定义格式)**/
	CURRENT_TIME0("current_time0"),
	/**系统当天时间23点59分59秒(自定义格式)**/
	CURRENT_TIME23("current_time23"),
	/**DB系统当前小时0分0秒0毫秒(自定义格式)**/
	DB_SYSTEM_HH_0("db_system_hh_0"),
	/**DB系统当前小时59分59秒999毫秒(自定义格式)**/
	DB_SYSTEM_HH_59("db_system_hh_59"),
	/**DB系统当前时间(自定义格式)**/
	DB_SYSTEM_TIME("db_system_time");
	
	private String name;
	private String format;
	
	SystemFunctions(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}
