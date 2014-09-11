package cn.ifanmi.findme.bean;

public class InfoStatus {

	private String isRead;
	private String updateTime;
	private Status status;
	
	public InfoStatus() {
		
	}
	
	public InfoStatus(String isRead, String updateTime, Status status) {
		this.isRead = isRead;
		this.updateTime = updateTime;
		this.status = status;
	}
	
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "InfoStatus [isRead=" + isRead + ", updateTime=" + updateTime
				+ ", status=" + status + "]";
	}
	
}
