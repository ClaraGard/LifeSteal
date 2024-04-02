package fr.clawara.lifesteal.discord;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.bukkit.Bukkit;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fr.clawara.lifesteal.data.Data;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordManager {

	private static JDA client;
	
	private static long CONNECTED_VC_ID = 977702504961822781L;
	private static long WHITELISTED_VC_ID = 977702537794813963L;
	
	public static String SERVERMESSAGES_ID = "986306822266822737";
	public static String WELCOME_ID = "982079983830835212";
	public static String WHITELIST_ID = "970407747969486909";
	public static String RULES_ID = "977277545592520746";
	public static String INFO_ID = "970407791179214878";
	public static String ANNOUNCEMENTS_ID = "976142024405299230";

	public static JDA getClient() {
		return client;
	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, String> getWhitelist() {
		HashMap<String, String> map = (HashMap<String, String>) new Data(HashMap.class).get("whitelist");
		if (map == null) {
			return new HashMap<String, String>();
		}
		return map;
	}
	
	public static void initStatChannels() {
		updatePlayerCountChannel();
		updateWhitelistCountChannel();
	}
	
	public static void clearStatChannels() {
		getClient().getVoiceChannelById(CONNECTED_VC_ID).getManager().setName("❌ Server Offline").queue();
	}
	
	public static void updatePlayerCountChannel() {
		String name = "Connected : "+Bukkit.getOnlinePlayers().size();
		getClient().getVoiceChannelById(CONNECTED_VC_ID).getManager().setName(name).queue();

	}
	
	public static void updateWhitelistCountChannel() {
		String name = "Whitelisted : "+getWhitelist().size();
		getClient().getVoiceChannelById(WHITELISTED_VC_ID).getManager().setName(name).queue();
	}

	public static String getWhichWhitelisted(String discordID) {
		return haveWhitelisted(discordID) ? getWhitelist().get(discordID) : "Popbob";
	}

	public static boolean isWhitelisted(String uuid) {
		return getWhitelist().containsValue(uuid);
	}

	public static boolean haveWhitelisted(String discordID) {
		return getWhitelist().containsKey(discordID);
	}

	private static String getValidUUID(String toformat) {
		return toformat.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
				"$1-$2-$3-$4-$5");
	}

	public static void addWhitelist(String discordID, String uuid) {
		HashMap<String, String> map = getWhitelist();
		map.put(discordID, uuid);
		new Data(HashMap.class).send("whitelist", new Gson().toJson(map));
	}
	
	public static void removeWhitelist(String discordID) {
		HashMap<String, String> map = getWhitelist();
		map.remove(discordID);
		new Data(HashMap.class).send("whitelist", new Gson().toJson(map));
	}


	@SuppressWarnings("deprecation")
	public static PlayerIdentity getPlayerInfoFromAPI(String name) {
		try {
			URL url_0 = new URL("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + name);
			InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
			JsonObject obj = new JsonParser().parse(reader_0).getAsJsonObject();
			String uuid = obj.get("id").getAsString();
			String username = obj.get("name").getAsString();

			return new PlayerIdentity(username, "https://minotar.net/helm/" + username + "/100.png",
					getValidUUID(uuid));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static PlayerIdentity getPlayerInfoFromAPI(UUID uuid) {
		try {
			URL url_0 = new URL("https://api.mojang.com/user/profiles/"+uuid+"/names");
			InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
			JsonArray array = new JsonParser().parse(reader_0).getAsJsonArray();
			JsonObject obj = (JsonObject) array.get(array.size()-1);
			String username = obj.get("name").getAsString();
			return new PlayerIdentity(username, "https://minotar.net/helm/" + username + "/100.png",
					uuid.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static void initDiscord() {
		String token = (String) new Data(String.class).get("tkn");
		try {
			client = JDABuilder.createDefault(token).disableCache(CacheFlag.ACTIVITY)
					.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS,
							GatewayIntent.GUILD_PRESENCES)
					.addEventListeners(new MessageListener()).addEventListeners(new JoinListener()).addEventListeners(new ServerMessages())
					.setMemberCachePolicy(MemberCachePolicy.ALL).enableCache(CacheFlag.ACTIVITY).build();
			/*Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
				System.out.println("voice");
				for(VoiceChannel channel : client.getVoiceChannels()) {
					System.out.println(channel.getName()+" "+channel.getId());
				}
				System.out.println("news");
				for(NewsChannel channel : client.getNewsChannels()) {
					System.out.println(channel.getName()+" "+channel.getId());
				}
				System.out.println("private");
				for(PrivateChannel channel : client.getPrivateChannels()) {
					System.out.println(channel.getName()+" "+channel.getId());
				}
				System.out.println("stage");
				for(StageChannel channel : client.getStageChannels()) {
					System.out.println(channel.getName()+" "+channel.getId());
				}
				System.out.println("thread");
				for(ThreadChannel channel : client.getThreadChannels()) {
					System.out.println(channel.getName()+" "+channel.getId());
				}
				System.out.println("text");
				for(TextChannel channel : client.getTextChannels()) {
					System.out.println(channel.getName()+" "+channel.getId());
				}
			}, 100) ;*/
		} catch (LoginException e) {
			e.printStackTrace();
		}

	}
}
