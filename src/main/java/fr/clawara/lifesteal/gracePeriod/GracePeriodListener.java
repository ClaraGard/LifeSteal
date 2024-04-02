package fr.clawara.lifesteal.gracePeriod;


import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class GracePeriodListener extends BukkitRunnable implements Listener {
	
	private Main main;
	
	public GracePeriodListener(Main main) {
		this.main=main;
		Bukkit.getGlobalRegionScheduler().runAtFixedRate(main, t -> this.run(), 1, 20);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageByEntityEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		if(event.isCancelled()) return;
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
		if(!damager.isGracePeriodOver()) {
			bukkitKiller.sendMessage("§cYou can't PvP when you are under grace period. You can do /gp remove to remove your grace period.");
			event.setCancelled(true);
			return;
		}
		if(!victim.isGracePeriodOver()) {
			bukkitKiller.sendMessage("§c"+bukkitKilled.getName()+" is under grace period.");
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onDamage(EntityDamageEvent event) {
		if(!(event.getEntity() instanceof Player)) return;
		Player bukkitKilled = (Player) event.getEntity();
		LifeStealPlayer victim = LifeStealPlayer.get(bukkitKilled.getUniqueId());
		if(victim.isGracePeriodOver()) {
			return;
		}
		if(event.getCause()==DamageCause.ENTITY_EXPLOSION || event.getCause()==DamageCause.BLOCK_EXPLOSION) {
			event.setCancelled(true);
		}
	}

	public void run() {
		for(LifeStealPlayer player : main.getOnlinePlayers()) {
			if(!player.isGracePeriodOver()) {
				player.gracePeriodLeft--;
				if(player.gracePeriodLeft==0) player.endGracePeriod();
			}
		}
	}
}
