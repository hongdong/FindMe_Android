package cn.ifanmi.findme.bean;

public class Time {

	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	
	public static final String YEAR = "year";
	public static final String MONTH = "month";
	public static final String DAY = "day";
	public static final String HOUR = "hour";
	public static final String MINUTE = "minute";
	
	public Time() {
		
	}
	
	public Time(Time time) {
		this.year = time.getYear();
		this.month = time.getMonth();
		this.day = time.getDay();
		this.hour = time.getHour();
		this.minute = time.getMinute();
	}
	
	public Time(int day, int hour, int minute) {
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}
	
	public Time(int year, int month, int day, int hour, int minute) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}

	@Override
	public String toString() {
		return "Time [year=" + year + ", month=" + month + ", day=" + day
				+ ", hour=" + hour + ", minute=" + minute + "]";
	}
	
	
}
