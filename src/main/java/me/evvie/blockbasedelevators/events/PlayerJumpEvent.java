package me.evvie.blockbasedelevators.events;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.util.Vector;

/**
 * This class is a custom PlayerJumpEvent
 * This event gets triggered if a player starts jumping
 */
public class PlayerJumpEvent extends PlayerEvent implements Cancellable
{
	//default implementation of a custom event
	private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
	
	public PlayerJumpEvent(Player player) 
	{
		super(player);
	}

	@Override
	public boolean isCancelled() 
	{
        return this.cancel;
    }

	@Override
    public void setCancelled(boolean cancel) 
	{
        this.cancel = cancel;
    }

	@Override
    public HandlerList getHandlers() 
	{
        return handlers;
    }
	
    public static HandlerList getHandlerList() 
    {
        return handlers;
    }

    /**
     * This inner-class handles the logic for the PlayerJumpEvent
     */
    public static class CallJumpEvent implements Listener 
    {
    	//map of jumping-states of all players on the server 
    	public static final Map<Player, Boolean> jumping = new HashMap<>();
    	
    	//threshold velocity for a jump to be considered jumping
        public static final double jump_velocity_threshold = 0.3;
        
        /**
         * This event-handler is used to grab PlayerMoveEvents and checks them for jumping behavior
         * @param event PlayerMoveEvent
         */
        @EventHandler
        public void onJump(PlayerMoveEvent event)
        {
        	//get player and his y-velocity
        	Player player = event.getPlayer();
        	double yVelocity = player.getVelocity().getY();
        	
        	//check if all jump conditions are met
        	if (yVelocity > jump_velocity_threshold && !isClimbing(player) && !jumping.get(player))
        	{
        		//create and call the custom jump event
        		PlayerJumpEvent playerJumpEvent = new PlayerJumpEvent(player);
        		Bukkit.getServer().getPluginManager().callEvent(playerJumpEvent);
        		
        		//cancel the jump if the jump event is canceled
        		if (playerJumpEvent.isCancelled())
        			player.setVelocity(new Vector(player.getVelocity().getX(), 0, player.getVelocity().getZ()));
        		
        		//track player as jumping
        		jumping.replace(player, true);
        	}
        	//if player is on ground reset his jumping state
        	else if (((LivingEntity)player).isOnGround() && jumping.get(player))
        	{
        		jumping.replace(player, false);
        	}
        }
        
        /**
         * This event-handler is used to grab the PlayerJoinEvent to track online players
         * @param event PlayerJoinEvent
         */
        @EventHandler
        public void onJoin(PlayerJoinEvent event)
        {
        	//start tracking joining players
            Player player = event.getPlayer();
            add(player);
        }

        /**
         * This event-handler is used to grab the PlayerQuitEvent to untrack leaving players
         * @param event PlayerQuitEvent
         */
        @EventHandler
        public void onQuit(PlayerQuitEvent event)
        {
        	//stop tracking leaving players
            Player player = event.getPlayer();
            remove(player);
        }

        /**
         * This event-handler is used to grab the PluginEnableEvent to track all players that might 
         * e there before the plugin was enabled
         * @param event PluginEnableEvent
         */
        @EventHandler
        public void onEnable(PluginEnableEvent event)
        {
        	//add all online players
            for (Player player: Bukkit.getOnlinePlayers()) 
            	add(player);
        }

        /**
         * This event-handler is used to grab the PluginDisableEvent to clear all tracking data before 
         * shutting down
         * @param event
         */
        @EventHandler
        public void onDisable(PluginDisableEvent event)
        {
        	//clear the map before shutting down
            jumping.clear();
        }

        /**
         * This method is used to add a player to the list of tracked players
         * @param player The player to be tracked
         */
        private void add(Player player)
        {
            if (!jumping.containsKey(player)) 
            	jumping.put(player, false);
        }
        
        /**
         * This method is used to remove a player from the list of tracked players
         * @param player The player to be removed
         */
        private void remove(Player player)
        {
            if (jumping.containsKey(player)) 
            	jumping.remove(player);
        }
        
        /**
         * This method is used to check if a player is in a climbing state
         * @param player The player to be checked
         * @return true if the player is climbing, false otherwise
         */
        private boolean isClimbing(Player player)
    	{
    		Material material = player.getLocation().getBlock().getType();
    		
    		return material == Material.LADDER || material == Material.VINE;
    	}
    }
}
