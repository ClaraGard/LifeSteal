package fr.clawara.lifesteal.main;

import java.awt.Color;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.google.gson.Gson;

import fr.clawara.lifesteal.data.Data;
import fr.clawara.lifesteal.data.DataManager;
import fr.clawara.lifesteal.data.DataType;
import fr.clawara.lifesteal.discord.DiscordManager;
import fr.clawara.lifesteal.sanctions.bans.Ban;
import fr.clawara.lifesteal.sanctions.mutes.Mute;
import fr.clawara.lifesteal.teleportations.TeleportRequest;
import fr.clawara.lifesteal.teleportations.TeleportType;
import fr.clawara.lifesteal.utils.ScoreboardUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class LifeStealPlayer {

	private UUID uuid;
	private String idDiscord;
	private String locationInTheWorld;
	private String username;
	private int hearts;
	private int storedHearts;
	private int kills;
	private int deaths;
	private int withdrawsLeft;
	private Rank rank;
	private List<Ban> bans;
	private List<Mute> mutes;
	private Date firstLogin;
	public int gracePeriodLeft;
	private long lastHit;
	private boolean inCombat;
	private boolean respawnInBed;
	private long lastDailyCommand;
	private long lastTpa;
	private long lastRtp;
	private long lastRtpCommand;
	private Inventory dailyInventory;
	private Inventory scoreboardManageInventory;
	private boolean hasConnected;
	private String address;
	
	public boolean showWithdraw = true;
	public boolean showGracePeriod = true;
	public boolean showCombat = true;
	public boolean showTpa = true;
	public boolean showRtp = true;
	public boolean showDaily = true;
	public boolean showKills = true;
	public boolean showDeaths = true;
	public boolean scoreboardVisible = true;
	
	public LifeStealPlayer(UUID uuid, String idDiscord) {
		this.uuid = uuid;
		this.idDiscord = idDiscord;
		this.hearts = 10;
		this.storedHearts = 0;
		this.withdrawsLeft = 0;
		this.deaths = 0;
		this.kills = 0;
		this.rank = Rank.PLAYER;
		this.bans = new ArrayList<Ban>();
		this.mutes = new ArrayList<Mute>();
		this.lastHit = 0;
		this.inCombat = false;
		this.respawnInBed = true;
		this.lastDailyCommand = 0;
		this.lastRtp = 0;
		this.lastRtpCommand = 0;
		this.dailyInventory = null;
		this.hasConnected = false;
		this.setAddress(null);
		save();
	}

	public static LifeStealPlayer get(UUID uuid) {
		LifeStealPlayer player = null;
		if (Main.getInstance().getLifeStealMap().containsKey(uuid))
			player = Main.getInstance().getLifeStealMap().get(uuid);
		if (player == null)
			player = (LifeStealPlayer) new Data(LifeStealPlayer.class).get(uuid.toString());
		if(player!=null && player.getMutes()==null) player.mutes = new ArrayList<>();
		return player;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public static void delete(UUID uuid) {
		DataManager.deleteFile(DataType.PLAYER_PROFILES, uuid.toString());
	}
	
	public boolean isNewPlayer() {
		return !hasConnected;
	}
	
	public Location getLocationInTheWorld() {
		return Main.locationFromString(locationInTheWorld);
	}
	
	public void setLocationInTheWorld(Location loc) {
		this.locationInTheWorld = Main.stringFromLocation(loc);
		save();
	}
	
	public void firstConnection(PlayerSpawnLocationEvent spawnEvent) {
		firstLogin = new Date();
		spawnEvent.setSpawnLocation(Main.spawn);
		//getBukkitPlayer().teleportAsync(Main.spawn);
		locationInTheWorld = Main.stringFromLocation(Main.getValidSpawnAround(Main.worldSpawn));
		if(Main.config.gracePeriod) gracePeriodLeft = Math.min(Main.config.gracePeriodMax, Main.config.gracePeriodStart+Main.config.gracePeriodPerHour*((int) (System.currentTimeMillis()-Main.config.serverOpening)/(1000*60*60)));
		getBukkitPlayer().getEnderChest().clear();
		getBukkitPlayer().getInventory().clear();
		getBukkitPlayer().getInventory().setArmorContents(null);
		getBukkitPlayer().getInventory().setItemInOffHand(null);
		getBukkitPlayer().setBedSpawnLocation(null);
		getBukkitPlayer().setFoodLevel(20);
		getBukkitPlayer().setSaturation(20);
		getBukkitPlayer().getInventory().addItem(new ItemStack(Material.COOKED_BEEF,32));
		getBukkitPlayer().getInventory().addItem(new ItemStack(Material.WHITE_BED));
		getBukkitPlayer().sendMessage("§eYou can set your pronouns with /pronouns set ");
		Bukkit.broadcastMessage("§dWelcome "+getBukkitPlayer().getName()+" !");
		JDA client = DiscordManager.getClient();
        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(getBukkitPlayer().getName()+" joined for the first time");
		builder.setColor(java.awt.Color.pink);
		serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
		hasConnected = true;
		save();
	}
	
	public long getTpaDelay() {
		return lastTpa+Main.config.tpaDelay-System.currentTimeMillis();
	}
	
	public Inventory getDailyInventory() {
		return dailyInventory;
	}
	
	public Inventory getScoreboardInventory() {
		return scoreboardManageInventory;
	}
	
	public boolean canRtp() {
		return lastRtp+Main.config.rtpDelay<System.currentTimeMillis();
	}
	
	public void bed() {
		if(getBukkitPlayer().getBedSpawnLocation()==null) {
			new TeleportRequest(this, Main.getValidSpawnAround(Main.worldSpawn), TeleportType.BED);
		}else {
			new TeleportRequest(this, getBukkitPlayer().getBedSpawnLocation(), TeleportType.BED);
		}
	}
	
	public void rtp() {
		if(System.currentTimeMillis()<lastRtpCommand+5*1000) {
			getBukkitPlayer().sendMessage("§cDo not spam this command.");
			return;
		}
		lastRtpCommand = System.currentTimeMillis();
		if(!canRtp()) {
			getBukkitPlayer().sendMessage("§cYou have to wait "+Main.getTimeLeft(lastRtp+Main.config.rtpDelay-System.currentTimeMillis())+" before being able to rtp again.");
			return;
		}
		Location loc;
		do {
			loc = new Location(Bukkit.getWorld("world"), Main.random.nextInt(Main.config.rtpMaxDistance*2)-Main.config.rtpMaxDistance+0.5, 100, Main.random.nextInt(Main.config.rtpMaxDistance*2)-Main.config.rtpMaxDistance+0.5);
			loc.setY(loc.getWorld().getHighestBlockYAt(loc));
		}
		while(!Main.isSafe(loc));
		new TeleportRequest(this, loc, TeleportType.RTP);
	}
	
	public void useRtp() {
		lastRtp = System.currentTimeMillis();
		save();
	}
	
	public boolean hasTpa() {
		return lastTpa+Main.config.tpaDelay<System.currentTimeMillis();
	}
	
	/*public void useTpaPaper() {
		if(hasTpa()) {
			tpaEnd+=(24*60*60*1000*2);
		}else {
			tpaEnd = System.currentTimeMillis() + (24*60*60*1000*2);
		}
		ItemStack inHand = getBukkitPlayer().getInventory().getItemInMainHand();
		inHand.setAmount(inHand.getAmount()-1);
		save();
	}*/
	
	public void useTpa() {
		lastTpa = System.currentTimeMillis();
		save();
	}
	
	public boolean canDaily() {
		return lastDailyCommand+24*60*60*1000<System.currentTimeMillis();
	}
	
	public void useDaily() {
		Player player = getBukkitPlayer();
		if (!canDaily()) {
			player.sendMessage("§cYou have to wait "+Main.getDelayString(lastDailyCommand, 24*60*60*1000)+" before doing /daily again.");
			return;
		}
		if(player.getInventory().firstEmpty()==-1) {
			player.sendMessage("§cPlase make room in your inventory first.");
			return;
		}
		Inventory inv = Bukkit.createInventory(null, 9);
		if(Main.config.randomItemDaily) inv.setItem(3, Main.randomItem.clone());
		if(Main.config.heartDaily) inv.setItem(4, Main.heart.clone());
		if(Main.config.withdrawDaily) inv.setItem(5, Main.withdrawItem.clone());
		player.openInventory(inv);
		dailyInventory = inv;
	}
	
	@SuppressWarnings("deprecation")
	public void getDaily(ItemStack it) {
		Player player = getBukkitPlayer();
		if(Main.isHeart(it)) {
			getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2);
			getBukkitPlayer().setHealth(getBukkitPlayer().getHealth() + 2);
			getBukkitPlayer().sendMessage("§cYou got §cone heart.");
			hearts++;
		}else if(Main.isWithdrawItem(it)) {
			/*getBukkitPlayer().sendMessage("§cYou got §cone withdraw.");
			withdrawsLeft++;*/
			if(getBukkitPlayer().getInventory().firstEmpty()==-1) {
				getBukkitPlayer().sendMessage("§cYour inventory is full.");
				return;
			}
			player.setHealth(player.getHealth() - 2);
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2);
			hearts -= 1;
			ItemStack heart = Main.heart.clone();
			heart.setAmount(1);
			player.getInventory().addItem(heart);
			player.sendMessage("§aYou withdrew one heart.");
		}
		else {
			if(getBukkitPlayer().getInventory().firstEmpty()==-1) {
				getBukkitPlayer().sendMessage("§cYour inventory is full.");
				return;
			}
			ItemStack randomItem = Main.getRandomItem();
			if(randomItem.getType()==Material.ENCHANTED_BOOK) {
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta)randomItem.getItemMeta();
				Enchantment enchant = Enchantment.values()[Main.random.nextInt(Enchantment.values().length)];
				while(enchant.isCursed()) {
					enchant = Enchantment.values()[Main.random.nextInt(Enchantment.values().length)];
				}
				meta.addStoredEnchant(enchant, enchant.getMaxLevel(), false);
				randomItem.setItemMeta(meta);
			}
			getBukkitPlayer().getInventory().addItem(randomItem);
			getBukkitPlayer().sendMessage("§eYou got §7"+randomItem.getAmount() + " " + ((randomItem.hasItemMeta() && randomItem.getItemMeta().hasDisplayName()) ? randomItem.getItemMeta().getDisplayName() : randomItem.getType().name()));
		}
		lastDailyCommand = System.currentTimeMillis();
		dailyInventory = null;
		save();
	}
	
	public boolean doesRespawnInBed() {
		return respawnInBed;
	}
	
	public void respawnToggle() {
		respawnInBed = !respawnInBed;
		save();
	}

	public void respawnInBed() {
		respawnInBed = true;
		save();
	}

	public void respawnAtSpawn() {
		respawnInBed = false;
		save();
	}

	public void scoreboardToggle() {
		scoreboardVisible = !scoreboardVisible;
		save();
	}

	public void enableScoreboard() {
		scoreboardVisible = true;
		save();
	}

	public void disableScoreboard() {
		scoreboardVisible = false;
		save();
	}

	public int getHearts() {
		return hearts;
	}

	public Player getBukkitPlayer() {
		return Bukkit.getPlayer(uuid);
	}
	
	public OfflinePlayer getOfflineBukkitPlayer() {
		return Bukkit.getOfflinePlayer(uuid);
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public void endGracePeriod() {
		gracePeriodLeft = -1;
		getBukkitPlayer().sendMessage("§cYour grace period is now over, you can be attacked by other players be careful!");
		System.out.println(getBukkitPlayer().getName()+" grace period is over");
		save();
	}

	public boolean isGracePeriodOver() {
		return gracePeriodLeft == -1;
	}

	public String gracePeriodLeftString() {
		long hours = gracePeriodLeft / (60 * 60);
		long minutes = gracePeriodLeft / (60) - hours * 60;
		long seconds = gracePeriodLeft - minutes * 60 - hours * 60 * 60;
		return hours + " hours " + minutes + " minutes and " + seconds + " seconds";
	}

	public void ban(String reason, long time, String comment) {
		bans.add(new Ban(reason, time, comment));
		String message = "";
		if (reason.equals("DEATH")) {
			message = "§cYou have reached 0 hearts, you are now banned temporarly.\nWhen you log back again, you will have to start everything over again.";
		} else {
			message = "§cYou have been banned for "+ reason;
		}
		save();
		if(getBukkitPlayer()!=null && getBukkitPlayer().isOnline()) getBukkitPlayer().kickPlayer(message);
	}

	public List<Ban> getBans() {
		return bans;
	}
	
	public void mute(String reason, long time, String comment) {
		mutes.add(new Mute(reason, time, comment));
		String message = "";
		message = "§cYou have been muted for "+ reason+" for "+time+" hours.";
		if(getBukkitPlayer()!=null && getBukkitPlayer().isOnline()) getBukkitPlayer().sendMessage(message);
		save();
	}
	
	public List<Mute> getMutes() {
		return mutes;
	}

	public Rank getRank() {
		return rank;
	}

	public void hit() {
		lastHit = System.currentTimeMillis();
		combat(true);
	}

	public long getLastHit() {
		return lastHit;
	}

	public boolean isInCombat() {
		return inCombat;
	}

	public void combat(boolean value) {
		if (inCombat != value) {
			if (value) {
				getBukkitPlayer().sendMessage("§cYou are now in combat, do not disconnect within the next "
						+ Main.config.combatTime + " seconds or you will be killed.");
				System.out.println(getBukkitPlayer().getName()+ " is now in combat.");
			} else {
				getBukkitPlayer().sendMessage("§aYou are not in combat anymore, you can disconnect safely.");
				System.out.println(getBukkitPlayer().getName()+ " is not in combat.");
			}
			inCombat = value;
		}
	}
	@Deprecated
	public void withdrawOld(int i) {
		Player player = getBukkitPlayer();
		if (withdrawsLeft <= 0) {
			player.sendMessage("§cYou do not have any withdraws left.");
			return;
		}
		if (i <= 0) {
			player.sendMessage("§cThe amount of hearts must be positive.");
			return;
		}
		if (i >= player.getHealth() / 2) {
			player.sendMessage("§cThe amount of hearts must be inferior to your current health.");
			return;
		}
		player.setHealth(player.getHealth() - 2 * i);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
				.setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2 * i);
		hearts -= i;
		storedHearts += i;
		withdrawsLeft--;
		player.sendMessage("§a" + i + " hearts have been stored successfuly.");
		save();
	}
	
	public void withdraw(int i) {
		Player player = getBukkitPlayer();
		/*if (withdrawsLeft <= 0) {
			player.sendMessage("§cYou do not have any withdraws left.");
			return;
		}*/
		if (i <= 0) {
			player.sendMessage("§cThe amount of hearts must be positive.");
			return;
		}
		if (i >= player.getHealth() / 2) {
			player.sendMessage("§cThe amount of hearts must be inferior to your current health.");
			return;
		}
		if(player.getInventory().firstEmpty()==-1) {
			player.sendMessage("§cYour inventory is full.");
			return;
		}
		player.setHealth(player.getHealth() - 2 * i);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2 * i);
		hearts -= i;
		//withdrawsLeft--;
		ItemStack heart = Main.heart.clone();
		heart.setAmount(i);
		player.getInventory().addItem(heart);
		player.sendMessage("§aYou withdrew one heart.");
		save();
	}
	
	public int getWithdraws() {
		return withdrawsLeft;
	}
	
	public void useHeart() {
		Player player = getBukkitPlayer();
		if (hearts == Main.config.heartLimit) {
			player.sendMessage("§cYou cannot have more than "+Main.config.heartLimit+" hearts");
			return;
		}
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2);
		player.setHealth(player.getHealth() + 2);
		hearts++;
		ItemStack inHand = player.getInventory().getItemInMainHand();
		inHand.setAmount(inHand.getAmount()-1);
		//player.getInventory().setItemInMainHand(inHand);
		save();
	}
	
	public void setHearts(int hearts) {
		this.hearts = hearts;
	}

	public void takeHearts(int i) {
		Player player = getBukkitPlayer();
		if (i <= 0) {
			player.sendMessage("§cThe amount of hearts must be positive");
			return;
		}
		if (i > storedHearts) {
			player.sendMessage("§cThe amount of hearts must be inferior to the amount you have stored.");
			return;
		}
		if (hearts == 20) {
			player.sendMessage("§cYou cannot have more than 20 hearts");
			return;
		}
		if (i + hearts > 20) {
			player.sendMessage("§cYou cannot have more than 20 hearts");
			i = 20 - hearts;
		}
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
				.setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2 * i);
		player.setHealth(player.getHealth() + 2 * i);
		hearts += i;
		storedHearts -= i;
		player.sendMessage("§a" + i + " hearts have been added successfuly.");
		save();
	}
	
	public void killed(boolean inArena) {
		Player player = getBukkitPlayer();
		combat(false);
		if (hearts == 1) {
			String possessive = PlaceholderAPI.setPlaceholders(player, "%pronouns_possessiveadj%");
			String progressive = PlaceholderAPI.setPlaceholders(player, "%pronouns_progressive%");
			player.getEnderChest().clear();
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.getInventory().setItemInOffHand(null);
			player.setBedSpawnLocation(null);
			ban("DEATH", Main.config.banTimeDeath, "Player died");
			hearts = Main.config.heartsAfterBan;
			if(Main.config.gracePeriod) gracePeriodLeft = Main.config.gracePeriodMax;
			Bukkit.broadcast(Component.text(player.getName(), NamedTextColor.DARK_RED)
					.append(Component.text("lost all of "+possessive+" hearts "+progressive+" banned for a week.", NamedTextColor.RED)));
			//Bukkit.broadcastMessage("§4"+player.getName()+" §clost all of "+possessive+" hearts "+progressive+" banned for a week.");
			JDA client = DiscordManager.getClient();
	        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle(player.getName()+" lost all of "+possessive+" hearts "+progressive+" banned for a week.");
			builder.setColor(Color.black);
			serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
		} else {
			hearts--;
			if(Main.config.gracePeriod && !inArena) gracePeriodLeft = Math.min(gracePeriodLeft+30*60, Main.config.gracePeriodMax);
			lastTpa=0;
		}
		die();
	}

	public void die() {
		deaths++;
		if(getBukkitPlayer().getBedSpawnLocation()==null) lastRtp = 0;
		if(Main.config.gracePeriod) gracePeriodLeft = Math.max(gracePeriodLeft, 30);
		save();
	}

	public void kill() {
		Player player = getBukkitPlayer();
		kills++;
		if (hearts < 20) {
			hearts++;
			player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2);
			player.setHealth(player.getHealth() + 2);
		}
		save();
	}
	

	public void updateScoreboard() {
		Player p = getBukkitPlayer();
	    if(p.getScoreboard().equals(Main.getInstance().getServer().getScoreboardManager().getMainScoreboard())) p.setScoreboard(Main.getInstance().getServer().getScoreboardManager().getNewScoreboard());
	    Scoreboard board = p.getScoreboard();
		Objective obj = board.getObjective(p.getName()) == null ? board.registerNewObjective(p.getName(), "dummy", "§4§lLifeTheft") : board.getObjective(p.getName());
		if(showWithdraw) {
			//ScoreboardUtils.replaceScore(obj, 9, "§r");
			//ScoreboardUtils.replaceScore(obj, 8, "Withdraws Left : §e" + withdrawsLeft);
		}else {
			//ScoreboardUtils.removeScore(obj, 9);
			//ScoreboardUtils.removeScore(obj, 8);
		}
		ScoreboardUtils.replaceScore(obj, 7, "§r§r");
		if(showGracePeriod) {
			ScoreboardUtils.replaceScore(obj, 6, "Grace Period: " + (isGracePeriodOver() ? "§c§l✘" : "§a"+Main.getTimeLeftSmall(gracePeriodLeft*1000L)));
		}else {
			ScoreboardUtils.removeScore(obj, 6);
		}
		if(showCombat) {
			ScoreboardUtils.replaceScore(obj, 5, "In Combat: "+ (isInCombat() ? "§a" + (Main.config.combatTime - ((System.currentTimeMillis() - getLastHit()) / 1000)) : "§c§l✘"));	
		}else {
			ScoreboardUtils.removeScore(obj, 5);
		}
		if(showTpa) {
			ScoreboardUtils.replaceScore(obj, 4, "Tpa: " + (hasTpa() ? "§a§l✓" : "§c"+Main.getTimeLeftSmall(lastTpa+Main.config.tpaDelay-System.currentTimeMillis())));
		}else {
			ScoreboardUtils.removeScore(obj, 4);
		}
		if(showRtp) {
			ScoreboardUtils.replaceScore(obj, 3, "Rtp: " + (canRtp() ? "§a§l✓" : "§c"+Main.getTimeLeftSmall(lastRtp+Main.config.rtpDelay-System.currentTimeMillis())));
		}else {
			ScoreboardUtils.removeScore(obj, 3);
		}
		if(showDaily) {
			ScoreboardUtils.replaceScore(obj, 2, "Daily: " + (canDaily() ? "§a§l✓" : "§c"+Main.getDelayStringSmall(lastDailyCommand, 24*60*60*1000)));
		}else {
			ScoreboardUtils.removeScore(obj, 2);
		}
		if(showGracePeriod || showCombat || showTpa /*|| showRtp || showDaily*/) {
			ScoreboardUtils.replaceScore(obj, 0, "§r§r§r");
		}else {
			ScoreboardUtils.removeScore(obj, 0);
		}
		if(showKills) {
			ScoreboardUtils.replaceScore(obj, -1, "Kills : §e" + kills);
		}else {
			ScoreboardUtils.removeScore(obj, -1);
		}
		if(showDeaths) {
			ScoreboardUtils.replaceScore(obj, -2, "Deaths : §e" + deaths);
		}else {
			ScoreboardUtils.removeScore(obj, -2);
		}
		if(showKills || showDeaths) {
			ScoreboardUtils.replaceScore(obj, -7, "§r§r§r§r");
		}else {
			ScoreboardUtils.removeScore(obj, -7);
		}
		ScoreboardUtils.replaceScore(obj, -9, "§c"+Main.ip);
		//Score date = obj.getScore(""+ChatColor.GRAY + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		//date.setScore(-8);
		if(scoreboardVisible) {
			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		}else {
			obj.setDisplaySlot(null);
			}
		Objective life = board.getObjective("hearts") == null ? board.registerNewObjective("hearts", "dummy", "hearts") : board.getObjective("hearts");
		//Objective life = board.getObjective(DisplaySlot.PLAYER_LIST) == null ? board.registerNewObjective(p.getName()+"hearts", "dummy", "Hearts", RenderType.INTEGER) : board.getObjective(p.getName()+"hearts");
		life.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		for (LifeStealPlayer pl : Main.getInstance().getOnlinePlayers()) {
			Score vie = life.getScore(pl.getBukkitPlayer().getName());
			vie.setScore(pl.getHearts());
		}
		p.setScoreboard(board);
	}
	
	public void scoreboardManage() {
		ItemStack withdraw = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta metaW = withdraw.getItemMeta();
		metaW.setDisplayName("§cShow Withdraws");
		metaW.setLore(Arrays.asList(showWithdraw ? "§aEnabled" : "§cDisabled"));
		withdraw.setItemMeta(metaW);
		
		ItemStack gracePeriod = new ItemStack(Material.SHIELD);
		ItemMeta metaG = gracePeriod.getItemMeta();
		metaG.setDisplayName("§7Show Grace Period Timer");
		metaG.setLore(Arrays.asList(showGracePeriod ? "§aEnabled" : "§cDisabled"));
		gracePeriod.setItemMeta(metaG);
		
		ItemStack inCombat = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta metaC = inCombat.getItemMeta();
		metaC.setDisplayName("§bShow Combat");
		metaC.setLore(Arrays.asList(showCombat ? "§aEnabled" : "§cDisabled"));
		inCombat.setItemMeta(metaC);
		
		ItemStack tpa = new ItemStack(Material.ENDER_PEARL);
		ItemMeta metaT = tpa.getItemMeta();
		metaT.setDisplayName("§3Show Tpa");
		metaT.setLore(Arrays.asList(showTpa ? "§aEnabled" : "§cDisabled"));
		tpa.setItemMeta(metaT);
		
		ItemStack rtp = new ItemStack(Material.ENDER_EYE);
		ItemMeta metaR = rtp.getItemMeta();
		metaR.setDisplayName("§3Show Rtp");
		metaR.setLore(Arrays.asList(showRtp ? "§aEnabled" : "§cDisabled"));
		rtp.setItemMeta(metaR);
		
		ItemStack daily = new ItemStack(Material.CHEST);
		ItemMeta metaD = daily.getItemMeta();
		metaD.setDisplayName("§6Show Daily");
		metaD.setLore(Arrays.asList(showDaily ? "§aEnabled" : "§cDisabled"));
		daily.setItemMeta(metaD);
		
		ItemStack score = new ItemStack(Material.ITEM_FRAME);
		ItemMeta metaS = score.getItemMeta();
		metaS.setDisplayName("§6Show Scoreboard");
		metaS.setLore(Arrays.asList(scoreboardVisible ? "§aEnabled" : "§cDisabled"));
		score.setItemMeta(metaS);
		
		ItemStack kills = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta metaK = kills.getItemMeta();
		metaK.setDisplayName("§cShow Kills");
		metaK.setLore(Arrays.asList(showKills ? "§aEnabled" : "§cDisabled"));
		kills.setItemMeta(metaK);
		
		ItemStack deaths = new ItemStack(Material.SKELETON_SKULL);
		ItemMeta metaDe = deaths.getItemMeta();
		metaDe.setDisplayName("§7Show Deaths");
		metaDe.setLore(Arrays.asList(showDaily ? "§aEnabled" : "§cDisabled"));
		deaths.setItemMeta(metaDe);
		
		Inventory inv = Bukkit.createInventory(null, 9);
		//inv.addItem(withdraw);
		inv.addItem(gracePeriod);
		inv.addItem(inCombat);
		inv.addItem(tpa);
		inv.addItem(rtp);
		inv.addItem(daily);
		inv.addItem(kills);
		inv.addItem(deaths);
		inv.setItem(inv.getSize()-1, score);
		scoreboardManageInventory = inv;
		getBukkitPlayer().openInventory(inv);
	}
	
	public void scoreboardManageUpdate() {
		scoreboardManageInventory.clear();
		ItemStack withdraw = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta metaW = withdraw.getItemMeta();
		metaW.setDisplayName("§cShow Withdraws");
		metaW.setLore(Arrays.asList(showWithdraw ? "§aEnabled" : "§cDisabled"));
		withdraw.setItemMeta(metaW);
		
		ItemStack gracePeriod = new ItemStack(Material.SHIELD);
		ItemMeta metaG = gracePeriod.getItemMeta();
		metaG.setDisplayName("§7Show Grace Period Timer");
		metaG.setLore(Arrays.asList(showGracePeriod ? "§aEnabled" : "§cDisabled"));
		gracePeriod.setItemMeta(metaG);
		
		ItemStack inCombat = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta metaC = inCombat.getItemMeta();
		metaC.setDisplayName("§bShow Combat");
		metaC.setLore(Arrays.asList(showCombat ? "§aEnabled" : "§cDisabled"));
		inCombat.setItemMeta(metaC);
		
		ItemStack tpa = new ItemStack(Material.ENDER_PEARL);
		ItemMeta metaT = tpa.getItemMeta();
		metaT.setDisplayName("§3Show Tpa");
		metaT.setLore(Arrays.asList(showTpa ? "§aEnabled" : "§cDisabled"));
		tpa.setItemMeta(metaT);
		
		ItemStack rtp = new ItemStack(Material.ENDER_EYE);
		ItemMeta metaR = rtp.getItemMeta();
		metaR.setDisplayName("§3Show Rtp");
		metaR.setLore(Arrays.asList(showRtp ? "§aEnabled" : "§cDisabled"));
		rtp.setItemMeta(metaR);
		
		ItemStack daily = new ItemStack(Material.CHEST);
		ItemMeta metaD = daily.getItemMeta();
		metaD.setDisplayName("§6Show Daily");
		metaD.setLore(Arrays.asList(showDaily ? "§aEnabled" : "§cDisabled"));
		daily.setItemMeta(metaD);
		
		ItemStack score = new ItemStack(Material.ITEM_FRAME);
		ItemMeta metaS = score.getItemMeta();
		metaS.setDisplayName("§6Show Scoreboard");
		metaS.setLore(Arrays.asList(scoreboardVisible ? "§aEnabled" : "§cDisabled"));
		score.setItemMeta(metaS);
		
		ItemStack kills = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta metaK = kills.getItemMeta();
		metaK.setDisplayName("§cShow Kills");
		metaK.setLore(Arrays.asList(showKills ? "§aEnabled" : "§cDisabled"));
		kills.setItemMeta(metaK);
		
		ItemStack deaths = new ItemStack(Material.SKELETON_SKULL);
		ItemMeta metaDe = deaths.getItemMeta();
		metaDe.setDisplayName("§7Show Deaths");
		metaDe.setLore(Arrays.asList(showDeaths ? "§aEnabled" : "§cDisabled"));
		deaths.setItemMeta(metaDe);
		
		//scoreboardManageInventory.addItem(withdraw);
		scoreboardManageInventory.addItem(gracePeriod);
		scoreboardManageInventory.addItem(inCombat);
		scoreboardManageInventory.addItem(tpa);
		scoreboardManageInventory.addItem(rtp);
		scoreboardManageInventory.addItem(daily);
		scoreboardManageInventory.addItem(kills);
		scoreboardManageInventory.addItem(deaths);
		scoreboardManageInventory.setItem(scoreboardManageInventory.getSize()-1, score);
	}
	
	public void endScoreboardCoonfiguration() {
		scoreboardManageInventory = null;
		save();
	}
	
	public void save() {
		new Data(LifeStealPlayer.class).send(uuid.toString(), new Gson().toJson(this));
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getFirstLogin() {
		return firstLogin;
	}

	public String getIdDiscord() {
		return idDiscord;
	}

	public void reset() {
		this.hearts = 10;
		this.storedHearts = 0;
		this.withdrawsLeft = 0;
		this.deaths = 0;
		this.kills = 0;
		this.lastHit = 0;
		this.inCombat = false;
		this.respawnInBed = true;
		this.lastDailyCommand = 0;
		this.lastRtp = 0;
		this.lastRtpCommand = 0;
		this.dailyInventory = null;
		this.hasConnected = false;
		save();
		
	}
	
	

}
