
package org.interview.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * 日期工具类
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class DateUtil {
	
	private DateUtil(){
		
	}
	
	/**
	 * 格式化日期,默认返回yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 * @author shersfy
	 */
	public static String format(Date date) {
		return format(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 指定格式, 格式化时间
	 * 
	 * @author PengYang
	 * @date 2017-05-09
	 * 
	 * @param milliseconds 毫秒数
	 * @param format  指定格式, d天(可选) h小时 m分 s秒 S毫秒(可选)
	 * @return String
	 */
	public static String format(long milliseconds, String format) {
		return new TimeObject(milliseconds).toString(format);
	}

	/**
	 * 格式化显示当前日期
	 * 
	 * @param format
	 * @return
	 * @author shersfy
	 */
	public static String format(String format) {
		return format(new Date(), format);
	}

	/**
	 * 日期格式化
	 * 
	 * @param date
	 * @param format
	 * @return
	 * @author shersfy
	 */
	public static String format(Date date, String format) {
		if(date==null){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}

	/**
	 * 时间格式化， 传入毫秒
	 * 
	 * @param time
	 * @return
	 * @author shersfy
	 */
	public static String dateFormat(long time) {
		return format(new Date(time), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 得到某一天以前day天的前面多少分钟
	 * @param d
	 * @param day
	 * @return
	 * @author shersfy
	 */
	public static Date getMinuteBefore(Date d, int minute) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) - minute);
		return now.getTime();
	}
	
	/**
	 * 得到某一天以前day天的后多少分钟
	 * @param d
	 * @param day
	 * @return
	 * @author shersfy
	 */
	public static Date getMinuteAfter(Date d, int minute) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + minute);
		return now.getTime();
	}
	
	/**
	 * 得到某一天以前day天的前面多少小时
	 * @param d
	 * @param day
	 * @return
	 * @author shersfy
	 */
	public static Date getHourBefore(Date d, int hour) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.HOUR, now.get(Calendar.HOUR) - hour);
		return now.getTime();
	}
	
	/**
	 * 得到某一天以前day天的后面多少小时
	 * @param d
	 * @param day
	 * @return
	 * @author shersfy
	 */
	public static Date getHourAfter(Date d, int hour) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.HOUR, now.get(Calendar.HOUR) + hour);
		return now.getTime();
	}

	/**
	 * 得到某一天以前day天的date
	 * @param d
	 * @param day
	 * @return
	 * @author shersfy
	 */
	public static Date getDateBefore(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}

	/**
	 * 得到某一天以后day天的date
	 * @param d
	 * @param day
	 * @return
	 * @author shersfy
	 */
	public static Date getDateAfter(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}
	
	/**
	 * 两个时间对象
	 * @param dateOne
	 * @param dateTwo
	 * @return 相等返回0,小于返回负数，大于返回正数
	 * @author shersfy
	 * @throws ParseException 
	 */
	public static long compareDate(String dateOneStr, String dateTwoStr,String format) throws ParseException{
		Date dateOne = getDateByStr(dateOneStr,format);
		Date dateTwo = getDateByStr(dateTwoStr,format);
		return dateOne.getTime() - dateTwo.getTime();
	}
	
	/**
	 * 两个时间对象
	 * @param dateOne
	 * @param dateTwo
	 * @return 相等返回0,小于返回负数，大于返回正数
	 * @author shersfy
	 */
	public static long compareDate(Date dateOne, Date dateTwo){
		return dateOne.getTime() - dateTwo.getTime();
	}
	
	/**
	 * 日期字符串解析
	 * @param dateStr 日期字符串
	 * @param format 日期格式
	 * @return Date对象
	 * @throws ParseException
	 * @author shersfy
	 */
	public static Date getDateByStr(String dateStr,String format) throws ParseException{
    	SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    	Date date = dateFormat.parse(dateStr);
    	return date; 
    }
	
	/**
	 * 按照yyyy-MM-dd HH:mm:ss格式转换字符串成日期对象
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 * @author shersfy
	 */
	public static Date getDateByStr(String dateStr) throws ParseException{
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date date = dateFormat.parse(dateStr);
    	return date; 
    }

	/**
	 * 检查时间合法性
	 * @param date
	 * @return 合法返回ture,反之false
	 * @author shersfy
	 */
	public static boolean checkDate(Date date) {
		boolean result = true;
		if (date != null) {
			String dateStr = DateUtil.format(date);
			if ("0000-00-00 00:00:00".equals(dateStr)) {
				result = false;
			}
		} else {
			result = false;
		}
		return result;
	}
	
	/**
	 * 获取当天指定小时分钟秒的时间
	 * @return
	 * @author shersfy
	 */
	public static Date getSyncDate(int hourOfDay,int minute,int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}
	
	/**
	 * 获取UTC from epoch 日期对象
	 * @return
	 * @author shersfy
	 */
	public static Date getDateByUtcEpochTime(long utcEpochTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(utcEpochTime);
		return calendar.getTime();
	}
	
	/**
	 * 获取指定timeZone的Calender
	 * @param timeInMillis
	 * @param timeZone
	 * @return
	 */
	public static Calendar getCalendar(long timeInMillis,TimeZone timeZone) {
		Calendar calendar = Calendar.getInstance(timeZone);
		calendar.setTimeInMillis(timeInMillis);
		return calendar;
	}
	
	
	public static int getTaskStatus(Date startDate, Date endDate) {
		Date now =new Date();
		if (startDate == null && endDate == null) {
			return 1;
		} else if (startDate != null && endDate == null) {
			if (now.before(startDate)) {
				return 0;
			} else {
				return 1;
			}
		} else if (startDate == null && endDate != null) {
			if (now.before(endDate)) {
				return 1;
			} else {
				return 2;
			}
		} else {
			if (now.before(startDate)) {
				return 0;
			} else if (now.after(endDate)) {
				return 2;
			} else {
				return 1;
			}
		}
	}
}


class TimeObject{
	public final long second = 1000;
	public final long minute = second*60;
	public final long hour 	= minute*60;
	public final long day 	= hour*24;
	
	private long days;
	private long hours;
	private long minutes;
	private long seconds;
	private long milliseconds;
	
	public TimeObject(){}
	
	public TimeObject(long milliseconds){
		
		this.days 			= milliseconds/day;
		this.hours 			= milliseconds/hour;
		this.minutes 		= milliseconds/minute;
		this.seconds 		= milliseconds/second;
		this.milliseconds 	= milliseconds;
		
	}
	
	public long getDays() {
		return days;
	}
	public long getHours() {
		return hours;
	}
	public long getMinutes() {
		return minutes;
	}
	public long getSeconds() {
		return seconds;
	}
	public long getMilliseconds() {
		return milliseconds;
	}
	public void setDays(long days) {
		this.days = days;
	}
	public void setHours(long hours) {
		this.hours = hours;
	}
	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}
	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}
	public void setMilliseconds(long milliseconds) {
		this.milliseconds = milliseconds;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
	
	/**
	 * 指定格式, 格式化时间
	 * 
	 * @author PengYang
	 * @date 2017-05-09
	 * 
	 * @param format 指定格式，d 天(可选) hh小时 mm分 ss秒 SSS毫秒(可选)
	 * @return String
	 */
	public String toString(String format) {
		if(StringUtils.isBlank(format)){
			return String.valueOf(milliseconds);
		}
		// 26天 2小时 45 分 16秒 089毫秒
		long sumDays 	= days;
		long sumHours 	= milliseconds%day/hour;
		long sumMinutes	= milliseconds%day%hour/minute;
		long sumSecond	= milliseconds%day%hour%minute/second;
		long sumMSecond	= milliseconds%day%hour%minute%second;
		// 天数为0, 丢弃天
		if(sumDays == 0 && format.indexOf("hh")!=-1){
			format = format.substring(format.indexOf("hh"));
			// 小时数为0，丢弃小时
			if(sumHours == 0 && format.indexOf("mm")!=-1){
				format = format.substring(format.indexOf("mm"));
				// 分钟数为0，丢弃分钟
				if(sumMinutes == 0 && format.indexOf("ss")!=-1){
					format = format.substring(format.indexOf("ss"));
					// 秒数为0，秒
					if(sumSecond == 0 && format.indexOf("SSS")!=-1){
						format = format.substring(format.indexOf("SSS"));
					}
				}
			}
		}
		if(StringUtils.contains(format, "d ")){
			
		}
		else if(StringUtils.contains(format, "hh")){
			sumHours 	= milliseconds/hour;
			sumMinutes	= milliseconds%hour/minute;
			sumSecond	= milliseconds%hour%minute/second;
			sumMSecond	= milliseconds%hour%minute%second;
		}
		else if(StringUtils.contains(format, "mm")){
			sumMinutes	= milliseconds/minute;
			sumSecond	= milliseconds%minute/second;
			sumMSecond	= milliseconds%minute%second;
		}
		else if(StringUtils.contains(format, "ss")){
			sumSecond	= milliseconds/second;
			sumMSecond	= milliseconds%second;
		}
		format = replace(format, "d ", String.valueOf(sumDays)+" ");
		format = replace(format, "hh", String.valueOf(sumHours));
		format = replace(format, "mm", String.valueOf(sumMinutes));
		format = replace(format, "ss", String.valueOf(sumSecond));
		if(StringUtils.contains(format, "SSS")){
			format = replace(format, "SSS", String.valueOf(sumMSecond));
		}
		
		return format;
		
	}
	
	private String replace(String str, String oldChar, String newChar){
		if(StringUtils.isBlank(str)){
			return str;
		}
		
		int cnt = 0;
		int index = 0;
		// 统计oldChar连续出现次数
		while(index<str.length()){
			index = str.indexOf(oldChar, index);
			if(index==-1){
				break;
			}
			index++;
			cnt++;
		}
		
		if(cnt == 0){
			return str;
		}
		
		StringBuffer buffer = new StringBuffer(0);
		for(int i=0;i<cnt;i++){
			buffer.append(oldChar);
		}
		oldChar = buffer.toString();
		
		// 如果旧字符串连续次数>新字符串长度，新字符串前导0
		if(oldChar.length()>newChar.length()){
			String fm = "%0" + cnt +"d";
			newChar = String.format(fm, Long.valueOf(newChar));
		}
		
		str = str.replace(oldChar, newChar);
		
		return str;
	}
	
}