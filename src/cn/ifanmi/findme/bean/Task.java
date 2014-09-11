package cn.ifanmi.findme.bean;

import java.util.Map;

public class Task {

	//任务ID
	private int taskId;
	
	//任务参数
	private Map<String, Object> taskParams;
	
	//常用的常量值
	public static final String FAN_ACTIVITY = "activity";
	public static final String FAN_FRAGMENT = "fragment";
	public static final String FAN_MODE = "mode";
	public static final String FAN_PTOR = "nl";
	public static final String FAN_LM = "ol";
	
	public static final int FAN_AUTHO = 1;
	public static final String FAN_AUTHO_DEVICE_CODE = "device_code";
	public static final String FAN_AUTHO_SYSTEM_TYPE = "system_type";
	public static final String FAN_AUTHO_SYSTEM_TYPE_ANDROID = "2";
	public static final String FAN_AUTHO_BACK_LOGIN = "back_login";
	public static final String FAN_AUTHO_BACK_LOGIN_YES = "1";
	public static final String FAN_AUTHO_BACK_LOGIN_NO = "0";
	
	public static final int FAN_GETDEPT = 2;
	public static final int FAN_LOGIN = 3;
	public static final int FAN_CHECKUPDATE = 4;
	public static final int FAN_EXIT = 5;
	public static final int FAN_GETSTATUS = 6; 
	public static final int FAN_WRITESTATUS = 7;
	
	public static final int FAN_GETSDM = 8;
	public static final String FAN_GETSDM_ISINFO = "isInfo";
	public static final String FAN_GETSDM_ISINFO_YES = "1";
	public static final String FAN_GETSDM_ISINFO_NO = "0";
	
	public static final int FAN_PRAISESTATUS = 9;
	public static final String FAN_PS_PRAISE = "p";
	public static final String FAN_PS_CANCEL = "c";
	
	public static final int FAN_SDMESSAGE = 10;
	public static final int FAN_GETPD = 11;
	public static final int FAN_UPDATEUP = 12;
	public static final int FAN_UPDATEUD = 13;
	public static final int FAN_ADDALBUM = 14;
	public static final int FAN_DELETEAP = 15;
	public static final int FAN_GETFRIEND = 16; 
	public static final int FAN_GETSTATUSINFO = 17; 
	
	public static final int FAN_GETMATCH = 18;
	public static final int FAN_PASS = 19;
	public static final int FAN_LIKE = 20;
	public static final String FAN_GETMATCH_TYPE = "type";
	public static final String FAN_GETMATCH_TYPE_MATCH = "1";
	public static final String FAN_GETMATCH_TYPE_FANS = "2";
	
	public static final int FAN_GETFANS = 21; 
	public static final int FAN_FEEDBACK = 22;
	public static final String FAN_FEEDBACK_STRING = "feedback";
	
	public static final int FAN_CHECKCAPTCHA = 23;
	public static final int FAN_GETPHOTO = 24;
	public static final String FAN_GETPHOTO_URL = "photourl";
	public static final String FAN_CAPTCHA_PREFIXURL = "http://114.215.115.33/upload/code/";
	public static final int FAN_AUTH = 25;
	public static final String FAN_AUTH_STUDENTID = "studentid";
	public static final String FAN_AUTH_PASSWORD = "password";
	public static final String FAN_AUTH_CAPTCHA = "captcha";
	
	public static final int FAN_GETTOKEN = 26;
	public static final String FAN_GETTOKEN_TYPE = "gettoken_type";
	public static final String FAN_GETTOKEN_TYPE_USER = "user";
	public static final String FAN_GETTOKEN_TYPE_POST = "post";
	
	public static final int FAN_QINIU_MYURI = 27;
	public static final String FAN_QINIU_TOKEN = "token";
	public static final String FAN_QINIU_URI = "uri";
	public static final String FAN_QINIU_DRR = "drr";
	public static final int FAN_QINIU_MYDRR = 28;
	public static final int FAN_QINIU_OTHERURI = 29;
	public static final int FAN_QINIU_OTHERDRR = 30;
	
	public static final int FAN_SIGNUP = 31;
	
	public Task() {
		
	}
	
	public Task(int taskId, Map<String, Object> taskParams) {
		this.taskId = taskId;
		this.taskParams = taskParams;
	}
	
	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public Map<String, Object> getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(Map<String, Object> taskParams) {
		this.taskParams = taskParams;
	}
	
}
