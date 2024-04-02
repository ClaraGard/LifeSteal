package fr.clawara.lifesteal.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

import fr.clawara.lifesteal.combatLog.CombatTagCommand;
import fr.clawara.lifesteal.combatLog.CombatTagListeners;
import fr.clawara.lifesteal.discord.DiscordManager;
import fr.clawara.lifesteal.discord.SendMessageDiscord;
import fr.clawara.lifesteal.discord.SendMessageToDiscordMembers;
import fr.clawara.lifesteal.discord.SendMessageToSeasonMembersCommand;
import fr.clawara.lifesteal.discord.ServerMessages;
import fr.clawara.lifesteal.gracePeriod.GracePeriodCommand;
import fr.clawara.lifesteal.gracePeriod.GracePeriodListener;
import fr.clawara.lifesteal.lifesteal.LifeStealListener;
import fr.clawara.lifesteal.lifesteal.ResetHeartsCommand;
import fr.clawara.lifesteal.lifesteal.SetHeartsCommand;
import fr.clawara.lifesteal.respawn.RespawnCommand;
import fr.clawara.lifesteal.sanctions.ModCommand;
import fr.clawara.lifesteal.sanctions.bans.BanCommand;
import fr.clawara.lifesteal.sanctions.bans.UnbanCommand;
import fr.clawara.lifesteal.sanctions.mutes.MuteCommand;
import fr.clawara.lifesteal.sanctions.mutes.UnmuteCommand;
import fr.clawara.lifesteal.scoreboard.LifeStealScoreboard;
import fr.clawara.lifesteal.scoreboard.ScoreboardCommand;
import fr.clawara.lifesteal.scoreboard.ScoreboardManageCommand;
import fr.clawara.lifesteal.spawn.SpawnProtection;
import fr.clawara.lifesteal.spawn.Arena.ResetArenaCommand;
import fr.clawara.lifesteal.spawn.Arena.ResetArenaTask;
import fr.clawara.lifesteal.spawn.Arena.SetArenaStructureCommand;
import fr.clawara.lifesteal.teleportations.BedCommand;
import fr.clawara.lifesteal.teleportations.GotolifestealCommand;
import fr.clawara.lifesteal.teleportations.RtpCommand;
import fr.clawara.lifesteal.teleportations.SpawnCommand;
import fr.clawara.lifesteal.teleportations.TpaCommand;
import fr.clawara.lifesteal.teleportations.TpacceptCommand;
import fr.clawara.lifesteal.teleportations.TpdenyCommand;
import net.kyori.adventure.text.Component;


public class Main extends JavaPlugin {
	
	private static Main INSTANCE;
	public static String ip = "mc.lifetheft.net";
	public static ItemStack heart;
	public static ItemStack crateKey;
	public static ItemStack randomItem;
	public static ItemStack tpaPaper;
	public static ItemStack withdrawItem;
	public static Location spawn;
	public static Location worldSpawn;
	public static Location crateLocation;
	public static List<ItemStack> crateItems;
	public static Random random = new Random();
	public static boolean serverOpen = false;
	
	private static File arenaFile;
	
	//Config
	public static Config config;
	
	private static List<Material> badBlocks = new ArrayList<>();

	
	public static Main getInstance() {
		return INSTANCE;
	}
		
	private HashMap<UUID, LifeStealPlayer> onlinePlayers = new HashMap<UUID, LifeStealPlayer>();
	private HashMap<String, List<LifeStealPlayer>> playersByIp = new HashMap<String, List<LifeStealPlayer>>();
	
	public HashMap<UUID, LifeStealPlayer> getLifeStealMap() {
		return onlinePlayers;
	}
	
	public Collection<LifeStealPlayer> getOnlinePlayers() {
		return onlinePlayers.values();
	}
	
	public List<LifeStealPlayer> getPlayersFromIp(String adress) {
		return playersByIp.get(adress);
	}
	
	public void connect(LifeStealPlayer player) {
		player.setUsername(player.getBukkitPlayer().getName());
		player.setAddress(player.getBukkitPlayer().getAddress().toString());
		player.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getHearts()*2);
		onlinePlayers.put(player.getUniqueId(), player);
		playersByIp.putIfAbsent(player.getBukkitPlayer().getAddress().toString(), new ArrayList<>());
		playersByIp.get(player.getBukkitPlayer().getAddress().toString()).add(player);
	}
	
	public void disconnect(LifeStealPlayer player) {
		onlinePlayers.remove(player.getUniqueId());
		playersByIp.get(player.getAddress()).remove(player);
		player.save();
	}
	
	public static boolean isHeart(ItemStack it) {
		return it!=null && it.getType()==heart.getType() && it.hasItemMeta() && it.getItemMeta().hasEnchant(Enchantment.WATER_WORKER) && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(heart.getItemMeta().getDisplayName());
	}
	
	public static boolean isCrateKey(ItemStack it) {
		return it!=null && it.getType()==crateKey.getType()&& it.hasItemMeta() && it.getItemMeta().hasEnchant(Enchantment.WATER_WORKER) && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(crateKey.getItemMeta().getDisplayName());
	}
	
	public static boolean isTpaPaper(ItemStack it) {
		return it!=null && it.getType()==tpaPaper.getType()&& it.hasItemMeta() && it.getItemMeta().hasEnchant(Enchantment.WATER_WORKER) && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(tpaPaper.getItemMeta().getDisplayName());
	}
	
	public static boolean isWithdrawItem(ItemStack it) {
		return it!=null && it.getType()==withdrawItem.getType()&& it.hasItemMeta() && it.getItemMeta().hasEnchant(Enchantment.WATER_WORKER) && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals(withdrawItem.getItemMeta().getDisplayName());
	}
	
	public static String getDelayString(long lastUse, long delay) {
		long timeLeftToWait = (lastUse+delay)-System.currentTimeMillis();
		timeLeftToWait = timeLeftToWait / 1000;
		long hours = timeLeftToWait / (60 * 60);
		long minutes = timeLeftToWait / (60) - hours * 60;
		long seconds = timeLeftToWait - minutes * 60 - hours * 60 * 60;
		return hours + " hours " + minutes + " minutes and " + seconds + " seconds";
	}
	
	public static String getDelayStringSmall(long lastUse, long delay) {
		long timeLeftToWait = (lastUse+delay)-System.currentTimeMillis();
		timeLeftToWait = timeLeftToWait / 1000;
		long hours = timeLeftToWait / (60 * 60);
		long minutes = timeLeftToWait / (60) - hours * 60;
		long seconds = timeLeftToWait - minutes * 60 - hours * 60 * 60;
		return (hours!=0 ? hours+":" : "00:") + (hours!=0 || minutes!=0 ? (minutes<10 ? "0"+minutes : minutes) +":" : "00:") + (seconds<10 ? "0"+seconds : seconds);
	}
	
	public static String getTimeLeft(long timeLeft) {
		timeLeft = timeLeft / 1000;
		long days = timeLeft / (60*60*24);
		long hours = timeLeft / (60 * 60) - days*24;
		long minutes = timeLeft / (60) - hours * 60 - days*24*60;
		long seconds = timeLeft - minutes * 60 - hours * 60 * 60 - days*24*60*60;
		return (days!=0 ? days + " days " : "") + hours + " hours " + minutes + " minutes and " + seconds + " seconds";
	}
	
	public static String getTimeLeftSmall(long timeLeft) {
		timeLeft = timeLeft / 1000;
		long hours = timeLeft / (60 * 60);
		long minutes = timeLeft / (60) - hours * 60;
		long seconds = timeLeft - minutes * 60 - hours * 60 * 60;
		return (hours!=0 ? hours+":" : "00:") + (hours!=0 || minutes!=0 ? (minutes<10 ? "0"+minutes : minutes) +":" : "00:") + (seconds<10 ? "0"+seconds : seconds);
	}
	
	public static ItemStack getRandomItem() {
		ItemStack randomItem = Main.crateItems.get(random.nextInt(Main.crateItems.size()));
		return new ItemStack(randomItem.getType(), randomItem.getAmount());
	}
	
	public static String stringFromLocation(Location loc) {
		if(loc==null) return null;
		return loc.getWorld().getName()+"%"+loc.getX()+"%"+loc.getY()+"%"+loc.getZ()+"%"+loc.getYaw()+"%"+loc.getPitch();
	}
	public static Location locationFromString(String s) {
		if(s==null) return null;
		String[] s2 = s.split("%");
		return new Location(Bukkit.getWorld(s2[0]), Double.valueOf(s2[1]),Double.valueOf(s2[2]), Double.valueOf(s2[3]),Float.valueOf(s2[4]),Float.valueOf(s2[5]));
	}
	
	public static Location getValidSpawnAround(Location loc) {
		Location spawnLoc = loc.clone();
		Bukkit.getRegionScheduler().execute(Main.INSTANCE, spawnLoc, () -> {
			while(!spawnLoc.getBlock().getType().isSolid()) {
				spawnLoc.add(random.nextInt(22)-11, 10, random.nextInt(22)-11);
				spawnLoc.setY(spawnLoc.getWorld().getHighestBlockAt(spawnLoc).getY());
			}
			spawnLoc.add(0.5, 1.5, 0.5);
		});
		return spawnLoc;
	}
	
	public static boolean isSafe(Location loc) {
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		return !(badBlocks.contains(loc.getBlock().getType()) || badBlocks.contains(loc.getWorld().getBlockAt(x,y+1,z).getType()));
	}
	
	public ItemStack getItem(Material material, String name, int amount) {
		ItemStack it = new ItemStack(material, amount);
		ItemMeta meta = it.getItemMeta();
		meta.setDisplayName(name);
		it.setItemMeta(meta);
		return it;
	}
	
	
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		
		arenaFile = new File("arenaFile");
		if(!arenaFile.exists()) {
			try {
				arenaFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

		config = Config.load();
		if(config == null) {
			config = new Config();
			config.save();
		}
        World spawnWorld = Bukkit.getWorld("world_spawn_spawn");
        spawn = spawnWorld.getSpawnLocation();
        spawn.add(0.5, 0.5, 0.5);
        spawn.setYaw(180);
        spawn.setPitch(0);
        worldSpawn = Bukkit.getWorld("world").getSpawnLocation();
        
        badBlocks.add(Material.LAVA);
        badBlocks.add(Material.WATER);
        badBlocks.add(Material.FIRE);
        
        crateLocation = spawnWorld.getBlockAt(0, 0, 0).getLocation();
        
		crateItems = new ArrayList<>();
		crateItems.add(getItem(Material.NETHERITE_SCRAP, "§6Netherite Scrap",1));
		crateItems.add(getItem(Material.DIAMOND, "§bDiamond",3));
		crateItems.add(getItem(Material.ENCHANTED_BOOK, "§eMax Leveled Random Enchant", 1));
		//crateItems.add(new ItemStack(Material.END_CRYSTAL, 2));
		crateItems.add(getItem(Material.EMERALD, "§aEmerald",10));
		crateItems.add(getItem(Material.GOLD_INGOT, "§eGold Ingot",16));
		crateItems.add(getItem(Material.IRON_INGOT, "§7Iron Ingot",20));
		//crateItems.add(getItem(Material.GOLDEN_APPLE, "§eGolden Apple",4));
		crateItems.add(getItem(Material.OBSIDIAN, "§5Obsidian",32));
		crateItems.add(getItem(Material.EXPERIENCE_BOTTLE, "§eXP Bottle",32));
		
		heart = new ItemStack(Material.COMMAND_BLOCK);
		ItemMeta meta = heart.getItemMeta();
		meta.addEnchant(Enchantment.WATER_WORKER, 1, false);
		meta.setDisplayName("§cHeart");
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.setLore(Arrays.asList("§7Right click to use"));
		heart.setItemMeta(meta);
		
		crateKey = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta meta2 = crateKey.getItemMeta();
		meta2.addEnchant(Enchantment.WATER_WORKER, 1, false);
		meta2.setDisplayName("§eCrate Key");
		meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta2.setLore(Arrays.asList("§7Use at the crate at spawn"));
		crateKey.setItemMeta(meta2);
		
		randomItem = new ItemStack(Material.DISPENSER);
		ItemMeta meta5 = randomItem.getItemMeta();
		meta5.addEnchant(Enchantment.WATER_WORKER, 1, false);
		meta5.setDisplayName("§eRandom Item");
		meta5.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> lore = new ArrayList<>();
		lore.add("§7Items can be:");
		for(ItemStack it : crateItems) {
			lore.add(it.getItemMeta().getDisplayName().substring(0,2)+it.getAmount()+" " + it.getItemMeta().getDisplayName());
		}
		meta5.setLore(lore);
		randomItem.setItemMeta(meta5);
				
		tpaPaper = new ItemStack(Material.PAPER);
		ItemMeta meta3 = tpaPaper.getItemMeta();
		meta3.addEnchant(Enchantment.WATER_WORKER, 1, false);
		meta3.setDisplayName("§e2 days of /tpa");
		meta3.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta3.setLore(Arrays.asList("§7Right click to use"));
		tpaPaper.setItemMeta(meta3);
		
		withdrawItem = heart.clone();
		ItemMeta meta4 = withdrawItem.getItemMeta();
		meta4.setLore(null);
		meta4.setDisplayName("§cOne Withdraw");
		withdrawItem.setItemMeta(meta4);
		
		ShapedRecipe heartRecipe = new ShapedRecipe(new NamespacedKey(this,"Heart"),heart);
		heartRecipe.shape("NDN","HSK","NDN");
		heartRecipe.setIngredient('N', Material.NETHERITE_INGOT);
		heartRecipe.setIngredient('H', Material.DRAGON_HEAD);
		heartRecipe.setIngredient('K', Material.SKELETON_SKULL);
		heartRecipe.setIngredient('S', Material.NETHER_STAR);
		heartRecipe.setIngredient('D', Material.DIAMOND_BLOCK);
		ShapedRecipe heartRecipeOld = new ShapedRecipe(new NamespacedKey(this,"Heart"),heart);
		heartRecipeOld.shape("GTG","CSC","GTG");
		heartRecipeOld.setIngredient('G', Material.GOLD_BLOCK);
		heartRecipeOld.setIngredient('T', Material.TOTEM_OF_UNDYING);
		heartRecipeOld.setIngredient('C', Material.END_CRYSTAL);
		heartRecipeOld.setIngredient('S', Material.DRAGON_HEAD);
		Bukkit.getServer().addRecipe(heartRecipe);
		
		Bukkit.getPluginManager().registerEvents(new MainListener(this), this);
		Bukkit.getPluginManager().registerEvents(new LifeStealListener(this), this);
		Bukkit.getPluginManager().registerEvents(new SpawnProtection(), this);
		Bukkit.getPluginManager().registerEvents(new GracePeriodListener(this), this);
		Bukkit.getPluginManager().registerEvents(new CombatTagListeners(this), this);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new ServerMessages(), this);
		getServer().getPluginCommand("gotolifesteal").setExecutor(new GotolifestealCommand());
		getServer().getPluginCommand("scoreboardmanager").setExecutor(new ScoreboardManageCommand());
		getServer().getPluginCommand("combattag").setExecutor(new CombatTagCommand());
		//getServer().getPluginCommand("withdraw").setExecutor(new WithdrawCommand());
		getServer().getPluginCommand("bed").setExecutor(new BedCommand());
		getServer().getPluginCommand("spawn").setExecutor(new SpawnCommand());
		getServer().getPluginCommand("tpa").setExecutor(new TpaCommand());
		getServer().getPluginCommand("tpaccept").setExecutor(new TpacceptCommand());
		getServer().getPluginCommand("tpdeny").setExecutor(new TpdenyCommand());
		getServer().getPluginCommand("rtp").setExecutor(new RtpCommand());
		getServer().getPluginCommand("daily").setExecutor(new DailyCommand());
		getServer().getPluginCommand("resethearts").setExecutor(new ResetHeartsCommand());
		getServer().getPluginCommand("sethearts").setExecutor(new SetHeartsCommand());
		getServer().getPluginCommand("unban").setExecutor(new UnbanCommand());
		getServer().getPluginCommand("ban").setExecutor(new BanCommand());
		getServer().getPluginCommand("mute").setExecutor(new MuteCommand());
		getServer().getPluginCommand("unmute").setExecutor(new UnmuteCommand());
		getServer().getPluginCommand("mod").setExecutor(new ModCommand());
		getServer().getPluginCommand("sendmessagediscord").setExecutor(new SendMessageDiscord());
		getServer().getPluginCommand("sendmessagetodiscordmembers").setExecutor(new SendMessageToDiscordMembers());
		getServer().getPluginCommand("sendmessagetoseasonmembers").setExecutor(new SendMessageToSeasonMembersCommand());
		getServer().getPluginCommand("endseason").setExecutor(new EndSeasonCommand());
		getServer().getPluginCommand("resetlifestealdata").setExecutor(new ResetLifeStealDataCommand());
		getServer().getPluginCommand("setarenastructure").setExecutor(new SetArenaStructureCommand());
		getServer().getPluginCommand("resetarena").setExecutor(new ResetArenaCommand());
		ScoreboardCommand sbExecutor = new ScoreboardCommand();
		getCommand("scoreboardvisibility").setTabCompleter(sbExecutor);
		getCommand("scoreboardvisibility").setExecutor(sbExecutor);
		RespawnCommand respawnExecutor = new RespawnCommand();
		getCommand("respawn").setTabCompleter(respawnExecutor);
		getCommand("respawn").setExecutor(respawnExecutor);
		GracePeriodCommand gpExecutor = new GracePeriodCommand();
		getCommand("graceperiod").setTabCompleter(gpExecutor);
		getCommand("graceperiod").setExecutor(gpExecutor);
		Bukkit.getRegionScheduler().runAtFixedRate(this, 
				new Location(spawn.getWorld(), -280, 112, 371), t -> new ResetArenaTask().run(), 1, 1200);
		//new LifeStealScoreboard(this).runTaskTimer(this, 0, 20);
		//new ResetArenaTask().runTaskTimer(this, 1200, 1200);
		
		Bukkit.getGlobalRegionScheduler().runDelayed(this, t -> {
			DiscordManager.initDiscord();
		}, 20);
		Bukkit.getGlobalRegionScheduler().runDelayed(this, t -> {
			DiscordManager.initStatChannels();
			serverOpen = true;
		}, 100);
		Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, t -> {
			DiscordManager.updatePlayerCountChannel();
			DiscordManager.updateWhitelistCountChannel();
		}, 3000, 12000);
		
	}
	
	public static boolean isInTheArena(Location loc) {
		if(loc.getWorld().getName().equals(spawn.getWorld().getName())) {
			if(loc.getBlockX()>-280 && loc.getY()>112 && loc.getBlockZ()>371 && loc.getBlockX()<-233 && loc.getY()<128 && loc.getBlockZ()<418) {
				return true;
			}
		}
		return false;
	}
	
	public static void resetArena() {
		try {
			Structure arena = Bukkit.getStructureManager().loadStructure(arenaFile);
			arena.place(new Location(spawn.getWorld(), -280, 112, 371), false, StructureRotation.NONE, Mirror.NONE, 0, 1, random);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void setArenaStructure() {
		Structure arena = Bukkit.getStructureManager().createStructure();
		arena.fill(new Location(spawn.getWorld(), -280, 112, 371), new Location(spawn.getWorld(), -233, 128, 418), false);
		try {
			Bukkit.getStructureManager().saveStructure(arenaFile, arena);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onDisable() {
		Iterator<? extends Player> it = Bukkit.getOnlinePlayers().iterator();
		while(it.hasNext()) {
			Player player = it.next();
			player.kick(Component.text("Server Closed"));
		}
		DiscordManager.clearStatChannels();
		DiscordManager.getClient().shutdown();
		
		
	}
}
