package fr.clawara.lifesteal.sanctions.mutes;

import fr.clawara.lifesteal.sanctions.Sanction;



public class Mute extends Sanction {
	
	public Mute(String reason, long time, String comment) {
		super(reason, time, comment);
	}
}
