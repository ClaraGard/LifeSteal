package fr.clawara.lifesteal.combatLog;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class CombatTagListeners extends BukkitRunnable implements Listener {
	
	private Main main;
	
	public CombatTagListeners(Main main) {
		this.main=main;
		Bukkit.getGlobalRegionScheduler().runAtFixedRate(main, t -> this.run(), 1, 20);	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.isCancelled() && !event.getEntity().getLocation().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
		if(!(event.getEntity() instanceof Player)) return;
		Player bukkitKilled = (Player) event.getEntity();
		Player bukkitKiller = null;
		if(event.getDamager() instanceof Player) {
			bukkitKiller = (Player) event.getDamager();
		}else if(event.getDamager() instanceof Arrow && ((Arrow)event.getDamager()).getShooter() instanceof Player) {
			bukkitKiller = (Player) ((Arrow)event.getDamager()).getShooter();
		}
		if(bukkitKiller==null) return;
		LifeStealPlayer victim = LifeStealPlayer.get(bukkitKilled.getUniqueId());
		LifeStealPlayer damager = LifeStealPlayer.get(bukkitKiller.getUniqueId());
		if(victim.equals(damager)) return;
		if(bukkitKilled.getLocation().getWorld().getName().equals(Main.spawn.getWorld().getName()) && bukkitKiller.getLocation().getWorld().getName().equals(Main.spawn.getWorld().getName())) {
			if(!Main.isInTheArena(bukkitKilled.getLocation()) || !Main.isInTheArena(bukkitKiller.getLocation())) {
				event.setCancelled(true);
				return;
			}else {
				event.setCancelled(false);
			}
		}
		damager.hit();
		victim.hit();
		System.out.println(damager.getBukkitPlayer().getName()+" hit "+victim.getBukkitPlayer().getName());
	}
	
	@EventHandler
	public void onDisconnect(PlayerQuitEvent event) {
		LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
		if(player.isInCombat()) {
			player.getBukkitPlayer().setHealth(0);
			Bukkit.broadcastMessage(player.getBukkitPlayer().getName()+" disconnected in combat and got punished.");
		}
	}
	
	@EventHandler
	public void onElytraBoost(PlayerInteractEvent event) {
		if(event.getAction()==Action.RIGHT_CLICK_AIR && event.getItem()!=null && event.getItem().getType()==Material.FIREWORK_ROCKET) {
			LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
			if(player.isInCombat() && player.getBukkitPlayer().getInventory().getChestplate()!=null && player.getBukkitPlayer().getInventory().getChestplate().getType()==Material.ELYTRA) {
				if(!Main.config.elytraBoostInCombat) {
					event.setCancelled(true);
					player.getBukkitPlayer().sendMessage("§cYou can't firework boost while in combat.");	
				}
			}	
		}
	}

	public void run() {
		for(LifeStealPlayer p : main.getOnlinePlayers()) {
			if(p.isInCombat() && p.getLastHit()+Main.config.combatTime*1000<System.currentTimeMillis()) {
				p.combat(false);
			}
		}
	}
}
