package com.hackquest.shenlong55.renewableresources.blockrespawner;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.flags.StateFlag;

public final class BlockRespawner extends BukkitRunnable
{
	private static BlockRespawner			instance;
	private final StateFlag					blockRespawnerWGFlag;
	private final BlockRespawnerConfig		config;
	private final HashSet<RespawningBlock>	respawningBlocks;

	private BlockRespawner(final StateFlag blockRespawnerWGFlag, final YamlConfiguration config)
	{
		this.blockRespawnerWGFlag = blockRespawnerWGFlag;
		this.config = BlockRespawnerConfig.getInstance(config);
		respawningBlocks = new HashSet<>();
	}

	public static BlockRespawner getInstance(final StateFlag blockRespawnerWGFlag, final YamlConfiguration config)
	{
		if (instance == null)
		{
			instance = new BlockRespawner(blockRespawnerWGFlag, config);
		}

		return instance;
	}

	public static BlockRespawner getInstance(final YamlConfiguration config)
	{
		return getInstance(null, config);
	}

	public void loadRespawningBlocksFromFile(final File dataFile)
	{
		final YamlConfiguration respawningBlocksData = YamlConfiguration.loadConfiguration(dataFile);
		final ConfigurationSection savedBlocksNode = respawningBlocksData.getConfigurationSection("RespawningBlocks");
		if (savedBlocksNode != null)
		{
			for (final String blockKey : savedBlocksNode.getKeys(false))
			{
				final String blockNodePath = "RespawningBlocks." + blockKey;
				final String worldName = respawningBlocksData.getString(blockNodePath + ".world");
				final Block block = Bukkit.getWorld(worldName).getBlockAtKey(Long.valueOf(blockKey));
				final String respawnType = respawningBlocksData.getString(blockNodePath + ".type");
				final Long respawnTime = respawningBlocksData.getLong(blockNodePath + ".respawnTime");
				respawningBlocks.add(new RespawningBlock(block, respawnTime, respawnType));
			}
		}
	}

	@Override
	public void run()
	{
		final HashSet<RespawningBlock> respawningBlocks = new HashSet<>(this.respawningBlocks);
		for (final RespawningBlock respawningBlock : respawningBlocks)
		{
			if (System.currentTimeMillis() >= respawningBlock.getRespawnTime())
			{
				respawningBlock.respawn();
				this.respawningBlocks.remove(respawningBlock);
			}
		}
	}

	public void saveRespawningBlocksToFile(final File dataFile) throws IOException
	{
		final YamlConfiguration respawningBlocksData = new YamlConfiguration();
		if (!respawningBlocks.isEmpty())
		{
			for (final RespawningBlock respawningBlock : respawningBlocks)
			{
				final String blockNodePath = "RespawningBlocks." + respawningBlock.getBlockKey().toString();
				respawningBlocksData.createSection(blockNodePath);
				respawningBlocksData.set(blockNodePath + ".world", respawningBlock.getWorld().getName());
				respawningBlocksData.set(blockNodePath + ".type", respawningBlock.getRespawnType().toString());
				respawningBlocksData.set(blockNodePath + ".respawnTime", respawningBlock.getRespawnTime());
			}
		}

		respawningBlocksData.save(dataFile);
	}

	protected void addRespawningBlock(final Block block, final Long respawnTime)
	{
		respawningBlocks.add(new RespawningBlock(block, respawnTime));
	}

	protected StateFlag getBlockRespawnerFlag()
	{
		return blockRespawnerWGFlag;
	}

	protected BlockRespawnerConfig getConfig()
	{
		return config;
	}

	protected Long getVeinRespawnTime(final Block block)
	{
		for (final RespawningBlock respawningBlock : respawningBlocks)
		{
			if ((respawningBlock.getFace(block) != null) && (respawningBlock.getRespawnType() == block.getType()))
			{
				return respawningBlock.getRespawnTime();
			}
		}

		return null;
	}
}