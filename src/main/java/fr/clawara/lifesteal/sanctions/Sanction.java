package fr.clawara.lifesteal.sanctions;

import java.util.Date;



public abstract class Sanction {
	
	private Date ending;
	private Date beginning;
	private String reason;
	private String comment;
	
	public Sanction(String reason, long time, String comment) {
		this.reason = reason;
		this.comment = comment;
		this.beginning = new Date(System.currentTimeMillis());
		this.ending = new Date(System.currentTimeMillis()+(time*60*60*1000));
	}
	
	public boolean isExpired() {
		return !ending.after(new Date());
	}
	
	public String getReason() {
		return reason;
	}
	
	public Date getEnding() {
		return ending;
	}
	
	public Date getBeginning() {
		return beginning;
	}
	
	public long getTimeLeft() {
		return Math.max(0, ending.getTime() - System.currentTimeMillis());
	}
	
	public void end() {
		ending = new Date(System.currentTimeMillis());
	}
	
	public String getComment() {
		return comment;
	}
}
