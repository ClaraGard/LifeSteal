package fr.clawara.lifesteal.combatLog;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class CombatTagCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if(!(sender instanceof Player)) {
			return false;
		}
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());

		if(!player.isInCombat()) {
			player.getBukkitPlayer().sendMessage("§aYou are not in combat, you can disconnect safely.");
			return true;
		}else {
			player.getBukkitPlayer().sendMessage("§cYou are still in combat for "+(Main.config.combatTime-((System.currentTimeMillis()-player.getLastHit())/1000))+" seconds, do not disconnect.");
			return true;
		}
	}

}
