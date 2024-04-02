package fr.clawara.lifesteal.discord;

public class PlayerIdentity {

	
	private String playername,skinURL,uuid;
	
	public PlayerIdentity(String playername, String skinURL, String uuid) {
		this.playername = playername;
		this.skinURL = skinURL;
		this.uuid = uuid;
	}
	
	public String getUUID() {
		return uuid;
	}
	
	public String getPlayerName() {
		return playername;
	}
	
	public String getSkin() {
		return skinURL;
	}
}
