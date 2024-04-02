package fr.clawara.lifesteal.teleportations;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;

public class TpaCommand implements CommandExecutor {
	
	private static List<TpaRequest> requestPending = new ArrayList<>();

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(!(sender instanceof Player)) {
			return false;
		}
		
		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if(!player.hasTpa()) {
			player.getBukkitPlayer().sendMessage("§cYou have to wait "+Main.getTimeLeft(player.getTpaDelay())+" before doing /tpa again.");
			return true;
		}
		if(player.isInCombat()) {
			player.getBukkitPlayer().sendMessage("§cYou cannot use teleportations commands while in combat");
			return true;
		}
		if(args.length==0) {
			player.getBukkitPlayer().sendMessage("§cUsage: /tpa <Player>.");
			return true;
		}
		Player player2 = Bukkit.getPlayer(args[0]);
		if(player2==null) {
			player.getBukkitPlayer().sendMessage("§c"+args[0]+" is not online.");
			return true;
		}
		if(player.getBukkitPlayer().equals(player2)) {
			player.getBukkitPlayer().sendMessage("§cYou can't tp to yourself.");
			return true;
		}
		LifeStealPlayer target = LifeStealPlayer.get(player2.getUniqueId());
		TpaRequest request = getRequest(player, target);
		if(request == null) {
			requestPending.add(new TpaRequest(player, target));
			return true;
		}else {
			request.sendMessages();
			request.resetTimeLeft();
			return true;
		}
	}
	
	public static void cancelRequest(TpaRequest request) {
		requestPending.remove(request);
	}
	
	public static TpaRequest getRequest(LifeStealPlayer asker, LifeStealPlayer target) {
		for(TpaRequest request : requestPending) {
			if(request.getAsker().equals(asker) && request.getTarget().equals(target)) {
				return request;
			}
		}
		return null;
	}
	
	public static TpaRequest getLastRequest(LifeStealPlayer target) {
		TpaRequest request = null;
		for(TpaRequest i : requestPending) {
			if(i.getTarget().equals(target)) {
				request = i;
			}
		}
		return request;
	}

}
