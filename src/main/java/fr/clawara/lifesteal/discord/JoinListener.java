package fr.clawara.lifesteal.discord;

import fr.clawara.lifesteal.main.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinListener extends ListenerAdapter {
	
	@Override 
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String user = event.getMember().getAsMention();
        JDA client = event.getJDA();
        TextChannel welcome = (TextChannel) client.getGuildChannelById(DiscordManager.WELCOME_ID);
        TextChannel whitelist = (TextChannel) client.getGuildChannelById(DiscordManager.WHITELIST_ID);
        TextChannel rules = (TextChannel) client.getGuildChannelById(DiscordManager.RULES_ID);
        TextChannel info = (TextChannel) client.getGuildChannelById(DiscordManager.INFO_ID);
        //TextChannel announcements = (TextChannel) client.getGuildChannelById(DiscordManager.ANNOUNCEMENTS_ID);

        if(Main.config.serverOpening<System.currentTimeMillis()) {
            welcome.sendMessage("Welcome " + user+", to join the server write your username in "+whitelist.getAsMention()+" and join the IP mc.lifetheft.net, don't forget to read "+rules.getAsMention()+" and "+info.getAsMention()).queue();
        }else {
            welcome.sendMessage("Welcome " + user+", to join the server write your username in "+whitelist.getAsMention()+" and join the IP mc.lifetheft.net, don't forget to read "+rules.getAsMention()+" and "+info.getAsMention()+"\n**The Season 2 of LifeTheft starts <t:1655334000>**").queue();
        }
        
    }
	
	@Override 
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        String user = event.getMember().getEffectiveName();
        JDA client = event.getJDA();
        TextChannel welcome = (TextChannel) client.getGuildChannelById(DiscordManager.WELCOME_ID);

        welcome.sendMessage("Bye " + user+", you left the best LifeSteal server, your loss.").queue();
        
    }

}