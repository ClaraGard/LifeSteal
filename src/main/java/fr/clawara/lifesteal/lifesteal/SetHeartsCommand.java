package fr.clawara.lifesteal.lifesteal;


import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class SetHeartsCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if (!player.getBukkitPlayer().isOp())
			return false;
		if(args.length!=2) {
			player.getBukkitPlayer().sendMessage("§cUsage /sethearts <Player> <Value>");
			return true;
		}
		int value;
		try {
			value = Integer.valueOf(args[1]);
		} catch (NumberFormatException e) {
			player.getBukkitPlayer().sendMessage("§cUsage /sethearts <Player> <Value>");
			return true;
		}
		if(value<=0) {
			player.getBukkitPlayer().sendMessage("§cValue must be positive");
			return true;
		}
		Player target = Bukkit.getPlayer(args[0]);
		if(target==null) {
			player.getBukkitPlayer().sendMessage("§c"+args[0]+" is not online.");
			return true;
		}
		LifeStealPlayer targett = LifeStealPlayer.get(target.getUniqueId());
		targett.setHearts(value);
		if(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()>value*2) {
			target.setHealth(value*2);
			target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(value*2);	
		}else {
			target.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(value*2);
			target.setHealth(value*2);
		}
		player.getBukkitPlayer().sendMessage("§a"+args[0]+" now has "+value+" hearts");
		return true;
	}

}
