package fr.clawara.lifesteal.teleportations;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;


public class GotolifestealCommand implements CommandExecutor {
	

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if(!player.getBukkitPlayer().getWorld().getName().equals(Main.spawn.getWorld().getName())) {
			player.getBukkitPlayer().sendMessage("§cYou can only use this command when you are at spawn.");
			return true;
		}
		if(player.getLocationInTheWorld()==null) {
			player.getBukkitPlayer().teleport(Main.getValidSpawnAround(Main.worldSpawn));
			player.getBukkitPlayer().sendMessage("§cYour old location could not be found you have been teleported to world spawn instead.\nIf you see this message please report it to an admin.");
			return true;
		}
		player.getBukkitPlayer().teleport(player.getLocationInTheWorld());
		return true;
	}

}
