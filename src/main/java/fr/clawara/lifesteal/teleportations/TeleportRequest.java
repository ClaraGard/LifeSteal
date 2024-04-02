package fr.clawara.lifesteal.teleportations;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class TeleportRequest extends BukkitRunnable implements Listener {
	
	private LifeStealPlayer player;
	private Location location;
	private Location lastLoc;
	private int timeleft;
	private TeleportType type;
	
	public TeleportRequest(LifeStealPlayer player, Location location, TeleportType type) {
		this.player = player;
		this.location = location;
		this.lastLoc = player.getBukkitPlayer().getLocation();
		this.timeleft = 5;
		this.type = type;
		if(type!=TeleportType.TPA) {
			player.getBukkitPlayer().sendMessage("§7Teleportation will start in 5 seconds...");
		}
		if(player.isInCombat()) {
			player.getBukkitPlayer().sendMessage("§cYou cannot teleport while in combat.");
			cancelTeleport();
		}else {
			runTaskTimer(Main.getInstance(), 0, 20);
		}
		TeleportManager.addNewRequest(this);
	}
	
	public LifeStealPlayer getPlayer() {
		return player;
	}
	
	public void cancelTeleport() {
		cancel();
	}

	@Override
	public void run() {
		if(player.isInCombat()) {
			player.getBukkitPlayer().sendMessage("§cYou moved, the teleportation has been canceled.");
			cancelTeleport();
		}
		if(player.getBukkitPlayer().getLocation().getBlock().equals(lastLoc.getBlock())) {
			timeleft--;
		}else {
			player.getBukkitPlayer().sendMessage("§cYou moved, the teleportation has been canceled.");
			cancelTeleport();
		}
		if(timeleft==0) {
			player.getBukkitPlayer().teleport(location);
			if(type==TeleportType.TPA) {
				player.useTpa();
			}else if(type==TeleportType.RTP) {
				player.useRtp();
			}
			cancel();
		}
	}
	
	

}
