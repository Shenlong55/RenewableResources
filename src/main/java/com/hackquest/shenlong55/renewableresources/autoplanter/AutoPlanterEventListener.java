package com.hackquest.shenlong55.renewableresources.autoplanter;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.hackquest.shenlong55.ddpluginlibrary.DDPlugin;
import com.hackquest.shenlong55.ddpluginlibrary.DDTags;
import com.hackquest.shenlong55.renewableresources.RenewableResourcesConfig;

public final class AutoPlanterEventListener implements Listener
{
	private final RenewableResourcesConfig	config;
	private final DDPlugin					plugin;
	private final Random					random;
	private final BukkitScheduler			scheduler;

	public AutoPlanterEventListener(final DDPlugin plugin, final RenewableResourcesConfig config)
	{
		this.config = config;
		this.plugin = plugin;
		random = new Random();
		scheduler = Bukkit.getServer().getScheduler();
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event)
	{
		final Block block = event.getBlock();
		final Material blockType = block.getType();
		if ((config.isAutoPlantSaplingsEnabled() && DDTags.LEAVES.contains(blockType)) || (config.isAutoPlantSeedsEnabled() && DDTags.SEED_DROPPING_PLANTS.contains(blockType)))
		{
			final Collection<ItemStack> drops = block.getDrops();
			final Location blockLocation = block.getLocation();

			event.setCancelled(true);
			block.setType(Material.AIR);

			for (final ItemStack itemStack : drops)
			{
				final Material itemType = itemStack.getType();
				boolean cancelled = false;
				if (DDTags.SAPLINGS.contains(itemType))
				{
					if (random.nextFloat() < config.getSaplingGerminationChanceOnDrop())
					{
						final Block growthBlock = findSaplingGrowthBlock(block);
						if (growthBlock != null)
						{
							cancelled = true;

							scheduler.scheduleSyncDelayedTask(plugin, () ->
							{
								plantSapling(growthBlock, itemType);
							}, config.getSaplingGerminationDelayTicks());
						}
					}
				}
				else if (DDTags.SEEDS.contains(itemType))
				{
					final int seedAmount = itemStack.getAmount();
					if (seedAmount > 1)
					{
						if (random.nextFloat() < config.getSeedGerminationChance())
						{
							itemStack.setAmount(seedAmount - 1);

							scheduler.scheduleSyncDelayedTask(plugin, () ->
							{
								if (block.isEmpty())
								{
									block.setType(blockType);
								}
							}, config.getSeedGerminationDelayTicks());
						}
					}
				}

				if (!cancelled)
				{
					block.getWorld().dropItemNaturally(blockLocation, itemStack);
				}
			}
		}
	}

	@EventHandler
	public void onLeafDecay(final LeavesDecayEvent event)
	{
		if (config.isAutoPlantSaplingsEnabled())
		{
			final Block block = event.getBlock();
			final Collection<ItemStack> drops = block.getDrops();

			event.setCancelled(true);
			block.setType(Material.AIR);

			for (final ItemStack itemStack : drops)
			{
				final Material itemType = itemStack.getType();
				boolean cancelled = false;
				if (DDTags.SAPLINGS.contains(itemType))
				{
					if (random.nextFloat() < config.getSaplingGerminationChanceOnDecay())
					{
						final Block growthBlock = findSaplingGrowthBlock(block);
						if (growthBlock != null)
						{
							cancelled = true;

							scheduler.scheduleSyncDelayedTask(plugin, () ->
							{
								plantSapling(growthBlock, itemType);
							}, config.getSaplingGerminationDelayTicks());
						}
					}
				}

				if (!cancelled)
				{
					block.getWorld().dropItemNaturally(block.getLocation(), itemStack);
				}
			}

		}
	}

	private Block findSaplingGrowthBlock(final Block block)
	{
		Block growthBlock = block;
		Block groundBlock = growthBlock.getRelative(BlockFace.DOWN);
		Material groundBlockType = groundBlock.getType();
		while (groundBlock.isPassable() || DDTags.LEAVES.contains(groundBlockType))
		{
			growthBlock = groundBlock;
			groundBlock = growthBlock.getRelative(BlockFace.DOWN);
			groundBlockType = groundBlock.getType();
		}

		if (growthBlock.isPassable() && DDTags.SAPLING_PLANTABLE_ON.contains(groundBlockType))
		{
			return growthBlock;
		}
		else
		{
			return null;
		}
	}

	private void plantSapling(final Block block, final Material saplingType)
	{
		if (config.isAvoidSaplingOvercrowdingEnabled())
		{
			int neighboringTrees = 0;
			for (int modX = -3; modX <= 3; modX++)
			{
				for (int modY = -3; modY <= 3; modY++)
				{
					for (int modZ = -3; modZ <= 3; modZ++)
					{
						final Material neighborBlockType = block.getRelative(modX, modY, modZ).getType();
						if (DDTags.SAPLINGS.contains(neighborBlockType) || DDTags.LOGS.contains(neighborBlockType))
						{
							if (((modX < -1) || (modX > 1)) || (modY != 0) || ((modZ < -1) || (modZ > 1)))
							{
								return;
							}
							else
							{
								neighboringTrees++;
								if (!neighborBlockType.equals(saplingType) || (neighboringTrees > 3))
								{
									return;
								}
							}
						}
					}
				}
			}
		}

		if (block.isPassable() && DDTags.SAPLING_PLANTABLE_ON.contains(block.getRelative(BlockFace.DOWN).getType()))
		{
			block.setType(saplingType);
		}
	}
}