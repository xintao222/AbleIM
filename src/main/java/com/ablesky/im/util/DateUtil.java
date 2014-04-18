package com.ablesky.im.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @author yeyin
 */
public class DateUtil {

	public static String YEAR = "year";
	public static String MONTH = "month";
	public static String DAY = "day";
	public static String HOUR = "hour";
	public static String MINUTE = "minute";
	public static String SECOND = "second";

	public static int INTERVAL_JUST = 0;
	public static int INTERVAL_10M = 1;
	public static int INTERVAL_30M = 2;
	public static int INTERVAL_1H = 3;
	public static int INTERVAL_2H = 4;
	public static int INTERVAL_3H = 5;
	public static int INTERVAL_5H = 6;
	public static int INTERVAL_1D = 7;

	public static Map<String, Object> getIntervalOfDates(long second) {
		Map<String, Object> map = new HashMap<String, Object>();
		long minute = second / 60;
		long hour = minute / 60;
		long day = hour / 24;
		if (day > 0) {
			map.put("type", "day");
			map.put("interval", day);
		} else if (hour > 0) {
			map.put("type", "hour");
			map.put("interval", hour);
		} else {
			map.put("type", "minute");
			map.put("interval", minute);
		}
		return map;
	}

	public static boolean compareDateIsLarger(Date beginDate, Date endDate) {
		if (beginDate == null) {
			return true;
		}
		if (endDate == null) {
			return false;
		}
		long endTime = endDate.getTime();
		long beginTime = beginDate.getTime();
		return endTime > beginTime;
	}

	public static long getIntervalOfTwoDates(Date beginDate, Date endDate) {
		if (beginDate == null || endDate == null)
			return 0;
		long endTime = endDate.getTime();
		long beginTime = beginDate.getTime();
		long intervalMilliSecond = endTime > beginTime ? endTime - beginTime : 0;
		long intervalSecond = intervalMilliSecond / 1000;
		return intervalSecond;
	}
	
	public static String convert(Date date) {
		String ret = "";
		long intervalMilliSecond = new Date().getTime() - date.getTime();
		if (intervalMilliSecond  >= 86400000) {
			ret = deSerialize(date, "MM月dd日");
		}else if (intervalMilliSecond  >= 3600000) {
			ret = intervalMilliSecond / 3600000 + "小时前";
		}else if (intervalMilliSecond  >= 60000) {
			ret = intervalMilliSecond / 60000 + "分钟前";
		}else {
			ret = intervalMilliSecond / 1000 + "秒前";
		} 
		return ret;
	}
	
	/**
	 * 将秒计数方式转化为HH:mm:ss格式
	 * @param seconds 总秒数
	 * @return
	 */
	public static String convert(Long seconds) {
		if(seconds==null){
			seconds = 0L;
		}
		int hour = (int)(seconds/3600);
		int minute = (int)((seconds-hour*3600)/60);
		int second = (int)((seconds-hour*3600-minute*60));
		
		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		
		return strHour + ":" + strMinute + ":" + strSecond;
	}

	public static Date getDateBeforeHours(Date comparedDate, int cursor, String unit) {
		if (unit.equalsIgnoreCase("hour")) {
			long interval = new Long(cursor);
			long millisecond = comparedDate.getTime() - interval * 3600 * 1000;
			return new Date(millisecond);
		} else if (unit.equalsIgnoreCase("day")) {
			long interval = new Long(cursor);
			long millisecond = comparedDate.getTime() - interval * 3600 * 1000 * 24;
			return new Date(millisecond);
		} else {
			long millisecond = comparedDate.getTime() - cursor * 1000;
			return new Date(millisecond);
		}
	}

	public static Date getBefore(Date comparedDate, int cursor, String unit) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(comparedDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		if (unit.equalsIgnoreCase(SECOND)) {
			second = second - cursor;
		} else if (unit.equalsIgnoreCase(MINUTE)) {
			minute = minute - cursor;
		} else if (unit.equalsIgnoreCase(HOUR)) {
			hour = hour - cursor;
		} else if (unit.equalsIgnoreCase(DAY)) {
			date = date - cursor;
		} else if (unit.equalsIgnoreCase(MONTH)) {
			month = month - cursor;
		} else if (unit.equalsIgnoreCase(YEAR)) {
			year = year - cursor;
		}
		calendar.set(year, month, date, hour, minute, second);
		return calendar.getTime();
	}

	public static Date getAfter(Date comparedDate, int cursor, String unit) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(comparedDate);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		if (unit.equalsIgnoreCase(SECOND)) {
			second += cursor;
		} else if (unit.equalsIgnoreCase(MINUTE)) {
			minute += cursor;
		} else if (unit.equalsIgnoreCase(HOUR)) {
			hour += cursor;
		} else if (unit.equalsIgnoreCase(DAY)) {
			date += cursor;
		} else if (unit.equalsIgnoreCase(MONTH)) {
			month += cursor;
		} else if (unit.equalsIgnoreCase(YEAR)) {
			year += cursor;
		}
		calendar.set(year, month, date, hour, minute, second);
		return calendar.getTime();
	}

	public static String deSerialize(Date date, String pattern) {
		if (date == null)
			return "";
		String defaultPattern = "yyyy-MM-dd HH:mm";
		if (pattern == null)
			pattern = defaultPattern;
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.format(date);
	}

	public static Date serialize(String dateStr, String pattern) throws ParseException {
		String defaultPattern = "yyyy-MM-dd HH:mm";
		if (pattern == null)
			pattern = defaultPattern;
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(dateStr);
	}

	public static int getIntervalTypeOfTwoDates(Date beginDate, Date endDate) {
		if (beginDate == null || endDate == null)
			return INTERVAL_JUST;
		long endTime = endDate.getTime();
		long beginTime = beginDate.getTime();
		long intervalMilliSecond = endTime > beginTime ? endTime - beginTime : 0;
		long intervalSecond = intervalMilliSecond / 1000;
		if (intervalSecond < 600) {
			return INTERVAL_JUST;
		} else if (intervalSecond > 600 && intervalSecond < 1800) {
			return INTERVAL_10M;
		} else if (intervalSecond > 1800 && intervalSecond < 3600) {
			return INTERVAL_30M;
		} else if (intervalSecond > 3600 && intervalSecond < 7200) {
			return INTERVAL_1H;
		} else if (intervalSecond > 7200 && intervalSecond < 10800) {
			return INTERVAL_2H;
		} else if (intervalSecond > 10800 && intervalSecond < 18000) {
			return INTERVAL_3H;
		} else if (intervalSecond > 18000 && intervalSecond < 3600 * 24) {
			return INTERVAL_5H;
		} else if (intervalSecond > 3600 * 24) {
			return INTERVAL_1D;
		}
		return INTERVAL_JUST;
	}
	
	/**
	 * 获得date日期该天的开始时间
	 * 例如：2011-04-27 00:00:00是2011-04-27的开始时间
	 * @param date
	 * @return
	 */
	public static Date getBeginTimeForDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}
	
	/**
	 * 获得date日期该天的结束时间
	 * 例如：2011-04-28 00:00:00是2011-04-27的结束时间
	 * @param date
	 * @return
	 */
	public static Date getEndTimeForDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	public static void main(String[] args) {
		try {
//			Date date = new Date();
//			System.out.println(date);
//			System.out.println(getBefore(date, 1, DAY));
//			// DateUtil.getDateBeforeHours(DateUtil.serialize("2009-09-03
//			// 09:49", null), 30, "day");
//			// Date date = new Date();
//			// Date after = DateUtil.getAfter(date, 30, DateUtil.DAY);
//			// System.out.println(DateUtil.deSerialize(date, "yyyy-MM-dd
//			// HH:mm:ss"));
//			// System.out.println(DateUtil.deSerialize(after, "yyyy-MM-dd
//			// HH:mm:ss"));
//			// System.out.println(date.getTime()+ "-"+after.getTime()+" =
//			// "+DateUtil.getIntervalOfTwoDates(after, date));
			System.out.println((int)9.9);
			System.out.println(Math.random());
			System.out.println(UUID.randomUUID());
			System.out.println(318 / 4);
			System.out.println(DateUtil.deSerialize(DateUtil.getDateBeforeHours(DateUtil.serialize("2010-05-31 19:49", null), 31, "day"), "yyyy-MM-dd HH:mm:ss"));
			
			System.out.println(DateUtil.getBefore(new Date(), 30, DateUtil.SECOND));
			
			System.out.println(deSerialize(new Date(), "yyyy-MM-dd HH:mm:ss"));
			System.out.println(convert((long)1));
			
			System.out.println(DateUtil.serialize("08:00:00", "HH:mm:ss"));
			System.out.println(DateUtil.serialize("54:01:37.19", "HH:mm:ss.SS").getTime() +  8 * 60 * 60 * 1000 );
			System.out.println(deSerialize(new Date(DateUtil.serialize("54:01:37.19", "HH:mm:ss.SS").getTime()), "HH:mm:ss"));
			System.out.println(1 * 60 + 37 + 54 * 60 * 60 );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
