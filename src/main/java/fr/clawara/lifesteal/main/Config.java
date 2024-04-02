package fr.clawara.lifesteal.main;

import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;

import fr.clawara.lifesteal.data.Data;
import fr.clawara.lifesteal.discord.ServerMessages;

public class Config {
	public int tpaDelay = 30*60*1000;
	public int rtpDelay = 24*60*60*1000;
	public int rtpMaxDistance = 2000;
	public long serverOpening = 1655668800000L;
	public int combatTime = 30;
	public int heartLimit = 20;
	public int banTimeDeath = 168;
	public boolean daily = true;
	public boolean heartDaily = false;
	public int maxHeartsDailyHeart = 5;
	public boolean randomItemDaily = true;
	public boolean withdrawDaily = true;
	public boolean elytraBoostInCombat = false;
	public boolean gracePeriod = true;
	public int gracePeriodStart = 30*60;
	public int gracePeriodMax = 60*60*5;
	public int gracePeriodPerHour = 3*60;
	public int heartsAfterBan = 8;
	public boolean forceDragonEggRespawn = false;
	
	public static Config load() {
		Config config = (Config) new Data(Config.class).get("config");
		return config;
	}
	
	public void dragonEggDestroyed() {
		ServerMessages.dragonEggDestroyedMessage();
		forceDragonEggRespawn = true;
		save();
	}
	
	public void dragonDied() {
		forceDragonEggRespawn = false;
		save();
	}
	
	public void save() {
		new Data(Config.class).send("config", new Gson().toJson(this));
	}
}
