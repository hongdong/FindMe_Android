package cn.ifanmi.findme.string;

public class JsonString {

	public static class Return {
		public static final String RESULT = "result";
		public static final String STATE = "state";
	}
	
	public static class Dept {
		public static final String DEPT_LIST = "department";
		public static final String DEPT_NAME = "deptName";
	}
	
	public static class Updata {
		public static final String VERSION_INFO = "versionInfo";
		public static final String VERSION_CODE = "versionCode";
		public static final String DISPLAY_MESSAGE = "displayMessage";
		public static final String DOWNLOAD_URL = "downloadURL";
		public static final String APK_NAME = "apkName";
	}
	
	public static class Status {
		public static final String STATUS_LIST = "postList";
		public static final String ID = "_id";
		public static final String CONTENT = "postContent";
		public static final String PERSON = "postUser";
		public static final String PERSONID = "_id";
		public static final String PHOTOS = "postPhoto";
		public static final String RELEASETIME = "postReleaseTime";
		public static final String READCOUNT = "postReadNumber";
		public static final String PRAISECOUNT = "postPraise";
		public static final String MESSAGECOUNT = "postMsgNumber";
		public static final String ISOFFICIAL = "postOfficial";
		public static final String ISTOP = "postTop";
		public static final String ISREAD = "isRead";
		public static final String UPDATETIME = "updateTime";
	}
	
	public static class StatusInfo {
		public static final String STATUSINFO_LIST = "newsList";
	}
	
	public static class MapSDM {
		public static final String MAP_SDM = "postMsg";
		public static final String INDEX = "index";
		public static final String BEGIN_INDEX = "beginIndex";
		public static final String END_INDEX = "endIndex";
		public static final String IS_PRAISE = "isPraise";
		public static final String MESSAGE_LIST = "postMsgList";
		public static final String ID = "_id";
		public static final String PERSON = "postMsgUser";
		public static final String PERSONID = "_id";
		public static final String CONTENT = "postMsgContent";
		public static final String RELEASETIME = "postMsgTime";
	}
	
	public static class Person {
		public static final String PERSON = "userInfo";
		public static final String ID = "_id";
		public static final String PHOTO = "userPhoto";
		public static final String NICKNAME = "userNickName";
		public static final String SIGNATURE = "userSignature";
		public static final String COLLEGE = "school";
		public static final String COLLEGENAME = "schoolName";
		public static final String DEPT = "department";
		public static final String DEPTNAME = "deptName";
		public static final String GRADE = "userGrade";
		public static final String SEX = "userSex";
		public static final String NAME = "userRealName";
		public static final String CL = "userConstellation";
		public static final String ALBUM = "userAlbum";
		public static final String ISAUTH = "userAuth";
	}
	
	public static class Friend {
		public static final String FRIEND_LIST = "friendList";
		public static final String ID = "_id";
		public static final String PHOTO = "userPhoto";
		public static final String NICKNAME = "userNickName";
		public static final String SIGNATURE = "userSignature";
	}
	
	public static class Match {
		public static final String MATCH = "userMatch";
		public static final String HAS_LIKE = "likeToday";
		public static final String PERSON = "user";
		public static final String COUNTDOWN = "nexttime";
	}
	
	public static class Fans {
		public static final String FANS_LIST = "userFans";
	}
	
}
