package fr.clawara.lifesteal.spawn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import fr.clawara.lifesteal.main.Main;

public class SpawnProtection implements Listener {
	
	public static List<Block> blockPlaced = new ArrayList<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
    	if(!event.getPlayer().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
        if (event.getTo().getY() < 0) {
            event.setTo(Main.spawn);
        }
    }
    
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
    	if(!event.getPlayer().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onBoatPlace(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	if(!event.getPlayer().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		if (player.getInventory().getItemInMainHand().getType().toString().contains("BOAT") || player.getInventory().getItemInMainHand().getType()==Material.ARMOR_STAND) {
    			event.setCancelled(true);
    		}
    	}
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
    	if(!event.getPlayer().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
    	if(!event.getEntity().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockBoom(BlockExplodeEvent event) {
    	if(!event.getBlock().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	Iterator<Block> it = event.blockList().iterator();
    	while(it.hasNext()) {
    		Block b = it.next();
    		if(!blockPlaced.contains(b)) event.blockList().remove(b);
    	}
    }
    
    @EventHandler
    public void onEntityBoom(EntityExplodeEvent event) {
    	if(!event.getEntity().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	Iterator<Block> it = event.blockList().iterator();
    	while(it.hasNext()) {
    		Block b = it.next();
    		if(!blockPlaced.contains(b)) event.blockList().remove(b);
    	}
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
    	if(!event.getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
        if(!event.getWorld().hasStorm())
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onPearl(ProjectileLaunchEvent event) {
    	if(!event.getEntity().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	if(event.getEntity() instanceof EnderPearl) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onBucket(PlayerBucketFillEvent event) {
    	if(!event.getBlock().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
    	if(!event.getBlock().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
        event.setCancelled(true);
    }
    
    
    
    
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	if(!event.getPlayer().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	if(Main.isInTheArena(event.getPlayer().getLocation())) return;
    	if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) event.setCancelled(true);
    }
    
    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
    	if(!event.getEntity().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	if(Main.isInTheArena(event.getEntity().getLocation())) return;
    	if(event.getEntity().getFoodLevel()>event.getFoodLevel())
        event.setCancelled(true);
    }
    
    @EventHandler
    public void damage(EntityDamageEvent event) {
    	if(!event.getEntity().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	if(event.getEntityType()==EntityType.ENDER_CRYSTAL) return;
    	if(event instanceof EntityDamageByEntityEvent) {
            event.setCancelled(true);
            return;
    	}
    	if(Main.isInTheArena(event.getEntity().getLocation())) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(!event.getBlock().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	if(blockPlaced.contains(event.getBlock()) || event.getBlock().getType()==Material.FIRE) {
    		blockPlaced.remove(event.getBlock());
    		return;
    	}
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	if(!event.getBlock().getWorld().getName().equals(Main.spawn.getWorld().getName())) return;
    	if(Main.isInTheArena(event.getBlock().getLocation())) {
            blockPlaced.add(event.getBlockPlaced());
    		return;
    	}
        event.setCancelled(true);
    }
}
