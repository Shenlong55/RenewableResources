package com.hackquest.shenlong55.renewableresources;

import java.io.File;
import java.io.IOException;

import com.hackquest.shenlong55.ddpluginlibrary.DDPlugin;
import com.hackquest.shenlong55.ddpluginlibrary.WGStateFlagRegistrar;
import com.hackquest.shenlong55.renewableresources.autoplanter.AutoPlanterEventListener;
import com.hackquest.shenlong55.renewableresources.blockrespawner.BlockRespawner;
import com.hackquest.shenlong55.renewableresources.blockrespawner.BlockRespawnerEventListener;
import com.hackquest.shenlong55.renewableresources.plantpropagator.PlantPropagator;
import com.sk89q.worldguard.protection.flags.StateFlag;

public final class RenewableResourcesPlugin extends DDPlugin
{
	private final String	blockRespawnerFlagName		= "block-respawn";
	private final String	blockRespawnerDataFileName	= "BlockRespawnerData.yml";

	private BlockRespawner				blockRespawner;
	private File						blockRespawnerDataFile;
	private RenewableResourcesConfig	config;

	@Override
	public void onDisable()
	{
		if (config.isBlockRespawnerEnabled())
		{
			try
			{
				blockRespawner.saveRespawningBlocksToFile(blockRespawnerDataFile);
			}
			catch (final IOException e)
			{
				getLogger().warning("Error occured while attempting to save data file: " + blockRespawnerDataFile.getAbsolutePath());
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onLoad()
	{
		config = RenewableResourcesConfig.getInstance(updateConfiguration("config.yml"));
		setDebugging(getConfig().getBoolean("debug"));

		if (config.isBlockRespawnerEnabled())
		{
			if (config.isBlockRespawnerWGFlagEnabled())
			{
				try
				{
					final StateFlag blockRespawnerWGFlag = new WGStateFlagRegistrar(blockRespawnerFlagName, false).getFlag();
					if (blockRespawnerWGFlag != null)
					{
						blockRespawner = BlockRespawner.getInstance(blockRespawnerWGFlag, updateConfiguration("BlockRespawnerConfig.yml"));
					}
					else
					{
						getLogger().warning("Disabling '" + blockRespawnerFlagName + "' flag:  Another plugin already registered a flag by the same name.");
						config.setBlockRespawnerWGFlagEnabled(false);
					}
				}
				catch (final NoClassDefFoundError exception)
				{
					getLogger().warning("Disabling '" + blockRespawnerFlagName + "' flag:  WorldGuard was not found.  Please install WorldGuard to use flags.");
					config.setBlockRespawnerWGFlagEnabled(false);
				}
			}

			if (blockRespawner == null)
			{
				blockRespawner = BlockRespawner.getInstance(updateConfiguration("BlockRespawnerConfig.yml"));
			}
		}
	}

	@Override
	protected void preEnable()
	{
		if (config.isBlockRespawnerEnabled())
		{
			blockRespawnerDataFile = new File(DATA_FOLDER, blockRespawnerDataFileName);
			if (blockRespawnerDataFile.exists())
			{
				blockRespawner.loadRespawningBlocksFromFile(blockRespawnerDataFile);
			}
			blockRespawner.runTaskTimer(this, 0, 20);
		}

		if (config.isPlantPropagatorEnabled())
		{
			PlantPropagator.getInstance(config).runTaskTimer(this, 0, config.getPlantPropogatorTimerTicks());
		}
	}

	@Override
	protected void registerEventListeners()
	{
		if (config.isBlockRespawnerEnabled())
		{
			registerEventListener(new BlockRespawnerEventListener(blockRespawner, config));
		}

		if (config.isAutoPlanterEnabled())
		{
			registerEventListener(new AutoPlanterEventListener(this, config));
		}
	}
}