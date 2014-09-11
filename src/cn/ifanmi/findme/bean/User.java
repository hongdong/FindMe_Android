package cn.ifanmi.findme.bean;

public class User {

	private String id;
	private String phoneNumber;
	private String password;
	private String collegeId;
	private String collegeName;
	private String deptId;
	private String deptName;
	private String grade;
	private String photo;
	private String name;
	private String sex;
	private String cl;
	private String nickname;
	
	public static final String ID = "id";
	public static final String PHONENUMBER = "phoneNumber";
	public static final String PASSWORD = "password";
	public static final String COLLEGEID = "collegeId";
	public static final String COLLEGENAME = "collegeName";
	public static final String DEPTID = "deptId";
	public static final String DEPTNAME = "deptName";
	public static final String GRADE = "grade";
	public static final String CL = "cl";
	public static final String NAME = "name";
	public static final String PHOTO = "photo";
	public static final String SEX = "sex";
	public static final String NICKNAME = "nickname";
	
	public static final String EM_PASSWORD = "123456";
	
	public User() {
		
	}

	public User(String id, String phoneNumber, String password,
			String collegeId, String collegeName, String deptId,
			String deptName, String grade) {
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.collegeId = collegeId;
		this.collegeName = collegeName;
		this.deptId = deptId;
		this.deptName = deptName;
		this.grade = grade;
	}

	public User(String id, String phoneNumber, String password,
			String collegeId, String collegeName, String deptId,
			String deptName, String grade, String photo, String name,
			String sex, String cl, String nickname) {
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.password = password;
		this.collegeId = collegeId;
		this.collegeName = collegeName;
		this.deptId = deptId;
		this.deptName = deptName;
		this.grade = grade;
		this.photo = photo;
		this.name = name;
		this.sex = sex;
		this.cl = cl;
		this.nickname = nickname;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCollegeId() {
		return collegeId;
	}
	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}
	public String getCollegeName() {
		return collegeName;
	}
	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}
	public String getDeptId() {
		return deptId;
	}
	public void setDeptId(String deptId) {
		this.deptId = deptId;
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
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getCl() {
		return cl;
	}
	public void setCl(String cl) {
		this.cl = cl;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", phoneNumber=" + phoneNumber
				+ ", password=" + password + ", collegeId=" + collegeId
				+ ", collegeName=" + collegeName + ", deptId=" + deptId
				+ ", deptName=" + deptName + ", grade=" + grade + ", photo="
				+ photo + ", name=" + name + ", sex=" + sex + ", cl=" + cl
				+ ", nickname=" + nickname + "]";
	}

}
