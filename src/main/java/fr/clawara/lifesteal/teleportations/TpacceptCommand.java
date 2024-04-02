package fr.clawara.lifesteal.teleportations;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;

public class TpacceptCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if(player.isInCombat()) {
			player.getBukkitPlayer().sendMessage("§cYou cannot use teleportations commands while in combat");
			return true;
		}
		if(args.length==0) {
			TpaRequest request = TpaCommand.getLastRequest(player);
			if(request==null) {
				player.getBukkitPlayer().sendMessage("§cYou do not have any teleport request pending.");
				return true;
			}else {
				request.accept();
				return true;
			}
		}
		Player player2 = Bukkit.getPlayer(args[0]);
		if(player2==null) {
			player.getBukkitPlayer().sendMessage("§c"+args[0]+" is not online.");
			return true;
		}
		LifeStealPlayer target = LifeStealPlayer.get(player2.getUniqueId());
		TpaRequest request = TpaCommand.getRequest(target, player);
		if(request == null) {
			player.getBukkitPlayer().sendMessage("§c"+target.getBukkitPlayer().getName()+" haven't sent any teleport request to you.");
			return true;
		}else {
			request.accept();
			return true;
		}
	}

}
