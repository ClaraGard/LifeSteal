package fr.clawara.lifesteal.discord;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

public class SendMessageDiscord implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if (sender instanceof Player) {
			LifeStealPlayer player = LifeStealPlayer.get(((Player) sender).getUniqueId());
			if (!player.getBukkitPlayer().isOp())
				return false;		}

		if(args.length==0) {
			sender.sendMessage("§cUsage: /sendMessageDiscord <DiscordID> <Message>");
			return true;
		}
		String message = "";
		for(int i=1;i<args.length;i++) {
			message+=args[i]+" ";
		}
		message = message.replace("\\n", System.getProperty("line.separator"));
		final String messageFinal = message;
		JDA client = DiscordManager.getClient();
		User user = client.getUserById(args[0]);
		try {
			user.openPrivateChannel().queue(channel -> {
				channel.sendMessage(messageFinal).queue();
			});	
			sender.sendMessage("§eMessage sent to "+user.getAsTag());
		}catch(Exception e) {
			sender.sendMessage("§cCould not send message to "+user.getAsTag());
		}
		sender.sendMessage("§aDone");
		return true;
	}

}
