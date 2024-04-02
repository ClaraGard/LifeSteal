package fr.clawara.lifesteal.main;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.bans.Ban;
import fr.clawara.lifesteal.bans.Reason;
import fr.clawara.lifesteal.data.Data;

public class ResetHeartsCommands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if (!player.getBukkitPlayer().isOp())
			return false;
		File profileFolder = new File("/home/serveurLifetheft/plugins/LifeSteal/data/profiles");
		for (File f : profileFolder.listFiles()) {
			LifeStealPlayer p = (LifeStealPlayer) new Data(LifeStealPlayer.class).get(f.getName());
			if (p.getHearts() < 10) {
				p.setHearts(10);
			}
			for (Ban ban : p.getBans()) {
				if (ban.getReason() == Reason.DEATH)
					ban.end();
			}
			p.save();
		}
		return true;
	}

}
