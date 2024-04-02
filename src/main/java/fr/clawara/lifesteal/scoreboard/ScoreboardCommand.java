package fr.clawara.lifesteal.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class ScoreboardCommand implements CommandExecutor, TabCompleter {
	
	List<String> validArgs = new ArrayList<>();
	
	public ScoreboardCommand() {
		validArgs.add("toggle");
		validArgs.add("on");
		validArgs.add("off");
	}

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		
		if(args.length==0) {
			player.scoreboardToggle();
			return true;
		}

		if(args.length!=0 && !validArgs.contains(args[0])) {
			player.getBukkitPlayer().sendMessage("§cUsage: /sb <toggle/on/off>");
			return true;
		}
		if(args[0].equalsIgnoreCase("toggle")) {
			player.scoreboardToggle();
			return true;
		}
		if(args[0].equalsIgnoreCase("on")) {
			player.enableScoreboard();
			return true;
		}
		if(args[0].equalsIgnoreCase("off")) {
			player.disableScoreboard();
			return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		
		if(args.length==1) {
			return validArgs;
		}
		return null;
	}

}
