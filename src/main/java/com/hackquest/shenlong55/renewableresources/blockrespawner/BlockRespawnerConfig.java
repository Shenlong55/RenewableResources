package com.hackquest.shenlong55.renewableresources.blockrespawner;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hackquest.shenlong55.ddpluginlibrary.DDConfig;

public final class BlockRespawnerConfig extends DDConfig
{
	private static BlockRespawnerConfig		instance;
	private final HashMap<Material, Long>	minRespawnMillis;
	private final HashMap<Material, Long>	maxRespawnMillis;

	private BlockRespawnerConfig(final YamlConfiguration fileConfig)
	{
		minRespawnMillis = new HashMap<>();
		maxRespawnMillis = new HashMap<>();
		final ConfigurationSection blockTypesNode = fileConfig.getConfigurationSection("BlockTypes");
		for (final String blockType : blockTypesNode.getKeys(false))
		{
			final ConfigurationSection blockTypeNode = blockTypesNode.getConfigurationSection(blockType);
			final Material blockTypeMaterial = Material.valueOf(blockType.toUpperCase());
			if (blockTypeNode.contains("MinRespawnTime"))
			{
				final String minRespawnTimeString = fileConfig.getString("BlockTypes." + blockType + ".MinRespawnTime");
				minRespawnMillis.put(blockTypeMaterial, convertTimeStringToMillis(minRespawnTimeString));
			}

			if (blockTypeNode.contains("MaxRespawnTime"))
			{
				final String maxRespawnTimeString = fileConfig.getString("BlockTypes." + blockType + ".MaxRespawnTime");
				maxRespawnMillis.put(blockTypeMaterial, convertTimeStringToMillis(maxRespawnTimeString));
			}
		}
	}

	public static BlockRespawnerConfig getInstance(final YamlConfiguration fileConfig)
	{
		if (instance == null)
		{
			instance = new BlockRespawnerConfig(fileConfig);
		}

		return instance;
	}

	public Long getRespawnMillis(final Material blockType)
	{
		if (!maxRespawnMillis.isEmpty() && !minRespawnMillis.isEmpty())
		{
			final long min = minRespawnMillis.get(blockType);
			final long max = maxRespawnMillis.get(blockType);
			return (long) ((Math.random() * ((max - min) + 1)) + min);
		}
		else if (!maxRespawnMillis.isEmpty())
		{
			return maxRespawnMillis.get(blockType);
		}
		else if (!minRespawnMillis.isEmpty())
		{
			return minRespawnMillis.get(blockType);
		}

		return null;
	}

	public boolean hasBlockRespawnerEntry(final Material blockType)
	{
		return (maxRespawnMillis.containsKey(blockType) || minRespawnMillis.containsKey(blockType));
	}
}