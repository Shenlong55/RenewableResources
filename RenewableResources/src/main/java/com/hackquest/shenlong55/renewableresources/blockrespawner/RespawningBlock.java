package com.hackquest.shenlong55.renewableresources.blockrespawner;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

final class RespawningBlock
{
	private final Block		block;
	private final Long		respawnTime;
	private final Material	respawnType;

	protected RespawningBlock(final Block block, final Long respawnTime)
	{
		this.block = block;
		this.respawnTime = respawnTime;
		respawnType = block.getType();
	}

	protected RespawningBlock(final Block block, final Long respawnTime, final String respawnType)
	{
		this.block = block;
		this.respawnTime = respawnTime;
		this.respawnType = Material.valueOf(respawnType);
	}

	protected Long getBlockKey()
	{
		return block.getBlockKey();
	}

	protected BlockFace getFace(final Block block)
	{
		return block.getFace(block);
	}

	protected Long getRespawnTime()
	{
		return respawnTime;
	}

	protected Material getRespawnType()
	{
		return respawnType;
	}

	protected World getWorld()
	{
		return block.getWorld();
	}

	protected void respawn()
	{
		block.setType(respawnType);
	}
}