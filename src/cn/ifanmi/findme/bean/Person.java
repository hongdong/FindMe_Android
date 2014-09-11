package cn.ifanmi.findme.bean;

import com.easemob.chat.EMContact;

public class Person extends EMContact {

	private String id;
	private String photo;
	private String nickname;
	private String signature;
	private String collegeName;
	private String deptName;
	private String grade;
	private String sex;
	private String name;
	private String cl;
	private String album;
	private String isAuth;
	
	public static final String ID = "id";
	public static final String PHOTO = "photo";
	public static final String NICKNAME = "nickname";
	public static final String SIGNATURE = "signature";
	public static final String COLLEGENAME = "collegeName";
	public static final String DEPTNAME = "deptName";
	public static final String GRADE = "grade";
	public static final String SEX = "sex";
	public static final String NAME = "name";
	public static final String CL = "cl";
	public static final String ALBUM = "album";
	public static final String ISAUTH = "isAuth";
	
	public static final String MALE = "男";
	public static final String FEMALE = "女";
	public static final String ISAUTH_YES = "1";
	public static final String ISAUTH_NO = "0";
	public static final int ALBUM_MAX_SELECT = 9;
	public static final int ALBUM_MAX_COUNT = 18;
	
	public Person() {
		
	}

	public Person(String id, String photo, String nickname, String signature,
			String collegeName, String deptName, String grade, String sex,
			String name, String cl, String album, String isAuth) {
		this.id = id;
		this.photo = photo;
		this.nickname = nickname;
		this.signature = signature;
		this.collegeName = collegeName;
		this.deptName = deptName;
		this.grade = grade;
		this.sex = sex;
		this.name = name;
		this.cl = cl;
		this.album = album;
		this.isAuth = isAuth;
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
	public String getCollegeName() {
		return collegeName;
	}
	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCl() {
		return cl;
	}
	public void setCl(String cl) {
		this.cl = cl;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getIsAuth() {
		return isAuth;
	}
	public void setIsAuth(String isAuth) {
		this.isAuth = isAuth;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", photo=" + photo + ", nickname="
				+ nickname + ", signature=" + signature + ", collegeName="
				+ collegeName + ", deptName=" + deptName + ", grade=" + grade
				+ ", sex=" + sex + ", name=" + name + ", cl=" + cl + ", album="
				+ album + ", isAuth=" + isAuth + "]";
	}
	
}
