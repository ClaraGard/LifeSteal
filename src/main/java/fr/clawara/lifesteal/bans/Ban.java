package fr.clawara.lifesteal.bans;

import java.util.Date;



public class Ban {
	
	private Date ending;
	private Date beginning;
	private Reason reason;
	private String comment;
	
	public Ban(Reason reason, int time, String comment) {
		this.reason = reason;
		this.comment = comment;
		this.beginning = new Date(System.currentTimeMillis());
		this.ending = new Date(System.currentTimeMillis()+(time*60*60*1000));
	}
	
	public boolean isExpired() {
		return !ending.after(new Date());
	}
	
	public Reason getReason() {
		return reason;
	}
	
	public Date getEnding() {
		return ending;
	}
	
	public void end() {
		ending = new Date(System.currentTimeMillis());
	}
}
