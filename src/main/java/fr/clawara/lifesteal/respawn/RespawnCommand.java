package fr.clawara.lifesteal.respawn;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class RespawnCommand implements CommandExecutor, TabCompleter {
	
	List<String> validArgs = new ArrayList<>();
	
	public RespawnCommand() {
		validArgs.add("toggle");
		validArgs.add("bed");
		validArgs.add("spawn");
	}

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if(args.length==0) {
			player.getBukkitPlayer().sendMessage("§cUsage: /respawn <bed/spawn>");
			return true;
		}
		if(args.length!=0 && !validArgs.contains(args[0])) {
			player.getBukkitPlayer().sendMessage("§cUsage: /respawn <bed/spawn>");
			return true;
		}
		if(args[0].equalsIgnoreCase("toggle")) {
			player.respawnToggle();
			return true;
		}
		if(args[0].equalsIgnoreCase("bed")) {
			player.respawnInBed();
			return true;
		}
		if(args[0].equalsIgnoreCase("spawn")) {
			player.respawnAtSpawn();
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
