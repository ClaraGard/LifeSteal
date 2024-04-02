package fr.clawara.lifesteal.sanctions.mutes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class MuteCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if (!player.getBukkitPlayer().isOp())
			return false;
		if(args.length<3) {
			player.getBukkitPlayer().sendMessage("§cUsage /mute <Player> <Reason> <Duration in hours> (<Comment>)");
			return true;
		}
		@SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if(target.getFirstPlayed()==0) {
			player.getBukkitPlayer().sendMessage("§cPlayer "+args[0]+" does not exist");
			return true;
		}
		LifeStealPlayer targett = LifeStealPlayer.get(target.getUniqueId());
		int value;
		try {
			value = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			player.getBukkitPlayer().sendMessage("§cUsage /mute <Player> <Reason> <Duration in hours> <Comment>");
			return true;
		}
		String reason = args[1];
		reason.replace("_", " ");
		if(value==0) value = 8760*15;
		String comment = "";
		for(int i=3;i<args.length;i++) {
			comment += args[i]+" ";
		}
		targett.mute(reason, value, comment);
		player.getBukkitPlayer().sendMessage("§a"+target.getName()+" has been muted for "+reason+" during "+value+" hours");
		return true;
	}

}
