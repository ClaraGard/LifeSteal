package fr.clawara.lifesteal.discord;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class SendMessageToDiscordMembers implements CommandExecutor {

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
		message = message.replace("\\n", System.getProperty("line.separator"));
		final String messageFinal = message;
		JDA client = DiscordManager.getClient();
		for(Member m : client.getGuildById(970404230198353940L).getMembers()) {
			User user = m.getUser();
			try {
				user.openPrivateChannel().queue(channel -> {
		            channel.sendMessage(messageFinal).queue();
				});
				sender.sendMessage("§eMessage sent to "+user.getAsTag());
			}catch(Exception e) {
				sender.sendMessage("§cCould not send message to "+user.getAsTag());
			}
		}
		sender.sendMessage("§aDone");
		return true;
	}

}
