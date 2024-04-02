package fr.clawara.lifesteal.discord;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.data.Data;
import fr.clawara.lifesteal.main.LifeStealPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class SendMessageToSeasonMembersCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if (sender instanceof Player) {
			LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
			if (!player.getBukkitPlayer().isOp())
				return false;
		}

		String message = "";
		for(String s : args) {
			message+=s+" ";
		}
		JDA client = DiscordManager.getClient();
		message = message.replace("\\n", System.getProperty("line.separator"));
		final String messageFinal = message;
		List<Object> players = new Data(LifeStealPlayer.class).getAll();
		for(Object o : players) {
			LifeStealPlayer p = (LifeStealPlayer) o;
			System.out.println(p.getIdDiscord());
			try {
				User user = client.getUserById(p.getIdDiscord());
				System.out.println(user.getAsTag());
				user.openPrivateChannel().queue(channel -> {
		            channel.sendMessage(messageFinal).queue();
				});
				sender.sendMessage("§eMessage sent to "+p.getUsername());
			}
			catch(Exception e){
				System.out.println("error");
				sender.sendMessage("§Could not send message to "+p.getUsername());
			}
		}
		sender.sendMessage("§aDone");
		return true;
	}

}
