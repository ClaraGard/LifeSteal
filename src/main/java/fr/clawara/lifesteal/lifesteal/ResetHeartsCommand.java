package fr.clawara.lifesteal.lifesteal;

import java.io.File;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.data.Data;
import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;
import fr.clawara.lifesteal.sanctions.bans.Ban;

public class ResetHeartsCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if (!player.getBukkitPlayer().isOp())
			return false;
		File profileFolder = new File("/home/serveurLifetheft/plugins/LifeSteal/data/profiles");
		if(args.length!=2) {
			player.getBukkitPlayer().sendMessage("§cUsage /resethearts <10 <unban> or /resethearts all <unban>");
			return true;
		}
		boolean unban;
		try {
			unban = Boolean.valueOf(args[1]);
		} catch (NumberFormatException e) {
			player.getBukkitPlayer().sendMessage("§cUsage /resethearts <10 <unban> or /resethearts all <unban> (unban must be true or false)");
			return true;
		}
		if(args[0].equalsIgnoreCase("<10")) {
			for (File f : profileFolder.listFiles()) {
				LifeStealPlayer p = (LifeStealPlayer) new Data(LifeStealPlayer.class).get(f.getName());
				if (p.getHearts() < 10) {
					p.setHearts(10);
				}
				if(unban) {
					for (Ban ban : p.getBans()) {
						if (ban.getReason().equals("DEATH"))
							ban.end();
					}	
				}
				p.save();
				for(LifeStealPlayer pl : Main.getInstance().getOnlinePlayers()) {
					if(pl.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()>20) {
						pl.getBukkitPlayer().setHealth(20);
						pl.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);	
					}else {
						pl.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
						pl.getBukkitPlayer().setHealth(20);
					}
				}
			}
			return true;
		}
		if(args[0].equalsIgnoreCase("all")) {
			for (File f : profileFolder.listFiles()) {
				LifeStealPlayer p = (LifeStealPlayer) new Data(LifeStealPlayer.class).get(f.getName());
				p.setHearts(10);
				for(LifeStealPlayer pl : Main.getInstance().getOnlinePlayers()) {
					if(pl.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()>20) {
						pl.getBukkitPlayer().setHealth(20);
						pl.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);	
					}else {
						pl.getBukkitPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
						pl.getBukkitPlayer().setHealth(20);
					}
				}
				if(unban) {
					for (Ban ban : p.getBans()) {
						if (ban.getReason().equals("DEATH"))
							ban.end();
					}	
				}
				p.save();
			}
			return true;
		}
		return false;
	}

}
