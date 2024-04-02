package fr.clawara.lifesteal.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.data.Data;

public class EndSeasonCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if (!player.getBukkitPlayer().isOp())
			return false;
		for(Object o : new Data(LifeStealPlayer.class).getAll()) {
			LifeStealPlayer p = (LifeStealPlayer) o;
			p.reset();
			player.getBukkitPlayer().sendMessage("§eData of "+p.getUsername()+" was reseted.");
		}
		player.getBukkitPlayer().sendMessage("§aDone");
		return true;
	}

}
