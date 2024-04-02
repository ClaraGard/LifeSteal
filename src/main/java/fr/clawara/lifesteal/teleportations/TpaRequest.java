package fr.clawara.lifesteal.teleportations;

import org.bukkit.scheduler.BukkitRunnable;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class TpaRequest extends BukkitRunnable {
	
	private LifeStealPlayer asker;
	private LifeStealPlayer target;
	private int timeLeft;
	
	public TpaRequest(LifeStealPlayer asker, LifeStealPlayer target) {
		this.target = target;
		this.asker = asker;
		timeLeft = 60;
		runTaskTimer(Main.getInstance(), 0, 20);
		sendMessages();
	}
	
	public int getTimeLeft() {
		return timeLeft;
	}
	
	public void accept() {
		new TeleportRequest(asker, target.getBukkitPlayer().getLocation(), TeleportType.TPA);
		asker.getBukkitPlayer().sendMessage("§e"+target.getBukkitPlayer().getName()+" §7accepted your teleport request you will be teleported in 5 seconds.");
		target.getBukkitPlayer().sendMessage("§aTeleport request accepted.");
		TpaCommand.cancelRequest(this);
	}
	
	public void deny() {
		asker.getBukkitPlayer().sendMessage("§e"+target.getBukkitPlayer().getName()+" §7denied your teleport request.");
		target.getBukkitPlayer().sendMessage("§aTeleport request denied.");
		TpaCommand.cancelRequest(this);
	}
	
	public void sendMessages() {
		asker.getBukkitPlayer().sendMessage("§eTeleport request sent successfuly to §b"+target.getBukkitPlayer().getName()+".");
		target.getBukkitPlayer().sendMessage("§b"+asker.getBukkitPlayer().getName()+" §ewould like to teleport to you, use /tpaccept "+asker.getBukkitPlayer().getName()+" to accept or /tpdeny "+asker.getBukkitPlayer().getName()+" to deny it.");
	}

	@Override
	public void run() {
		
		timeLeft--;
		if(timeLeft==0) {
			TpaCommand.cancelRequest(this);
			cancel();
		}
		
	}
	
	public void resetTimeLeft() {
		timeLeft = 60;
	}

	public LifeStealPlayer getTarget() {
		return target;
	}

	public LifeStealPlayer getAsker() {
		return asker;
	}

}
