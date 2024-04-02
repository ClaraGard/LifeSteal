package fr.clawara.lifesteal.sanctions.bans;

import fr.clawara.lifesteal.sanctions.Sanction;



public class Ban extends Sanction {
	
	public Ban(String reason, long time, String comment) {
		super(reason, time, comment);
	}
}
