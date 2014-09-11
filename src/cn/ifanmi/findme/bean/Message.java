package cn.ifanmi.findme.bean;

public class Message {

	private String id;
	private String personId;
	private String content;
	private String releaseTime;
	
	public static final String ID = "id";
	public static final String CONTENT = "content";

	public Message() {
		
	}

	public Message(String id, String personId, String content,
			String releaseTime) {
		this.id = id;
		this.personId = personId;
		this.content = content;
		this.releaseTime = releaseTime;
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
	public String getReleaseTime() {
		return releaseTime;
	}
	public void setReleaseTime(String releaseTime) {
		this.releaseTime = releaseTime;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", personId=" + personId + ", content="
				+ content + ", releaseTime=" + releaseTime + "]";
	}
	
}
