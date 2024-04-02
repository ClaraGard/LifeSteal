package fr.clawara.lifesteal.lifesteal;


import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class LifeStealListener implements Listener {
	
	private Main main;
	
	public LifeStealListener(Main main) {
		this.main=main;
	}

	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		Player bukkitKilled = event.getEntity();
		Player bukkitKiller = event.getEntity().getKiller();
		LifeStealPlayer killed = LifeStealPlayer.get(bukkitKilled.getUniqueId());
		if(bukkitKiller!=null) {
			LifeStealPlayer killer = LifeStealPlayer.get(bukkitKiller.getUniqueId());
			if(!killer.equals(killed)) {
				if(main.getPlayersFromIp(killer.getBukkitPlayer().getAddress().toString()).contains(killed)) {
					killer.getBukkitPlayer().sendMessage("§cYou don't get hearts from killing someone with your IP.");
					return;
				}
				killer.kill();
				killed.killed(bukkitKilled.getWorld().getName().equals(Main.spawn.getWorld().getName()));
				return;
			}
		}else if(killed.isInCombat()) {
			killed.killed(bukkitKilled.getWorld().getName().equals(Main.spawn.getWorld().getName()));
			killed.getBukkitPlayer().getWorld().dropItemNaturally(killed.getBukkitPlayer().getLocation(), Main.heart);
			return;
		}
		killed.die();
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
		player.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(player.getHearts()*2);
		if(!player.doesRespawnInBed()) {
			event.setRespawnLocation(Main.spawn);
			if(player.getBukkitPlayer().getBedSpawnLocation()==null) {
				player.setLocationInTheWorld(Main.getValidSpawnAround(Main.worldSpawn));
			}else {
				player.setLocationInTheWorld(player.getBukkitPlayer().getBedSpawnLocation());
			}
			
		}
	}
	
	@EventHandler
	public void useHeart(PlayerInteractEvent event) {
		if(event.getAction()==Action.RIGHT_CLICK_AIR || event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			if(Main.isHeart(event.getItem())) {
				LifeStealPlayer.get(event.getPlayer().getUniqueId()).useHeart();
			}	
		}
	}
}
