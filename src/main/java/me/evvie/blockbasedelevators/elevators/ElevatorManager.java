package me.evvie.blockbasedelevators.elevators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * This class manages all elevators that get created during the runtime of the server
 */
public class ElevatorManager
{
	//materials that make up an valid elevator floor
	private final Material elevatorCenterMat;
	private final Material elevatorRimMat;
	
	//list of all known elevators since server start
	private List<Elevator> cachedElevators = new ArrayList<Elevator>();
	
	/**
	 * Constructor for the creation of the ElevatorManager
	 * @param centerMatName The material that makes up the center of an elevator
	 * @param rimMatName The material that makes up the rim of an elevator
	 */
	public ElevatorManager(String centerMatName, String rimMatName)
	{
		//parse the configuration material strings to materials
		elevatorCenterMat = Material.getMaterial(centerMatName.toUpperCase());
		elevatorRimMat = Material.getMaterial(rimMatName.toUpperCase());
	}
	
	/**
	 * This method checks if the given block is the center of an valid elevator floor
	 * @param centerBlock The potential center of an elevator
	 * @return True if the block is the center of an valid elevator floor, false otherwise
	 */
	public boolean checkForValidElevator(Block centerBlock)
	{
		if (centerBlock.getType() != elevatorCenterMat) return false;
		if (centerBlock.getRelative(BlockFace.NORTH).getType() != elevatorRimMat) return false;
		if (centerBlock.getRelative(BlockFace.EAST).getType() != elevatorRimMat) return false;
		if (centerBlock.getRelative(BlockFace.SOUTH).getType() != elevatorRimMat) return false;
		if (centerBlock.getRelative(BlockFace.WEST).getType() != elevatorRimMat) return false;
		return true;
	}
	
	/**
	 * This method checks if a block is part of an already existing elevator. If an elevator  
	 * is found it gets checked for the block. Should the block be missing from the elevator 
	 * it gets added to it. Blocks used to call this method need to be a valid elevator floor.
	 * @param block A block that is a valid elevator floor center
	 * @return The existing elevator, or null if no elevator containing this block exists
	 */
	public Elevator elevatorExists(Block block)
	{
		for (int i = 0; i < cachedElevators.size(); i++)
		{
			if (cachedElevators.get(i).exists(block))
			{
				if(cachedElevators.get(i).containsElevatorFloor(block))
					return cachedElevators.get(i);
				else
					cachedElevators.get(i).addElevatorFloor(block);
				
				return cachedElevators.get(i);
			}
		}
		
		return null;
	}
	
	/**
	 * This method creates a new elevator using an already checked center block
	 * @param block A block that is a valid elevator floor center
	 * @return The newly created elevator
	 */
	public Elevator createElevator(Block block)
	{
		Elevator elevator = new Elevator();
		elevator.create(block);
		cachedElevators.add(elevator);
		rescanElevator(cachedElevators.size() - 1);
		return cachedElevators.get(cachedElevators.size() - 1);
	}
	
	/**
	 * This method checks a block and triggers removal from an elevator if needed
	 * @param block A block that needs to be checked for being part of an elevator floor
	 */
	public void checkAndRemoveElevatorFloor(Block block)
	{
		if (block.getType() == elevatorCenterMat)
		{
			removeFloorFromElevator(block);
		}
		else if (block.getType() == elevatorRimMat)
		{
			Block elevatorBlock = isPartOfElevator(block);
			
			if (elevatorBlock != null)
			{
				removeFloorFromElevator(elevatorBlock);
			}
		}
	}
	
	/**
	 * This method rescans an elevator to update its floors
	 * @param index The index of the elevator that needs a rescan
	 */
	private void rescanElevator(final int index)
	{
		World world = cachedElevators.get(index).getWorld();
		cachedElevators.get(index).clearElevator();
		
		for (int i = world.getMinHeight(); i < world.getMaxHeight(); i++)
		{
			Block block = world.getBlockAt(cachedElevators.get(index).getX(), i, cachedElevators.get(index).getZ());
			
			if (checkForValidElevator(block))
			{
				cachedElevators.get(index).addElevatorFloor(block);
			}
		}
	}
	
	/**
	 * This method checks if a block is part of an elevator floor
	 * @param block A block that needs to be checked
	 * @return The center block of the floor that the given block is part of, null otherwise
	 */
	private Block isPartOfElevator(Block block)
	{
		if (block.getRelative(BlockFace.NORTH).getType() == elevatorCenterMat) return block.getRelative(BlockFace.NORTH);
		if (block.getRelative(BlockFace.EAST).getType() == elevatorCenterMat) return block.getRelative(BlockFace.EAST);
		if (block.getRelative(BlockFace.SOUTH).getType() == elevatorCenterMat) return block.getRelative(BlockFace.SOUTH);
		if (block.getRelative(BlockFace.WEST).getType() == elevatorCenterMat) return block.getRelative(BlockFace.WEST);
		return null;
	}

	/**
	 * This method removes a floor from an elevator
	 * @param block The center block of an elevator floor
	 */
	private void removeFloorFromElevator(Block block)
	{
		for (int i = 0; i < cachedElevators.size(); i++)
		{
			if (cachedElevators.get(i).exists(block))
			{
				cachedElevators.get(i).removeElevatorFloor(block);
			}
		}
	}
}
