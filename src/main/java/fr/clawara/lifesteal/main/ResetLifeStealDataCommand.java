package fr.clawara.lifesteal.main;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.data.Data;

public class ResetLifeStealDataCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if (!player.getBukkitPlayer().isOp())
			return false;
		if(args.length!=1) {
			player.getBukkitPlayer().sendMessage("§cUsage /resetlifestealdata <Player>");
			return true;
		}
		@SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if(target.getFirstPlayed()==0) {
			player.getBukkitPlayer().sendMessage("§cPlayer "+args[0]+" does not exist");
			return true;
		}
		LifeStealPlayer targett = LifeStealPlayer.get(target.getUniqueId());
		targett.reset();
		player.getBukkitPlayer().sendMessage("§eData of "+targett.getUsername()+" was reseted.");
		player.getBukkitPlayer().sendMessage("§aDone");
		return true;
	}

}
