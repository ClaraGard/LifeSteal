package fr.clawara.lifesteal.discord;

import java.awt.Color;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;
import fr.clawara.lifesteal.sanctions.mutes.Mute;
import fr.clawara.lifesteal.utils.GradientMessages;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ServerMessages extends ListenerAdapter implements Listener {
	
	public static void dragonEggDestroyedMessage() {
		Bukkit.broadcastMessage("§c§lThe Dragon egg has been destroyed, the next dragon that will be killed will drop an egg.");
		JDA client = DiscordManager.getClient();
        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("The Dragon egg has been destroyed, the next dragon that will be killed will drop an egg.");
		builder.setColor(Color.red);
		serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
	}
	
	public static void dragonDeathMessage() {
		JDA client = DiscordManager.getClient();
        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("The Dragon was slain.");
		builder.setColor(Color.white);
		serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
	}
		
	@EventHandler(priority = EventPriority.HIGH)
	public void onMessageOnServer(AsyncPlayerChatEvent event) {
		if(event.isCancelled()) return;
		String playerName = event.getPlayer().getName();
		String message = event.getMessage();
		JDA client = DiscordManager.getClient();
        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
		serverMessagesChannel.sendMessage(playerName+" § "+message).queue();
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		JDA client = DiscordManager.getClient();
        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(event.getPlayer().getName()+" joined");
		builder.setColor(Color.green);
		serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		JDA client = DiscordManager.getClient();
        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(event.getPlayer().getName()+" left");
		builder.setColor(Color.red);
		serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		JDA client = DiscordManager.getClient();
        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(event.getDeathMessage());
		builder.setColor(Color.yellow);
		serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
	}
	
	@EventHandler
	public void killPet(EntityDamageByEntityEvent event) {
	    if (!(event.getEntity() instanceof Tameable))
	        return;
	    if (!(event.getDamager() instanceof Player))
	        return;
	    Player damager = (Player) event.getDamager();
	    Tameable pet = (Tameable) event.getEntity();
	    OfflinePlayer owner = null;
	    if(pet.isTamed()) {
	    	owner = (OfflinePlayer) pet.getOwner();
		    if (event.getFinalDamage() >= pet.getHealth()) {
		    	event.setDamage(0);
		    	pet.remove();
		    	Bukkit.broadcastMessage(pet.getName()+" ("+pet.getType().toString().toLowerCase()+" owned by "+owner.getName()+") was slain by "+damager.getName());
				JDA client = DiscordManager.getClient();
		        TextChannel serverMessagesChannel = (TextChannel) client.getGuildChannelById(DiscordManager.SERVERMESSAGES_ID);
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle(pet.getName()+" ("+pet.getType().toString().toLowerCase()+" owned by "+owner.getName()+") was slain by "+damager.getName());
				builder.setColor(Color.orange);
				serverMessagesChannel.sendMessageEmbeds(builder.build()).queue();
		    }
	    }
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if(e.getChannelType() != ChannelType.TEXT || e.getAuthor().isBot()) {
			return;
		}
		if(e.getTextChannel().getId().equals(DiscordManager.SERVERMESSAGES_ID)) {
			if(!DiscordManager.haveWhitelisted(e.getAuthor().getId()) || LifeStealPlayer.get(UUID.fromString(DiscordManager.getWhichWhitelisted(e.getAuthor().getId()))).getUsername() == null) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("Error");
				builder.setColor(Color.red);
				builder.setDescription(e.getAuthor().getAsMention()+" You cannot talk in the Minecraft server if you never connected to the Minecraft server.");
				e.getTextChannel().sendMessageEmbeds(builder.build()).queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
			}else {
				LifeStealPlayer player = LifeStealPlayer.get(UUID.fromString(DiscordManager.getWhichWhitelisted(e.getAuthor().getId())));
				for(Mute mute : player.getMutes()) {
					if(!mute.isExpired()) {
						String muteMessage = e.getAuthor().getAsMention()+" You are currently muted for: "+mute.getReason()+"\nYou will be unmuted in "+Main.getTimeLeft(mute.getTimeLeft());
						EmbedBuilder builder = new EmbedBuilder();
						builder.setTitle("Error");
						builder.setColor(Color.red);
						builder.setDescription(muteMessage);
						e.getTextChannel().sendMessageEmbeds(builder.build()).queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
						e.getMessage().delete().queue();
						return;
					}
				}
				String message = e.getMessage().getContentRaw();
				String playername = player.getUsername();
				String pronouns = PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(player.getUniqueId()), "%pronouns_pronouns%");
				Bukkit.broadcastMessage("§9Discord § §r"+GradientMessages.getGradientMessage(pronouns, "#9dacfa", "#fa9efa")+"§7 §f"+playername+": §7"+message);
				e.getTextChannel().sendMessage("*Discord*: "+playername+" § "+message).queue();
			}
			e.getMessage().delete().queue();
		}
	}
	
	

}
