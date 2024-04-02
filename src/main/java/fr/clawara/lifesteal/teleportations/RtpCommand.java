package fr.clawara.lifesteal.teleportations;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class RtpCommand implements CommandExecutor {
	

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if(player.isInCombat()) {
			player.getBukkitPlayer().sendMessage("§cYou cannot use teleportations commands while in combat");
			return true;
		}
		Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
			player.rtp();
		});
		return true;
	}

}
