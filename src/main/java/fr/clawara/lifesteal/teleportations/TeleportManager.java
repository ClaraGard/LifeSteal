package fr.clawara.lifesteal.teleportations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeleportManager {
	
	private static List<TeleportRequest> list = new ArrayList<>();
	
	public static void addNewRequest(TeleportRequest request) {
		Iterator<TeleportRequest> it = list.iterator();
		while(it.hasNext()) {
			TeleportRequest i = it.next();
			if(i.getPlayer().equals(request.getPlayer())) {
				list.remove(i);
				i.cancelTeleport();
				break;
			}
		}
		list.add(request);
	}

}
