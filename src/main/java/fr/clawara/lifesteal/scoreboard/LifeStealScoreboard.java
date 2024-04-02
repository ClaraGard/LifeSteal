package fr.clawara.lifesteal.scoreboard;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import fr.clawara.lifesteal.main.LifeStealPlayer;
import fr.clawara.lifesteal.main.Main;


public class LifeStealScoreboard extends BukkitRunnable {

	private Main main;

	public LifeStealScoreboard(Main main) {
		this.main = main;
	}

	@Override
	public void run() {
		Iterator<LifeStealPlayer> it = main.getOnlinePlayers().iterator();
		if(it!=null) {
			while (it.hasNext()) {
				LifeStealPlayer player = it.next();
				player.updateScoreboard();
			}	
		}
	}

}
