package com.hackquest.shenlong55.renewableresources.plantpropagator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.hackquest.shenlong55.ddpluginlibrary.DDTags;
import com.hackquest.shenlong55.renewableresources.RenewableResourcesConfig;

public final class PlantPropagator extends BukkitRunnable
{
	private static PlantPropagator			instance;
	private final Map<Material, Material>	alternateGrowthBlocks;
	private final RenewableResourcesConfig	config;
	private final int						verticalSearchRange	= 8;

	private PlantPropagator(final RenewableResourcesConfig config)
	{
		final Map<Material, Material> alternateGrowthBlocks = new HashMap<>();
		alternateGrowthBlocks.put(Material.ACACIA_LOG, Material.ACACIA_SAPLING);
		alternateGrowthBlocks.put(Material.BAMBOO, Material.BAMBOO_SAPLING);
		alternateGrowthBlocks.put(Material.BIRCH_LOG, Material.BIRCH_SAPLING);
		alternateGrowthBlocks.put(Material.DARK_OAK_LOG, Material.DARK_OAK_SAPLING);
		alternateGrowthBlocks.put(Material.JUNGLE_LOG, Material.JUNGLE_SAPLING);
		alternateGrowthBlocks.put(Material.KELP_PLANT, Material.KELP);
		alternateGrowthBlocks.put(Material.OAK_LOG, Material.OAK_SAPLING);
		alternateGrowthBlocks.put(Material.SPRUCE_LOG, Material.SPRUCE_SAPLING);
		this.alternateGrowthBlocks = Collections.unmodifiableMap(alternateGrowthBlocks);

		this.config = config;
	}

	public static PlantPropagator getInstance(final RenewableResourcesConfig config)
	{
		if (instance == null)
		{
			instance = new PlantPropagator(config);
		}

		return instance;
	}

	@Override
	public void run()
	{
		final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (final Player player : players)
		{
			final Set<Block> searchedBlocks = new HashSet<>();
			boolean newGrowthPlanted = false;
			int searchAttempts = 0;
			while (!newGrowthPlanted && (searchAttempts <= 150000))
			{
				searchAttempts++;

				final Block originPlantSearchBlock = getRandomRelativeBlock(config.getOriginPlantSearchRange(), player.getLocation().getBlock());
				if (!searchedBlocks.contains(originPlantSearchBlock))
				{
					searchedBlocks.add(originPlantSearchBlock);

					final Material originPlantSearchBlockType = originPlantSearchBlock.getType();
					if (DDTags.SEED_DISPERSING_LAND_PLANTS.contains(originPlantSearchBlockType) || DDTags.SEED_DISPERSING_WATER_PLANTS.contains(originPlantSearchBlockType))
					{
						final Set<Block> blocksSearchedForNewGrowth = new HashSet<>();
						int spreadAttempts = 0;
						while (!newGrowthPlanted && (spreadAttempts <= 50000))
						{
							spreadAttempts++;

							Block newGrowthSearchBlock = getRandomRelativeBlock(config.getNewGrowthSpreadRange(), originPlantSearchBlock);
							if (DDTags.SEED_DISPERSING_LAND_PLANTS.contains(originPlantSearchBlockType))
							{
								final Location newGrowthSearchLocation = getRandomRelativeBlock(config.getNewGrowthSpreadRange(), originPlantSearchBlock).getLocation();
								newGrowthSearchBlock = player.getWorld().getHighestBlockAt(newGrowthSearchLocation);
							}

							final Block newGrowthBlock = newGrowthSearchBlock.getRelative(BlockFace.UP);
							if (!newGrowthBlock.equals(originPlantSearchBlock) && !blocksSearchedForNewGrowth.contains(newGrowthSearchBlock) && newGrowthBlock.getLocation().getNearbyPlayers(1).isEmpty())
							{
								blocksSearchedForNewGrowth.add(newGrowthSearchBlock);

								final Material newGrowthSearchBlockType = newGrowthSearchBlock.getType();
								final Material newGrowthBlockType = newGrowthBlock.getType();
								boolean growthConditionsMet = false;
								if (newGrowthBlockType.equals(Material.WATER))
								{
									if (DDTags.WATER_PLANTS_PLANTABLE_ON.contains(newGrowthSearchBlockType))
									{
										if (DDTags.CORALS.contains(originPlantSearchBlockType))
										{
											growthConditionsMet = true;
										}
										else if (DDTags.CORAL_BLOCKS.contains(newGrowthSearchBlockType))
										{
											if (originPlantSearchBlockType.equals(Material.SEA_PICKLE))
											{
												growthConditionsMet = true;
											}
										}
										else if (DDTags.WATER_GRASSES.contains(originPlantSearchBlockType) && newGrowthBlock.getRelative(BlockFace.UP).getType().equals(Material.WATER))
										{
											growthConditionsMet = true;
										}
									}
								}
								else if (newGrowthBlockType.isAir())
								{
									if (originPlantSearchBlockType.equals(Material.CACTUS))
									{
										if (DDTags.SAND_BLOCKS.contains(newGrowthSearchBlockType))
										{
											final Block northNeighbor = newGrowthBlock.getRelative(BlockFace.NORTH);
											final Block eastNeighbor = newGrowthBlock.getRelative(BlockFace.EAST);
											final Block southNeighbor = newGrowthBlock.getRelative(BlockFace.SOUTH);
											final Block westNeighbor = newGrowthBlock.getRelative(BlockFace.WEST);
											if (northNeighbor.isPassable() && eastNeighbor.isPassable() && southNeighbor.isPassable() && westNeighbor.isPassable())
											{
												growthConditionsMet = true;
											}
										}
									}
									else if (newGrowthBlock.getLightLevel() >= 9)
									{
										if (originPlantSearchBlockType.equals(Material.BAMBOO))
										{
											if (DDTags.BAMBOO_PLANTABLE_ON.contains(newGrowthSearchBlockType))
											{
												growthConditionsMet = true;
											}
										}
										else if (DDTags.CROPS.contains(originPlantSearchBlockType))
										{
											if (newGrowthSearchBlockType.equals(Material.FARMLAND))
											{
												growthConditionsMet = true;
											}
										}
										else if (DDTags.FLOWERS.contains(originPlantSearchBlockType))
										{
											if ((newGrowthSearchBlockType.equals(Material.DIRT) || newGrowthSearchBlockType.equals(Material.GRASS_BLOCK)))
											{
												growthConditionsMet = true;
											}
										}
										else if (DDTags.LAND_GRASSES.contains(originPlantSearchBlockType))
										{
											if (newGrowthSearchBlockType.equals(Material.GRASS_BLOCK))
											{
												growthConditionsMet = true;
											}
										}
										else if (DDTags.LOGS.contains(originPlantSearchBlockType))
										{
											if (DDTags.SAPLING_PLANTABLE_ON.contains(newGrowthSearchBlockType))
											{
												growthConditionsMet = true;
											}
										}
										else if (originPlantSearchBlockType.equals(Material.SUGAR_CANE))
										{
											if (DDTags.SUGAR_CANE_PLANTABLE_ON.contains(newGrowthSearchBlockType))
											{
												final Material northNeighborType = newGrowthSearchBlock.getRelative(BlockFace.NORTH).getType();
												final Material eastNeighborType = newGrowthSearchBlock.getRelative(BlockFace.EAST).getType();
												final Material southNeighborType = newGrowthSearchBlock.getRelative(BlockFace.SOUTH).getType();
												final Material westNeighborType = newGrowthSearchBlock.getRelative(BlockFace.WEST).getType();
												if (northNeighborType.equals(Material.WATER) || eastNeighborType.equals(Material.WATER) || southNeighborType.equals(Material.WATER) || westNeighborType.equals(Material.WATER))
												{
													growthConditionsMet = true;
												}
											}
										}
										else if (originPlantSearchBlockType.equals(Material.SWEET_BERRY_BUSH))
										{
											if (DDTags.BERRY_BUSH_PLANTABLE_ON.contains(newGrowthSearchBlockType))
											{
												growthConditionsMet = true;
											}
										}
									}
								}

								if (growthConditionsMet)
								{
									if (alternateGrowthBlocks.containsKey(originPlantSearchBlockType))
									{
										newGrowthBlock.setType(alternateGrowthBlocks.get(originPlantSearchBlockType));
									}
									else
									{
										newGrowthBlock.setType(originPlantSearchBlockType);
									}
									newGrowthPlanted = true;
								}
							}
						}
					}
				}
			}
		}
	}

	private Block getRandomRelativeBlock(final int horizontalSearchRange, final Block block)
	{
		final int x = getRandomRelativePoint(horizontalSearchRange);
		final int y = getRandomRelativePoint(verticalSearchRange);
		final int z = getRandomRelativePoint(horizontalSearchRange);
		return block.getRelative(x, y, z);
	}

	private int getRandomRelativePoint(final int range)
	{
		return (int) ((Math.random() * range) - (range / 2));
	}
}