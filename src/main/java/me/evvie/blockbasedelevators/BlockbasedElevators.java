package me.evvie.blockbasedelevators;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import me.evvie.blockbasedelevators.elevators.ElevatorListener;
import me.evvie.blockbasedelevators.events.PlayerJumpEvent;

/**
 * The main-class of this plug-in
 * It handles the start-up and shut-down logic of this plug-in
 */
public class BlockbasedElevators extends JavaPlugin
{
	@Override
	public void onEnable() 
	{
        getLogger().info("Enable elevatorsystems");
        
        //save the default configuration, if it doesn't exist
        saveDefaultConfig();
        
        //register the custom Jump-Event
        getServer().getPluginManager().registerEvents(new PlayerJumpEvent.CallJumpEvent(), this);
        
        //register elevator functionality
        getServer().getPluginManager().registerEvents(new ElevatorListener(this), this);
	}
	
	@Override
	public void onDisable() 
	{
		getLogger().info("Disable elevatorsystems");
		
		//clean up everything added by this plug-in
		HandlerList.unregisterAll(this);;
	}
}
