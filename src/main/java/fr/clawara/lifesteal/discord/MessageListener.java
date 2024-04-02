package fr.clawara.lifesteal.discord;

import java.awt.Color;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MessageListener extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		Message msg = e.getMessage();
		if(e.getChannelType() != ChannelType.TEXT || e.getAuthor().isBot()) {
			return;
		}
		if (e.getTextChannel().getId().equals(DiscordManager.WHITELIST_ID)) {
			if(DiscordManager.haveWhitelisted(e.getAuthor().getId())) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("Error");
				builder.setColor(Color.red);
				UUID uuid = UUID.fromString(DiscordManager.getWhichWhitelisted(e.getAuthor().getId()));
				PlayerIdentity identity = DiscordManager.getPlayerInfoFromAPI(uuid);
				if(Bukkit.getOfflinePlayer(uuid).getName()==null) {
					builder.setDescription("You already whitelisted the account **"+identity.getPlayerName()+"**, "+e.getAuthor().getAsMention()+" !\nWould you like to change account ?");
					e.getTextChannel().sendMessageEmbeds(builder.build()).setActionRow(Button.success("changeuser<!>"+identity.getUUID()+"<!>"+identity.getPlayerName()+"<!>"+e.getAuthor().getId(), "Unwhitelist "+identity.getPlayerName()), Button.danger("dontchangeuser<!>"+e.getAuthor().getId(), "I am "+identity.getPlayerName())).queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
				}else {
					builder.setDescription("You already whitelisted the account **"+identity.getPlayerName()+"**, "+e.getAuthor().getAsMention()+" !");
					e.getTextChannel().sendMessageEmbeds(builder.build()).queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
				}
				e.getMessage().delete().queue();
				return;
			}
			PlayerIdentity identity = DiscordManager.getPlayerInfoFromAPI(msg.getContentRaw());
			if(identity != null) {
				if(DiscordManager.isWhitelisted(identity.getUUID())) {
					EmbedBuilder builder = new EmbedBuilder();
					builder.setTitle("Error");
					builder.setColor(Color.red);
					builder.setDescription("The player is already whitelisted on the server!");
					e.getTextChannel().sendMessageEmbeds(builder.build()).queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
					e.getMessage().delete().queue();
					return;
				}
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("Found a player ! - "+identity.getPlayerName());
				builder.setColor(Color.green);
				builder.setImage(identity.getSkin());
				builder.setDescription("The player named **"+identity.getPlayerName()+"** was found *(UUID : "+identity.getUUID()+")*, is this your account ? \n:warning: Be sure to select the right account or you won't be able to connect!");
				e.getTextChannel().sendMessageEmbeds(builder.build()).setActionRow(Button.success("itsme<!>"+identity.getUUID()+"<!>"+identity.getPlayerName()+"<!>"+e.getAuthor().getId(), "It's me"), Button.danger("itsnotme<!>"+e.getAuthor().getId(), "It's not me")).queue();
			} else {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("Error");
				builder.setColor(Color.red);
				builder.setDescription("Couldn't find the player named \""+e.getMessage().getContentRaw()+"\", please try again "+e.getAuthor().getAsMention());
				e.getTextChannel().sendMessageEmbeds(builder.build()).queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
				e.getMessage().delete().queue();
			}
		}
	}
	
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().startsWith("itsme")) {
        	String[] args = event.getComponentId().split("<!>");
        	String playerUUID = args[1];
        	String playername = args[2];
        	String authorID = args[3];
        	if(authorID.equals(event.getMember().getId())) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("Success !");
				builder.setColor(Color.green);
				builder.setDescription("The player **"+playername+"** is now in the whitelist!");
				event.getMessage().editMessageEmbeds(builder.build()).setActionRows().queue();
				new LifeStealPlayer(UUID.fromString(playerUUID), authorID);
				DiscordManager.addWhitelist(authorID, playerUUID);
				DiscordManager.updateWhitelistCountChannel();
				event.deferEdit();
        	}
        } else if (event.getComponentId().startsWith("itsnotme")) {
        	String[] args = event.getComponentId().split("<!>");
        	String authorID = args[1];
        	if(authorID.equals(event.getMember().getId())) {
        		event.getMessage().delete().queue();
        		event.deferEdit();
        	}
        } else if (event.getComponentId().startsWith("changeuser")) {
        	String[] args = event.getComponentId().split("<!>");
        	String playerUUID = args[1];
        	String playername = args[2];
        	String authorID = args[3];
        	if(authorID.equals(event.getMember().getId())) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setTitle("Success !");
				builder.setColor(Color.green);
				builder.setDescription("The player **"+playername+"** is now unwhitelisted!");
				event.getMessage().editMessageEmbeds(builder.build()).setActionRows().queue();
				LifeStealPlayer.delete(UUID.fromString(playerUUID));
				DiscordManager.removeWhitelist(authorID);
				DiscordManager.updateWhitelistCountChannel();
				event.deferEdit();
				event.getMessage().delete().queueAfter(1, TimeUnit.MINUTES);
        	}
        }else if (event.getComponentId().startsWith("dontchangeuser")) {
        	String[] args = event.getComponentId().split("<!>");
        	String authorID = args[1];
        	if(authorID.equals(event.getMember().getId())) {
        		event.getMessage().delete().queue();
        		event.deferEdit();
        	}
        }
    }
	
}
