package fr.clawara.lifesteal.data;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.reflect.TypeToken;

import fr.clawara.lifesteal.main.Config;
import fr.clawara.lifesteal.main.LifeStealPlayer;

public enum DataType {
	
	DISCORD_WHITELISTED("whitelist", HashMap.class, new TypeToken<HashMap<String, String>>() {}.getType()),
	DISCORD_TOKEN("discord", String.class, new TypeToken<String>() {}.getType()),
	PLAYER_PROFILES("profiles", LifeStealPlayer.class, new TypeToken<LifeStealPlayer>() {}.getType()),
	CONFIG("config", Config.class, new TypeToken<Config>() {}.getType());
	
	private String src;
	private Class<?> dataClass;
	private Type token;
	
	private DataType(String src, Class<?> dataClass, Type token) {
		this.src = src;
		this.token = token;
		this.dataClass = dataClass;
	}
	
	public Type getToken() {
		return token;
	}
	
	public Class<?> getDataClass() {
		return dataClass;
	}
	
	public String getSrc() {
		return src;
	}
	
	
	
	public static DataType getType(Class<?> c) {
		return Arrays.stream(values()).filter(e -> e.getDataClass().toString().equals(c.toString())).findAny().orElse(null);
	}

}
