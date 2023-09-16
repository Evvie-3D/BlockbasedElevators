package me.evvie.blockbasedelevators.elevators;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * This class represents an elevator. 
 * It contains blocks that represent its floors sorted from lowest to highest.
 */
public class Elevator 
{
	//list of blocks representing floors of the elevator
	//sorted from lowest to highest
	private List<Block> elevatorBlocks = new ArrayList<Block>();
	
	//values needed to identify an elevator
	private World world;
	private int xLocation;
	private int zLocation;
	
	/**
	 * This method checks if an elevator already exists 
	 * @param block A block that should be part of an elevator
	 * @return True or false depending on the elevator already existing
	 */
	public boolean exists(Block block)
	{
		if (world == block.getWorld() && xLocation == block.getX() && zLocation == block.getZ()) return true;
		else return false;
	}
	
	/**
	 * This method creates the elevator based on a block
	 * @param startBlock A block that is part of the elevator
	 */
	public void create(Block startBlock)
	{
		xLocation = startBlock.getLocation().getBlockX();
		zLocation = startBlock.getLocation().getBlockZ();
		world = startBlock.getWorld();
	}
	
	/**
	 * This method is used to get the world of this elevator
	 * @return The world this elevator is part of
	 */
	public World getWorld()
	{
		return world;
	}
	
	/**
	 * This method gets the x-coordinate of the elevator
	 * @return The x-coordinate of the elevator
	 */
	public int getX()
	{
		return xLocation;
	}
	
	/**
	 * This method gets the z-coordinate of the elevator
	 * @return The z-coordinate of the elevator
	 */
	public int getZ()
	{
		return zLocation;
	}
	
	/**
	 * This method gets the next floor above the given block
	 * @param block The block representing the current floor
	 * @return The block representing the next floor if it exists, otherwise null
	 */
	public Block getAbove(Block block)
	{
		if(elevatorBlocks.contains(block))
		{
			int index = elevatorBlocks.indexOf(block);
			
			if (index < 0 || index + 1 == elevatorBlocks.size())
				return null;
			
			return elevatorBlocks.get(index + 1);
		}
		
		return null;
	}
	
	/**
	 * This method gets the next floor below the given block
	 * @param block The block representing the current floor
	 * @return The block representing the next floor if it exists, null otherwise
	 */
	public Block getBelow(Block block)
	{
		if(elevatorBlocks.contains(block))
		{
			int index = elevatorBlocks.indexOf(block);
			
			if (index - 1 < 0)
				return null;
			
			return elevatorBlocks.get(index - 1);
		}
		
		return null;
	}
	
	/**
	 * This method gets the amount of floors of the elevator
	 * @return The amount of floors of this elevator
	 */
	public int floorCount()
	{
		return elevatorBlocks.size();
	}
	
	/**
	 * This method gets the floor-number of a given floor block
	 * @param block The block representing the current floor
	 * @return The floor-number of the block, and -1 if the block is not part of the elevator
	 */
	public int getFloorNumber(Block block)
	{
		if(elevatorBlocks.contains(block))
			return elevatorBlocks.indexOf(block) + 1;
		return -1;
	}
	
	/**
	 * This method inserts a new floor into the elevator
	 * @param block The block representing the new floor
	 */
	public void addElevatorFloor(Block block)
	{
		//inserts a block to the elevator at the right position corresponding to its y-coordinate
		if(!elevatorBlocks.contains(block))
		{
			for (int i = 0; i < elevatorBlocks.size(); i++)
			{
				//if the current block is higher than the new one, this becomes the place of the 
				//new one and the rest gets shifted to the right
				if (elevatorBlocks.get(i).getLocation().getBlockY() > block.getLocation().getBlockY())
				{
					elevatorBlocks.add(i, block);
					
					//return early since the new position is fond
					return;
				}
			}
			
			//if no higher floor can be found, this becomes the new highest floor
			elevatorBlocks.add(block);
		}
	}
	
	/**
	 * This method removes a floor from the elevator
	 * @param block The block representing the floor
	 */
	public void removeElevatorFloor(Block block)
	{
		if(elevatorBlocks.contains(block))
			elevatorBlocks.remove(block);
	}
	
	/**
	 * This method checks if a block represents a floor in the elevator
	 * @param block The block that might represent a floor
	 * @return True if the block represents a floor, false otherwise
	 */
	public boolean containsElevatorFloor(Block block)
	{
		return elevatorBlocks.contains(block);
	}
	
	/**
	 * This method clears all floors from an elevator
	 */
	public void clearElevator()
	{
		elevatorBlocks.clear();
	}
}
