package cn.ifanmi.findme.bean;

import com.easemob.chat.EMContact;

public class Friend extends EMContact {

	private String id;
	private String photo;
	private String header;
	private String nickname;
	private String signature;
	private String urmCount;
	
	public static final String ID = "id";
	public static final String PHOTO = "photo";
	public static final String HEADER = "header";
	public static final String NICKNAME = "nickname";
	public static final String SIGNATURE = "signature";
	public static final String URMCOUNT = "urmCount";
	
	public Friend() {
		
	}
	
	public Friend(String id, String photo, String header, String nickname,
			String signature, String urmCount) {
		this.id = id;
		this.photo = photo;
		this.header = header;
		this.nickname = nickname;
		this.signature = signature;
		this.urmCount = urmCount;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getUrmCount() {
		return urmCount;
	}
	public void setUrmCount(String urmCount) {
		this.urmCount = urmCount;
	}
	
}
