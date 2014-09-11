package cn.ifanmi.findme.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import cn.ifanmi.findme.bean.Time;

public class TimeUtil {
	
	public static final int LARGE = 1; 
	public static final int EQULE = 0;
	public static final int SMALL = -1;

	public static String getDisplayTime(Time now, String original) {
		return getDisplayTime(now, originalToTime(original));
	}
	
	public static String getDisplayTime(Time now, Time original) {
		String displayTime = null;
		String month, day, hour, minute;
		Time time = original;
		if (time.getMonth() < 10) {
			month = "0" + time.getMonth();
		} else {
			month = "" + time.getMonth();
		}
		if (time.getDay() < 10) {
			day = "0" + time.getDay();
		} else {
			day = "" + time.getDay();
		}
		if (time.getHour() < 10) {
			hour = "0" + time.getHour();
		} else {
			hour = "" + time.getHour();
		}
		if (time.getMinute() < 10) {
			minute = "0" + time.getMinute();
		} else {
			minute = "" + time.getMinute();
		}
		if (time.getYear() != now.getYear()) {
			displayTime = time.getYear() + "年" + 
					month + "月" +
					day + "日  " +
					hour + ":" +
					minute;
		} else {
			if (time.getMonth() == now.getMonth() && time.getDay() == now.getDay()) {
				displayTime = "今天  " + hour + ":" + minute;
			} else {
				displayTime = month + "月" +
						day + "日  " +
						hour + ":" +
						minute;
			}
		}
		return displayTime;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Time getNow() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		/*这里需要格外注意，Calendar获取出来的月份是从0到11*/
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		Time now = new Time(year, month, day, hour, minute);
		return now;
	}
	
	/**
	 * 
	 * @param calendar
	 * @return
	 */
	public static Time getTimeByCalendar(Calendar calendar) {
		int year = calendar.get(Calendar.YEAR);
		/*这里需要格外注意，Calendar获取出来的月份是从0到11*/
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		Time now = new Time(year, month, day, hour, minute);
		return now;
	}
	
	/**
	 * 
	 * @param original
	 * @return
	 */
	public static Time originalToTime(String original) {
		char[] time = original.toCharArray();
		int year = Integer.parseInt(String.copyValueOf(time, 0, 4));
		int month = Integer.parseInt(String.copyValueOf(time, 5, 2));
		int day = Integer.parseInt(String.copyValueOf(time, 8, 2));
		int hour = Integer.parseInt(String.copyValueOf(time, 11, 2));
		int minute = Integer.parseInt(String.copyValueOf(time, 14, 2));
		Time result = new Time(year, month, day, hour, minute);
		return result;
	}
	
	/**
	 * 
	 * @param time
	 * @return
	 */
	public static String timeToOriginal(Time time) {
		String month, day, hour, minute;
		if (time.getMonth() < 10) {
			month = "0" + time.getMonth();
		} else {
			month = "" + time.getMonth();
		}
		if (time.getDay() < 10) {
			day = "0" + time.getDay();
		} else {
			day = "" + time.getDay();
		}
		if (time.getHour() < 10) {
			hour = "0" + time.getHour();
		} else {
			hour = "" + time.getHour();
		}
		if (time.getMinute() < 10) {
			minute = "0" + time.getMinute();
		} else {
			minute = "" + time.getMinute();
		}
		String original = String.valueOf(time.getYear()) + "-" 
				+ month + "-" 
				+ day + " " 
				+ hour + ":"
				+ minute + ":"
				+ "00";
		return original;
	}
	
	/**
	 * 两个时间比较大小，一直判断，还相当麻烦
	 * @param large
	 * @param small
	 * @return
	 */
	public static int compare(Time large, Time small) {
		if (large.getYear() > small.getYear()) {
			return LARGE;
		} else if (large.getYear() == small.getYear()) {
			if (large.getMonth() > small.getMonth()) {
				return LARGE;
			} else if (large.getMonth() == small.getMonth()) {
				if (large.getDay() > small.getDay()) {
					return LARGE;
				} else if (large.getDay() == small.getDay()) {
					if (large.getHour() > small.getHour()) {
						return LARGE;
					} else if (large.getHour() == small.getHour()) {
						if (large.getMinute() > small.getMinute()) {
							return LARGE;
						} else if (large.getMinute() == small.getMinute()) {
							return EQULE;
						}
					}
				}
			}
		}
		return SMALL;
	}
	
	/**
	 * 设置上次刷新时间，用于可下拉的listview下次下拉时显示
	 * @return
	 */
	public static String setLastestUpdata() {
		String hour, minute;
		Time lastestRefreshTime = getNow();
		
		if (lastestRefreshTime.getHour() < 10) {
			hour = "0" + lastestRefreshTime.getHour();
		} else {
			hour = "" + lastestRefreshTime.getHour();
		}
		if (lastestRefreshTime.getMinute() < 10) {
			minute = "0" + lastestRefreshTime.getMinute();
		} else {
			minute = "" + lastestRefreshTime.getMinute();
		}
		
		return hour + ":" + minute;
	}
	
	public static String getMyActTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());     
		String time = sDateFormat.format(new java.util.Date());
		return time;
	}
	
}
