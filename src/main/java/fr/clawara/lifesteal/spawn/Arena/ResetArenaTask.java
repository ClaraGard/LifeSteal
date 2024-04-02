package fr.clawara.lifesteal.spawn.Arena;

import org.bukkit.scheduler.BukkitRunnable;

import fr.clawara.lifesteal.main.Main;
import fr.clawara.lifesteal.spawn.SpawnProtection;

public class ResetArenaTask extends BukkitRunnable {

	@Override
	public void run() {
		Main.resetArena();
		SpawnProtection.blockPlaced.clear();
	}

}
