package fr.clawara.lifesteal.sanctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.clawara.lifesteal.discord.DiscordManager;
import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;
import fr.clawara.lifesteal.sanctions.bans.Ban;
import fr.clawara.lifesteal.sanctions.mutes.Mute;
import net.dv8tion.jda.api.entities.User;

public class ModCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
		if (!player.getBukkitPlayer().isOp())
			return false;
		if(args.length!=1) {
			player.getBukkitPlayer().sendMessage("§cUsage /mod <Player>");
			return true;
		}
		@SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
		if(target.getFirstPlayed()==0) {
			player.getBukkitPlayer().sendMessage("§cPlayer "+args[0]+" does not exist");
			return true;
		}
		LifeStealPlayer targett = LifeStealPlayer.get(target.getUniqueId());
		int nbMutes = targett.getMutes().size();
		int nbBans = targett.getBans().size();
		Inventory inv = Bukkit.createInventory(null, (1+((1+nbBans+nbMutes)/9))*9);
		ItemStack head1 = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skull1 = (SkullMeta) head1.getItemMeta();
		skull1.setDisplayName("§r§e"+target.getName());
		skull1.setOwningPlayer(target);
		List<String> loreList = new ArrayList<>();
		loreList.add("§aFirst Join: "+targett.getFirstLogin());
		loreList.add("§eUUID: "+target.getUniqueId());
		DiscordManager.getClient().retrieveUserById(targett.getIdDiscord()).map(User::getAsTag).queue(name -> {
			loreList.add("§9Discord Name: "+name);
			loreList.add("§4Bans: "+nbBans);
			loreList.add("§cMutes: "+nbMutes);
			loreList.add("§aIP: "+targett.getAddress());
			skull1.setLore(loreList);
			head1.setItemMeta(skull1);
			inv.setItem(0, head1);
		});
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
			for(Ban ban : targett.getBans()) {
				ItemStack itBan = new ItemStack(Material.BARRIER);
				ItemMeta metaBans = itBan.getItemMeta();
				metaBans.setLore(Arrays.asList("§cReason: "+ban.getReason(),"§9Beginning: "+ban.getBeginning(), "§aTime left: "+Main.getTimeLeft(ban.getTimeLeft()), "§eComment: "+ban.getComment()));
				itBan.setItemMeta(metaBans);
				inv.addItem(itBan);
			}
			for(Mute mute : targett.getMutes()) {
				ItemStack itMute = new ItemStack(Material.PAPER);
				ItemMeta mutesMeta = itMute.getItemMeta();
				mutesMeta.setLore(Arrays.asList("§cReason: "+mute.getReason(),"§9Beginning: "+mute.getBeginning(), "§aTime left: "+Main.getTimeLeft(mute.getTimeLeft()), "§eComment: "+mute.getComment()));
				itMute.setItemMeta(mutesMeta);
				inv.addItem(itMute);
			}
			player.getBukkitPlayer().openInventory(inv);
		}, 20);
		return true;
	}

}
