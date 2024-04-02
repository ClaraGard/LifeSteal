package fr.clawara.lifesteal.main;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import fr.clawara.lifesteal.discord.ServerMessages;
import fr.clawara.lifesteal.sanctions.bans.Ban;


public class MainListener extends BukkitRunnable implements Listener {
	
	private Main main;
    private final Set<Entity> trackedEntities = new HashSet<>();
    private final Set<Entity> droppedEggs = new HashSet<>();
	
	public MainListener(Main main) {
		this.main=main;
		//this.runTaskTimer(main, 0, 1);
	}

	@EventHandler
	public void onJoin(PlayerSpawnLocationEvent event) {
		System.out.println("joined");
		LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
		if(player.isNewPlayer()) {
			player.firstConnection(event);
		}
		main.connect(player);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		main.disconnect(LifeStealPlayer.get(event.getPlayer().getUniqueId()));
	}
	@EventHandler 
	public void preLogin(AsyncPlayerPreLoginEvent event) {
		if(!Main.serverOpen) {
			event.disallow(Result.KICK_OTHER, "§cServer is starting please retry in a few seconds.");
			return;
		}
		if(Bukkit.getOfflinePlayer(event.getUniqueId())!=null && Bukkit.getOfflinePlayer(event.getUniqueId()).isOp()) {
			return;
		}
		if(System.currentTimeMillis()<Main.config.serverOpening) {
			String timeLeft = Main.getTimeLeft(Main.config.serverOpening - System.currentTimeMillis());
			event.disallow(Result.KICK_OTHER, "§cThe server is not opened yet, \n§esee you on Discord: §9https://discord.gg/ghhhtEJxhy\n§aIt will open in "+timeLeft+"\n");
			return;
		}
		LifeStealPlayer player = LifeStealPlayer.get(event.getUniqueId());
		if(player==null) {
			event.disallow(Result.KICK_WHITELIST, "§cYou are not whitelisted yet.\n§eTo be whitelisted join this Discord: §9https://discord.gg/ghhhtEJxhy\n§eAnd write your username in the #whitelist channel and then follow the steps.");
		}else {
			for(Ban ban : LifeStealPlayer.get(event.getUniqueId()).getBans()) {
				if(!ban.isExpired()) {
					String message = "§cYou are currently banned for: "+ban.getReason()+"\n§fYou will be unbanned in §9"+Main.getTimeLeft(ban.getTimeLeft());
					event.disallow(Result.KICK_BANNED, message);
				}
			}	
		}
	}
	
	@EventHandler
	public void onChangeWorld(PlayerTeleportEvent event) {
		if(event.getFrom().getWorld().equals(event.getTo().getWorld())) return;
		LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
		if(event.getFrom().getWorld().getName().equals(Main.spawn.getWorld().getName())) {
			player.setLocationInTheWorld(null);
		}else if(event.getTo().getWorld().getName().equals(Main.spawn.getWorld().getName())) {
			player.setLocationInTheWorld(event.getFrom());
		}
	}
	
	@EventHandler
	public void dragonEggEchest(InventoryCloseEvent event) {
		if(event.getView().getTopInventory()!=null && event.getView().getTopInventory().getType()==InventoryType.ENDER_CHEST) {
			Inventory ec = event.getView().getTopInventory();
			if(ec.contains(Material.DRAGON_EGG)) {
				ec.remove(Material.DRAGON_EGG);
				Player player = (Player) event.getPlayer();
				Item dropped = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.DRAGON_EGG));
		        dropped.setVelocity(player.getLocation().getDirection().normalize().multiply(0.35));
		        dropped.setPickupDelay(40);
		        player.sendMessage("§cYou can't put the dragon egg in an ender chest.");
			}
			for(ItemStack shulker : ec.getContents()) {
				if(shulker!=null && shulker.getType().toString().contains("SHULKER_BOX")) {
					BlockStateMeta meta = (BlockStateMeta) shulker.getItemMeta();
		            ShulkerBox box = (ShulkerBox) meta.getBlockState();
		            if(box.getInventory().contains(Material.DRAGON_EGG)) {
						box.getInventory().remove(Material.DRAGON_EGG);
						meta.setBlockState(box);
						shulker.setItemMeta(meta);
						Player player = (Player) event.getPlayer();
						Item dropped = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.DRAGON_EGG));
				        dropped.setVelocity(player.getLocation().getDirection().normalize().multiply(0.35));
				        dropped.setPickupDelay(40);
				        player.sendMessage("§cYou can't put the dragon egg in an ender chest.");
		            }
				}
			}
		}
	}
	
	@EventHandler
	public void dragonEggFellOut(ItemSpawnEvent event) {
		Item item = (Item) event.getEntity();
		if(item.getItemStack().getType()==Material.DRAGON_EGG) {
			item.getScheduler().runAtFixedRate(main, null, null, getTaskId(), getTaskId());
			
		}  
	}
	
	@EventHandler
	public void fallingBlock(EntitySpawnEvent event) {
		if(event.getEntityType()==EntityType.FALLING_BLOCK) {
			FallingBlock fb = (FallingBlock) event.getEntity();
			if(fb.getBlockData().getMaterial()==Material.DRAGON_EGG) {
				droppedEggs.add(fb);
			}
		}
	}
	
    @EventHandler
    public void dragonEggDies(EntityDamageEvent event) {
    	if(event.getEntityType()==EntityType.DROPPED_ITEM) {
			Item item = (Item) event.getEntity();
			if(item.getItemStack().getType()==Material.DRAGON_EGG) {
				if(trackedEntities.contains(event.getEntity())) {
	                return;
				}
	            trackedEntities.add(event.getEntity());
	            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
	                if(event.getEntity().isDead()) {
	        			Main.config.dragonEggDestroyed();
	                }
	                trackedEntities.remove(event.getEntity());
	            });
			}
    	}
    }
    
	@EventHandler
	public void dragonEggDespawn(ItemDespawnEvent event) {
		if(event.getEntity().getItemStack().getType()==Material.DRAGON_EGG) {
			Main.config.dragonEggDestroyed();
		}
	}
	
	@EventHandler
	public void dragonDies(EntityDeathEvent event){
	     if(event.getEntityType()==EntityType.ENDER_DRAGON){
    		 ServerMessages.dragonDeathMessage();
	    	 if(Main.config.forceDragonEggRespawn) {
	    		 Bukkit.getWorld("world_the_end").getBlockAt(0,100,0).setType(Material.DRAGON_EGG);
	    		 Main.config.dragonDied();
	    	 }
	     }
	}
	
	
	
	@EventHandler
	public void dailyInventory(InventoryClickEvent event) {
		if(event.getClickedInventory()==null) return;
		LifeStealPlayer player = LifeStealPlayer.get(event.getWhoClicked().getUniqueId());
		if(event.getClickedInventory().equals(player.getDailyInventory())) {
			event.setCancelled(true);
			if(event.getCurrentItem()!=null) {
				if(player.getHearts()>=Main.config.maxHeartsDailyHeart && Main.isHeart(event.getCurrentItem())) {
					player.getBukkitPlayer().sendMessage("§cYou can't chose the heart if you already have more than "+Main.config.maxHeartsDailyHeart+".");
					return;
				}
				if(player.getBukkitPlayer().getHealth()<2 && Main.isWithdrawItem(event.getCurrentItem())) {
					player.getBukkitPlayer().sendMessage("§cYou can't chose the withdraw if you have only 1 heart.");
					return;
				}
				player.getDaily(event.getCurrentItem());
				player.getBukkitPlayer().closeInventory();
			}
		}
	}
	
	@EventHandler
	public void pillagerCaptainSpawn(CreatureSpawnEvent event) {
		if(event.getEntity() instanceof Raider) {
			Raider raider = (Raider) event.getEntity();
			if(event.getSpawnReason()==SpawnReason.RAID && raider.isPatrolLeader()) {
				raider.setPatrolLeader(false);
			}
		}
	}
	
	@EventHandler
	public void pressurePlateSpawn(PlayerMoveEvent event) {
		if(event.getTo().getWorld().getName().equals(Main.spawn.getWorld().getName())) {
			if(event.getTo().getBlockX() == -295 && event.getTo().getBlockY() == 136 && event.getTo().getBlockZ() == 296) {
				event.getPlayer().getScheduler().execute(main, () -> event.getPlayer().teleportAsync(LifeStealPlayer.get(event.getPlayer().getUniqueId()).getLocationInTheWorld()), null, 1);
			}
		}
	}
	
	@EventHandler
	public void scoreboardInventory(InventoryClickEvent event) {
		if(event.getClickedInventory()==null) return;
		LifeStealPlayer player = LifeStealPlayer.get(event.getWhoClicked().getUniqueId());
		if(event.getClickedInventory().equals(player.getScoreboardInventory())) {
			event.setCancelled(true);
			if(event.getCurrentItem()!=null) {
				if(event.getCurrentItem().getType()==Material.COMMAND_BLOCK) {
					player.showWithdraw=!player.showWithdraw;
				}else if(event.getCurrentItem().getType()==Material.SHIELD) {
					player.showGracePeriod=!player.showGracePeriod;
				}else if(event.getCurrentItem().getType()==Material.DIAMOND_SWORD) {
					player.showCombat=!player.showCombat;
				}else if(event.getCurrentItem().getType()==Material.ENDER_PEARL) {
					player.showTpa=!player.showTpa;
				}else if(event.getCurrentItem().getType()==Material.ENDER_EYE) {
					player.showRtp=!player.showRtp;
				}else if(event.getCurrentItem().getType()==Material.CHEST) {
					player.showDaily=!player.showDaily;
				}else if(event.getCurrentItem().getType()==Material.NETHERITE_SWORD) {
					player.showKills=!player.showKills;
				}else if(event.getCurrentItem().getType()==Material.SKELETON_SKULL) {
					player.showDeaths=!player.showDeaths;
				}else if(event.getCurrentItem().getType()==Material.ITEM_FRAME) {
					player.scoreboardVisible=!player.scoreboardVisible;
				}
				player.scoreboardManageUpdate();
			}
		}
	}
	
	@EventHandler
	public void inventoryCloseScoreboard(InventoryCloseEvent event) {
		LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
		if(event.getInventory().equals(player.getScoreboardInventory())) {
			player.endScoreboardCoonfiguration();
		}
	}

	@Override
	public void run() {
		Iterator<Entity> it = droppedEggs.iterator();
		while(it.hasNext()) {
			Entity e = it.next();
			if(e.isDead()) droppedEggs.remove(e);
			if(e.getLocation().getY()<-64) {
				Main.config.dragonEggDestroyed();
				e.remove();
				droppedEggs.remove(e);
			}
		}
	}
}
