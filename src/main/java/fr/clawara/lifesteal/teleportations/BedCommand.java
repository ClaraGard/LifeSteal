package fr.clawara.lifesteal.teleportations;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class BedCommand implements CommandExecutor {
	

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if(player.isInCombat()) {
			player.getBukkitPlayer().sendMessage("§cYou cannot use teleportations commands while in combat");
			return true;
		}
		player.bed();
		return true;
	}

}
