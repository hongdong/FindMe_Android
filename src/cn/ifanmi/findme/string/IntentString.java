package cn.ifanmi.findme.string;

public class IntentString {

	public static class RequestCode {
		public static final int IMAGE = 1;
		public static final int CAMERA = 2;
		public static final int PHOTO = 3;
		public static final int COMPLETEUD_SELECTCL = 4;
		public static final int GROUP_WRITESTATUS = 5;
		public static final int CHAT_BAIDUMAP = 6;
		public static final int CHAT_ALERT_EMPTY = 7;
		public static final int CHAT_ALERT_TEXT = 8;
		public static final int CHAT_ALERT_VOICE = 9;
		public static final int CHAT_ALERT_PICTURE = 10;
		public static final int CHAT_ALERT_LOCATION = 11;
		public static final int CHAT_ALERT_VIDEO = 12;
		public static final int CHAT_ALERT_FILE = 13;
		public static final int CHAT_CONTEXTMENU = 14;
		public static final int CHAT_VIDEO = 15;
		public static final int CHAT_FILE = 16;
	}
	
	public static class ResultCode {
		public static final int SELECTCL_COMPLETEUD = 4;
		public static final int WRITESTATUS_GROUP = 5;
		public static final int BAIDUMAP_CHAT = 6;
		public static final int ALERT_CHAT = 7;
		public static final int CONTEXTMENU_CHAT_COPY = 14;
		public static final int CONTEXTMENU_CHAT_DELETE = 15;
		public static final int CONTEXTMENU_CHAT_FORWARD = 16;
		public static final int CONTEXTMENU_CHAT_OPEN = 17;
		public static final int CONTEXTMENU_CHAT_DOWNLOAD = 18;
		public static final int CONTEXTMENU_CHAT_TO_CLOUD = 19;
	}
	
	public static class Extra {
		public static final String COLLEGEID = "collegeId";
		public static final String PHOTOS = "photos";
		public static final String CLICK_POSITION = "click_position";
		public static final String TYPE = "type";
		public static final String BUCKET_NAME = "bucket_name";
		public static final String IMAGE_LIST = "imagelist";
		
		public static final String ALERT_TITLEISCANCLE = "titleIsCancel";
		public static final String ALERT_MESSAGE = "message";
		public static final String ALERT_TITLE = "title";
		public static final String ALERT_CANCLE = "cancle";
		public static final String ALERT_POSITION = "position";
	}
	
	public static class Push {
		public static final String TYPE = "type";
		public static final String TYPE_FORCE_QUIT = "10001";
		public static final String TYPE_STATUS_INFO = "10002";
		public static final String TYPE_HAS_MATCH = "10003";
		public static final String TYPE_GIRL_LIKE_YOU = "10004";
		public static final String TYPE_NEW_FRIEND = "10005";
		public static final String TYPE_BOY_LIKE_YOU = "10006";
	}
	
	public static class EM {
		public static final String ACTION_CLICK = "em_click";
		public static final String TYPE_CLICK = "10000";
	}
	
	public class Receiver {
		public static final String NEW_INFO = "cn.ifanmi.fangme.NEW_INFO";
		public static final String NEW_FRIEND = "cn.ifanmi.fangme.NEW_FRIEND";
	}
	
}
