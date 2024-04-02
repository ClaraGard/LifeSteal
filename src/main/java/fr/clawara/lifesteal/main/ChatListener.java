package fr.clawara.lifesteal.main;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import fr.clawara.lifesteal.sanctions.mutes.Mute;
import fr.clawara.lifesteal.utils.GradientMessages;
import me.clip.placeholderapi.PlaceholderAPI;
public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
		for(Mute mute : player.getMutes()) {
			if(!mute.isExpired()) {
				String message = "§cYou are currently muted for: "+mute.getReason()+"\n§fYou will be unmuted in §9"+Main.getTimeLeft(mute.getTimeLeft());
				player.getBukkitPlayer().sendMessage(message);
				event.setCancelled(true);
				return;
			}
		}
		String pronouns = PlaceholderAPI.setPlaceholders(player.getBukkitPlayer(), "%pronouns_pronouns%");
		//String m = PlaceholderAPI.setPlaceholders(player.getBukkitPlayer(),"%gradient_message_#fa9efa_#9dacfa_"+pronouns+"%")+"§7|§f%s§7: %s";
		String message = GradientMessages.getGradientMessage(pronouns, "#9dacfa", "#fa9efa")+"§7 §f%s: §7%s";
		event.setFormat(message);
	}
	
	@EventHandler
	public void muteMsg(PlayerCommandPreprocessEvent event) {
		if(event.getMessage().toLowerCase().startsWith("/msg")) {
			LifeStealPlayer player = LifeStealPlayer.get(event.getPlayer().getUniqueId());
			for(Mute mute : player.getMutes()) {
				if(!mute.isExpired()) {
					String message = "§cYou are currently muted for: "+mute.getReason()+"\n§fYou will be unmuted in §9"+Main.getTimeLeft(mute.getTimeLeft());
					player.getBukkitPlayer().sendMessage(message);
					event.setCancelled(true);
					return;
				}
			}
		}
	}

		
}
