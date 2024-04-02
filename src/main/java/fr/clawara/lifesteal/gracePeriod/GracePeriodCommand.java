package fr.clawara.lifesteal.gracePeriod;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class GracePeriodCommand implements CommandExecutor, TabCompleter {
	
	List<String> validArgs = new ArrayList<>();
	public GracePeriodCommand() {
		validArgs.add("remove");
	}

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if(player.isGracePeriodOver()) {
			player.getBukkitPlayer().sendMessage("§cYour grace period is over.");
			return true;
		}
		if(args.length==0 || !args[0].equalsIgnoreCase("remove")) {
			player.getBukkitPlayer().sendMessage("§aYou still have "+player.gracePeriodLeftString()+" of grace period left. Use /gp remove to end it.");
			return true;
		}else {
			player.endGracePeriod();
			return true;
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		if(args.length==1) {
			return validArgs;
		}
		return null;
	}

}
