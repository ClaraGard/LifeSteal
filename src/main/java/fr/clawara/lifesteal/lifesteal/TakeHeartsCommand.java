package fr.clawara.lifesteal.lifesteal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class TakeHeartsCommand implements CommandExecutor {
		
	public TakeHeartsCommand() {
		
	}

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());

		if(args.length==0) {
			player.getBukkitPlayer().sendMessage("§cUsage is /takehearts <Number of hearts>.");
			return true;
		}
		try {
			int hearts = Integer.valueOf(args[0]);
			player.takeHearts(hearts);
			return true;
		}catch(Exception e) {
			player.getBukkitPlayer().sendMessage("§c"+args[0]+" is not a number.");
			return true;
		}
	}

}
