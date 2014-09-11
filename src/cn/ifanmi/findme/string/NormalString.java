package cn.ifanmi.findme.string;

public class NormalString {
	
	public class Fanmi {
		public static final String URL = "http://www.ifanmi.cn/";
		public static final String VERSION_CODE = "1.0";
	}
	
	public class Pattern {
		public static final String EXPRESSION = "\\[fac[0-1][0-9][0-9]";
		public static final String EXP_FILE_PREFIX = "smiley_";
		public static final int EXP_PREFIX_LEN = 4;
		public static final int EXP_START = 0;
		public static final int EXP_END = 105;
	}
	
	public class List {
		public static final String LEFT = "left";
		public static final String RIGHT = "right";
	}
	
	public class Handler {
		public static final int WHAT_SUCCESS = 1;
		public static final int WHAT_ERROR = 2;
	}
	
	public class File {
		public static final String DIR_FANMI = "/fanmi/";
		public static final String IMAGE_FILE_NAME = "fanmiPhoto.png";
		public static final String IMAGE_DIR = "chat/image/";
		public static final String VOICE_DIR = "chat/audio/";
		public static final String VIDEO_DIR = "chat/video";
	}
	
	public class ChatViewType {
		public static final int COUNT = 12;
		public static final int INVALID = -1;
		public static final int RECV_TXT = 0;
		public static final int SENT_TXT = 1;
		public static final int SENT_IMAGE = 2;
		public static final int SENT_LOCATION = 3;
		public static final int RECV_LOCATION = 4;
		public static final int RECV_IMAGE = 5;
		public static final int SENT_VOICE = 6;
		public static final int RECV_VOICE = 7;
		public static final int SENT_VIDEO = 8;
		public static final int RECV_VIDEO = 9;
		public static final int SENT_FILE = 10;
		public static final int RECV_FILE = 11;
	}
	
	public class StatusViewType {
		public static final int COUNT = 3;
		public static final int INVALID = -1;
		public static final int NO_PICTURE = 0;
		public static final int ONE_PICTURE = 1;
		public static final int MULTI_PICTURE = 2;
	}
	
	public class SmallPhoto {
		public static final String PERSON_FIND = "250";
		public static final String PERSON_FANS = "150";
		public static final String PERSON_INFO = "150";
		public static final String PERSON_FRIEND = "150";
		public static final String PERSON_CHAT = "150";
		public static final String STATUS_ONE = "300";
		public static final String STATUS_MULTI = "250";
		public static final String PERSON_ME = "250";
		public static final String PERSON_DATA = "250";
		public static final String ALBUM = "250";
	}
	
	public class Other {
		public static final int EM_PAGESIZE = 20;
	}
	
}
