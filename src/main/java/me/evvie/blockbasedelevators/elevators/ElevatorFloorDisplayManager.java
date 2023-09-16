package me.evvie.blockbasedelevators.elevators;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This class manages the floor GUI of players
 */
public class ElevatorFloorDisplayManager 
{
	private final int displaySeconds;
	private final Plugin plugin;
	private Map<Player, BossBar> playersWithDisplay = new HashMap<Player, BossBar>();
	
	/**
	 * Constructor for the creation of the manager used for floor GUIs for players
	 * @param plugin A reference to the main-class of this plug-in
	 * @param displaySeconds The duration the floor display should be shown
	 */
	ElevatorFloorDisplayManager(Plugin plugin, int displaySeconds)
	{
		this.plugin = plugin;
		this.displaySeconds = displaySeconds;
	}
	
	/**
	 * This method displays the floor to a player
	 * @param player The player the GUI will be created for
	 * @param floor The floor the player is on
	 * @param elevator The elevator that is used by the player
	 */
	public void displayFloorDispay(Player player, int floor, Elevator elevator)
	{
		//if the display already exists, update it with the new information
		if (playersWithDisplay.containsKey(player))
		{
			playersWithDisplay.get(player).setTitle("Floor " + floor + " of " + elevator.floorCount());
			playersWithDisplay.get(player).setProgress((float)floor / elevator.floorCount());
		}
		else //if not, create a new one
		{
			BossBar display = player.getServer().createBossBar("", BarColor.BLUE, BarStyle.SOLID);
			display.setTitle("Floor " + floor + " of " + elevator.floorCount());
			display.setProgress((float)floor / (float)elevator.floorCount());
			display.addPlayer(player);
			playersWithDisplay.put(player, display);
			
			//schedule the hiding of the BossBar
			startHideDisplayTimer(player, elevator);
		}
	}
	
	/**
	 * This method is used to create the timer used to hide the GUI with a delay
	 * @param player The player currently having a GUI element
	 * @param elevator The elevator used by the player
	 */
	private void startHideDisplayTimer(final Player player, final Elevator elevator)
	{
		//create a timer to remove the BossBar after a configured amount of time
		new BukkitRunnable()
		{
			@Override
			public void run() 
			{
				hideFloorDisplay(player, elevator);
			}
		}.runTaskLater(plugin, displaySeconds * 20); // * 20 since a server tick is 0.05 seconds
	}
	
	/**
	 * This method is used to remove the GUI element from a player
	 * @param player The player currently having a GUI element
	 * @param elevator The elevator used by the player
	 */
	private void hideFloorDisplay(Player player, Elevator elevator)
	{
		//player is still on the elevator, keep display up
		if (elevator.containsElevatorFloor(player.getLocation().getBlock().getRelative(BlockFace.DOWN)))
		{
			startHideDisplayTimer(player, elevator);
		}
		else //player left the elevator, clear display
		{
			playersWithDisplay.get(player).removeAll();
			playersWithDisplay.replace(player, null);
			playersWithDisplay.remove(player);
		}
	}
}
