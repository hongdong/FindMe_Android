package cn.ifanmi.findme.bean;

public class Match {

	private String hasLike;
	private Person person;
	private String countdown;
	
	public Match() {
		
	}

	public Match(String hasLike, Person person, String countdown) {
		this.hasLike = hasLike;
		this.person = person;
		this.countdown = countdown;
	}

	public String getHasLike() {
		return hasLike;
	}
	public void setHasLike(String hasLike) {
		this.hasLike = hasLike;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public String getCountdown() {
		return countdown;
	}
	public void setCountdown(String countdown) {
		this.countdown = countdown;
	}

	@Override
	public String toString() {
		return "Match [hasLike=" + hasLike + ", person=" + person
				+ ", countdown=" + countdown + "]";
	}
	
}
