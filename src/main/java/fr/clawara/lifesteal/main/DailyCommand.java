package fr.clawara.lifesteal.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyCommand implements CommandExecutor {
	

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		if(!Main.config.daily) return false;
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		player.useDaily();
		return true;
	}

}
