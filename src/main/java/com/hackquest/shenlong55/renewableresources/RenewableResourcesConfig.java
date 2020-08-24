package com.hackquest.shenlong55.renewableresources;

import org.bukkit.configuration.file.YamlConfiguration;

import com.hackquest.shenlong55.ddpluginlibrary.DDConfig;

public final class RenewableResourcesConfig extends DDConfig
{
	private static RenewableResourcesConfig	instance;
	private final boolean					autoPlanterEnabled;
	private final boolean					autoPlantSaplingsEnabled;
	private final boolean					autoPlantSeedsEnabled;
	private final boolean					avoidSaplingOvercrowdingEnabled;
	private final boolean					blockRespawnerEnabled;
	private final int						newGrowthSpreadRange;
	private final int						originPlantSearchRange;
	private final boolean					plantPropagatorEnabled;
	private final int						plantPropagatorTimerTicks;
	private final float						saplingGerminationChanceDecay;
	private final float						saplingGerminationChanceDrop;
	private final int						saplingGerminationDelayTicks;
	private final float						seedGerminationChance;
	private final int						seedGerminationDelayTicks;
	private final boolean					syncVeinRespawnEnabled;

	private boolean blockRespawnerWGFlagEnabled;

	private RenewableResourcesConfig(final YamlConfiguration fileConfig)
	{
		autoPlanterEnabled = fileConfig.getBoolean("AutoPlanter.Enabled");
		autoPlantSaplingsEnabled = fileConfig.getBoolean("AutoPlanter.Saplings.Enabled");
		autoPlantSeedsEnabled = fileConfig.getBoolean("AutoPlanter.Seeds.Enabled");
		avoidSaplingOvercrowdingEnabled = fileConfig.getBoolean("AutoPlanter.Saplings.AvoidOvercrowding");
		blockRespawnerEnabled = fileConfig.getBoolean("BlockRespawner.Enabled");
		blockRespawnerWGFlagEnabled = fileConfig.getBoolean("BlockRespawner.WGFlag");
		newGrowthSpreadRange = fileConfig.getInt("PlantPropagator.NewGrowthSpreadRange");
		originPlantSearchRange = fileConfig.getInt("PlantPropagator.OriginPlantSearchRange");
		plantPropagatorEnabled = fileConfig.getBoolean("PlantPropagator.Enabled");

		final int attemptsPerDay = fileConfig.getInt("PlantPropagator.AttemptsPerDay");
		final int minutes = (((attemptsPerDay > 0) && (attemptsPerDay <= 20)) ? (20 / attemptsPerDay) : 1);
		plantPropagatorTimerTicks = minutes * 60 * 20;

		saplingGerminationChanceDecay = (fileConfig.getInt("AutoPlanter.Saplings.GerminationChance-Decay") / 100.0f);
		saplingGerminationChanceDrop = (fileConfig.getInt("AutoPlanter.Saplings.GerminationChance-Drop") / 100.0f);
		saplingGerminationDelayTicks = convertTimeStringToTicks(fileConfig.getString("AutoPlanter.Saplings.GerminationDelay"));
		seedGerminationChance = (fileConfig.getInt("AutoPlanter.Seeds.GerminationChance") / 100.0f);
		seedGerminationDelayTicks = convertTimeStringToTicks(fileConfig.getString("AutoPlanter.Seeds.GerminationDelay"));
		syncVeinRespawnEnabled = fileConfig.getBoolean("BlockRespawner.SyncVeinRespawn");
	}

	public static RenewableResourcesConfig getInstance(final YamlConfiguration fileConfig)
	{
		if (instance == null)
		{
			instance = new RenewableResourcesConfig(fileConfig);
		}

		return instance;
	}

	public int getNewGrowthSpreadRange()
	{
		return newGrowthSpreadRange;
	}

	public int getOriginPlantSearchRange()
	{
		return originPlantSearchRange;
	}

	public int getPlantPropogatorTimerTicks()
	{
		return plantPropagatorTimerTicks;
	}

	public float getSaplingGerminationChanceOnDecay()
	{
		return saplingGerminationChanceDecay;
	}

	public float getSaplingGerminationChanceOnDrop()
	{
		return saplingGerminationChanceDrop;
	}

	public long getSaplingGerminationDelayTicks()
	{
		return saplingGerminationDelayTicks;
	}

	public float getSeedGerminationChance()
	{
		return seedGerminationChance;
	}

	public long getSeedGerminationDelayTicks()
	{
		return seedGerminationDelayTicks;
	}

	public boolean isAutoPlanterEnabled()
	{
		return autoPlanterEnabled;
	}

	public boolean isAutoPlantSaplingsEnabled()
	{
		return autoPlantSaplingsEnabled;
	}

	public boolean isAutoPlantSeedsEnabled()
	{
		return autoPlantSeedsEnabled;
	}

	public boolean isAvoidSaplingOvercrowdingEnabled()
	{
		return avoidSaplingOvercrowdingEnabled;
	}

	public boolean isBlockRespawnerEnabled()
	{
		return blockRespawnerEnabled;
	}

	public boolean isBlockRespawnerWGFlagEnabled()
	{
		return blockRespawnerWGFlagEnabled;
	}

	public boolean isPlantPropagatorEnabled()
	{
		return plantPropagatorEnabled;
	}

	public boolean isSyncVeinRespawnEnabled()
	{
		return syncVeinRespawnEnabled;
	}

	public void setBlockRespawnerWGFlagEnabled(final boolean blockRespawnerWGFlagEnabled)
	{
		this.blockRespawnerWGFlagEnabled = blockRespawnerWGFlagEnabled;
	}
}