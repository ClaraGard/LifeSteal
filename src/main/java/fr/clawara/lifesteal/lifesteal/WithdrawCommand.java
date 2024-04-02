package fr.clawara.lifesteal.lifesteal;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class WithdrawCommand implements CommandExecutor {
	
	
	public WithdrawCommand() {
		
	}

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());

		if(args.length==0) {
			player.getBukkitPlayer().sendMessage("§cThis command has been disabled.");
			//player.getBukkitPlayer().sendMessage("§eYou still have "+player.getWithdraws()+" withdraws left.");
			return true;
		}
		try {
			int hearts = Integer.valueOf(args[0]);
			player.withdraw(hearts);
			return true;
		}catch(Exception e) {
			player.getBukkitPlayer().sendMessage("§c"+args[0]+" is not a number.");
			return true;
		}
	}

}
