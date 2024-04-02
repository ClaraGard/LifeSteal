package fr.clawara.lifesteal.spawnProtection;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class SpawnProtection implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
    	if(!event.getPlayer().getWorld().getName().equals("spawn")) return;
        if (event.getTo().getY() < 0) {
            event.setTo(event.getPlayer().getWorld().getSpawnLocation());
        }
    }
    
    @EventHandler
    public void damage(EntityDamageEvent event) {
    	if(!event.getEntity().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void foodChange(FoodLevelChangeEvent event) {
    	if(!event.getEntity().getWorld().getName().equals("spawn")) return;
    	if(event.getEntity().getFoodLevel()>event.getFoodLevel())
        event.setCancelled(true);
    }
    
    @EventHandler
    public void blockUpdates(BlockPhysicsEvent event) {
    	if(!event.getBlock().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	if(!event.getPlayer().getWorld().getName().equals("spawn")) return;
    	if(event.getAction()!=Action.RIGHT_CLICK_AIR && event.getAction()!=Action.RIGHT_CLICK_BLOCK) event.setCancelled(true);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
    	if(!event.getPlayer().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
    	if(!event.getEntity().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	if(!event.getBlock().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	if(!event.getBlock().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBucket(PlayerBucketFillEvent event) {
    	if(!event.getBlock().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
    	if(!event.getBlock().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
    	if(!event.getEntity().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }


    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
    	if(!event.getBlock().getWorld().getName().equals("spawn")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
    	if(!event.getWorld().getName().equals("spawn")) return;
        if(!event.getWorld().hasStorm())
            event.setCancelled(true);
    }
}
