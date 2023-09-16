package me.evvie.blockbasedelevators.elevators;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import me.evvie.blockbasedelevators.BlockbasedElevators;
import me.evvie.blockbasedelevators.events.PlayerJumpEvent;

/**
 * This enum is used to manage the elevator directions
 */
enum Direction
{
	UP,
	DOWN
}

/**
 * This class is used to handle the events that are used to trigger interactions with the elevators
 */
public class ElevatorListener implements Listener
{
	private final ElevatorManager elevatorManager;
	private final ElevatorFloorDisplayManager elevatorDisplayManager;
	private final boolean displayFloorInfo;
	
	/**
	 * Constructor to create the ElevatorListener with all needed information
	 * @param plugin Reference to the main-class of this plug-in
	 */
	public ElevatorListener(BlockbasedElevators plugin)
	{
		//create the ElevatorManager using the configuration data
		FileConfiguration config = plugin.getConfig();
		elevatorManager = new ElevatorManager(config.getString("ElevatorCenterBlockMaterial"), config.getString("ElevatorRimMaterial"));
		displayFloorInfo = config.getBoolean("DisplayFloorOnTeleport");
		
		//prepare the ElevatorFloorDisplayManager
		elevatorDisplayManager = new ElevatorFloorDisplayManager(plugin, config.getInt("DisplayDuration"));
	}
	
	/**
	 * The event-handler used to listen for PlayerToggleSneakEvents
	 * @param event PlayerToggleSneakEvent
	 */
	@EventHandler
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event)
	{
		Player player = event.getPlayer();
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		//player toggles into the sneaking mode and is standing on a valid elevator
		if (!player.isSneaking() && elevatorManager.checkForValidElevator(block))
		{
			executeElevatorTeleport(Direction.DOWN, player, block);
		}
	}
	
	/**
	 * The event-handler used to listen for PlayerJumpEvents (custom event by this plug-in)
	 * @param event PlayerJumpEvent
	 */
	@EventHandler
	public void onPlayerJumpEvent(PlayerJumpEvent event)
	{
		Player player = event.getPlayer();
		Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		
		//player is standing on a valid elevator
		if (elevatorManager.checkForValidElevator(block))
		{
			executeElevatorTeleport(Direction.UP, player, block);
		}
	}
	
	/**
	 * The event-handler used to listen for BlockBreakEvents
	 * @param event BlockBreakEvent
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		
		//make sure to remove floors from elevators if the block was part of one
		elevatorManager.checkAndRemoveElevatorFloor(block);
	}
	
	/**
	 * This method is used to handle the parts of the telportation process, that need the 
	 * elevator object itself
	 * @param direction The direction the player wants to be teleported
	 * @param player The player that wants to teleport
	 * @param start The current floor of the elevator
	 */
	private void executeElevatorTeleport(Direction direction, Player player, Block start)
	{
		//check and get the used elevator
		Elevator elevator = elevatorManager.elevatorExists(start);
		
		//create a new one, if there wasn't one already
		if (elevator == null)
			elevator = elevatorManager.createElevator(start);
		
		Block targetFloor = null;
		
		//trigger the teleport into the wished direction
		switch (direction)
		{
			case UP:
				targetFloor = elevator.getAbove(start);
				break;
			case DOWN:
				targetFloor = elevator.getBelow(start);
				break;
			default:
				break;
		}
		
		//check if there is a valid target, and do nothing if not
		if (targetFloor == null)
			return;
		
		//execute the teleport
		teleportPlayerToFloor(player, targetFloor);
		
		//display floor information
		displayFloorInformation(player, elevator.getFloorNumber(targetFloor), elevator);
	}
	
	/**
	 * This method is used to teleport a player to an other floor
	 * @param player The player that wants to teleport
	 * @param target The target floor to teleport to
	 */
	private void teleportPlayerToFloor(Player player, Block target)
	{
		//adjust player location to fit the target location height
		//this way optional data like rotation stay intact
		Location targetLocation = player.getLocation();
		targetLocation.setY(target.getLocation().getY() + 1);
		
		//teleport the player while adding some effects and sound as feedback
		player.getWorld().spawnParticle(Particle.GLOW, player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ(), 10, 0.5, 1, 0.5);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		player.teleport(targetLocation);
		player.getWorld().spawnParticle(Particle.GLOW, targetLocation.getX(), targetLocation.getY() + 1, targetLocation.getZ(), 10, 0.5, 1, 0.5);
	}
	
	/**
	 * This method is used to display the current floor to a player after telporting to a new one
	 * @param player The player that needs the GUI element
	 * @param targetFloor The new floor number
	 * @param elevator The elevator currently used by the player
	 */
	private void displayFloorInformation(Player player, int targetFloor, Elevator elevator)
	{
		if (displayFloorInfo)
		{
			//old floor info using big on-screen text
			//player.resetTitle();
			//player.sendTitle("Floor " + targetFloor + " of " + floorCount, null, 10, 40, 10);

			//display floor information using the BossBar GUI
			elevatorDisplayManager.displayFloorDispay(player, targetFloor, elevator);
		}
	}
}
