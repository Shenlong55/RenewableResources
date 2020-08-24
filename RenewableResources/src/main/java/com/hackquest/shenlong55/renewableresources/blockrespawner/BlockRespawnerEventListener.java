package com.hackquest.shenlong55.renewableresources.blockrespawner;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.hackquest.shenlong55.renewableresources.RenewableResourcesConfig;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public final class BlockRespawnerEventListener implements Listener
{
	private final BlockRespawner			blockRespawner;
	private final RenewableResourcesConfig	config;

	public BlockRespawnerEventListener(final BlockRespawner blockRespawner, final RenewableResourcesConfig config)
	{
		this.blockRespawner = blockRespawner;
		this.config = config;
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent e)
	{
		final Block block = e.getBlock();
		final Material blockType = block.getType();
		if (blockRespawner.getConfig().hasBlockRespawnerEntry(blockType))
		{
			if (config.isBlockRespawnerWGFlagEnabled())
			{
				final RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
				final Location blockLocation = BukkitAdapter.adapt(block.getLocation());
				final ApplicableRegionSet set = query.getApplicableRegions(blockLocation);
				if (!set.testState(null, blockRespawner.getBlockRespawnerFlag()))
				{
					return;
				}
			}

			Long respawnTime = null;
			if (config.isSyncVeinRespawnEnabled())
			{
				respawnTime = blockRespawner.getVeinRespawnTime(block);
			}

			if (respawnTime == null)
			{
				final Long respawnMillis = blockRespawner.getConfig().getRespawnMillis(blockType);
				respawnTime = System.currentTimeMillis() + respawnMillis;
			}

			blockRespawner.addRespawningBlock(block, respawnTime);
		}
	}
}