package cn.ifanmi.findme.bean;

public class Status {

	private String id;
	private String personId;
	private String content;
	private String photos;
	private String releaseTime;
	private String readCount;
	private String praiseCount;
	private String messageCount;
	private String isOfficial;
	private String isTop;
	
	public static final String ID = "id";
	public static final String PERSONID = "personId";
	public static final String CONTENT = "content";
	public static final String PHOTOS = "photos";
	public static final String RELEASETIME = "releaseTime";
	public static final String READCOUNT = "readCount";
	public static final String PRAISECOUNT = "praiseCount";
	public static final String MESSAGECOUNT = "messageCount";
	public static final String ISOFFICIAL = "isOfficial";
	public static final String ISOFFICIAL_YES = "1";
	public static final String ISOFFICIAL_NO = "2";
	public static final String OFFICIAL_NICKNAME = "官方发布";
	public static final String ANONY_NICKNAME = "匿名发布";
	public static final String ISTOP = "isTop";
	public static final String ISTOP_YES = "1";
	public static final String ISTOP_NO = "0";
	public static final int MAX_PHOTO_COUNT = 9;
	
	public Status() {
		
	}

	public Status(String id, String personId, String content, String photos,
			String releaseTime, String readCount, String praiseCount,
			String messageCount, String isOfficial, String isTop) {
		this.id = id;
		this.personId = personId;
		this.content = content;
		this.photos = photos;
		this.releaseTime = releaseTime;
		this.readCount = readCount;
		this.praiseCount = praiseCount;
		this.messageCount = messageCount;
		this.isOfficial = isOfficial;
		this.isTop = isTop;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPhotos() {
		return photos;
	}
	public void setPhotos(String photos) {
		this.photos = photos;
	}
	public String getReleaseTime() {
		return releaseTime;
	}
	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}
	public String getReadCount() {
		return readCount;
	}
	public void setReadCount(String readCount) {
		this.readCount = readCount;
	}
	public String getPraiseCount() {
		return praiseCount;
	}
	public void setPraiseCount(String praiseCount) {
		this.praiseCount = praiseCount;
	}
	public String getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(String messageCount) {
		this.messageCount = messageCount;
	}
	public String getIsOfficial() {
		return isOfficial;
	}
	public void setIsOfficial(String isOfficial) {
		this.isOfficial = isOfficial;
	}
	public String getIsTop() {
		return isTop;
	}
	public void setIsTop(String isTop) {
		this.isTop = isTop;
	}

	@Override
	public String toString() {
		return "Status [id=" + id + ", personId=" + personId + ", content="
				+ content + ", photos=" + photos + ", releaseTime="
				+ releaseTime + ", readCount=" + readCount + ", praiseCount="
				+ praiseCount + ", messageCount=" + messageCount
				+ ", isOfficial=" + isOfficial + ", isTop=" + isTop + "]";
	}
	
}
