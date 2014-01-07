/*
 * Copyright (C) 2004-2014 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.model.actor.group;

import java.util.Arrays;
import java.util.List;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.interfaces.IL2Procedure;

/**
 * This class creates a wrapper around {@link L2PcInstance} to threat it {@link AbstractPlayerGroup}
 * @author Battlecruiser
 */
public class SoloPlayerGroup extends AbstractPlayerGroup
{
	private List<L2PcInstance> list;
	private final L2PcInstance _player;
	
	/**
	 * @param player to wrap
	 */
	public SoloPlayerGroup(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public void setLeader(L2PcInstance leader)
	{
		// do nothing
		
	}
	
	@Override
	public List<L2PcInstance> getMembers()
	{
		if (list == null)
		{
			list = Arrays.asList(_player);
		}
		return list;
	}
	
	@Override
	public int getLevel()
	{
		return _player.getLevel();
	}
	
	@Override
	public L2PcInstance getLeader()
	{
		return _player;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.AbstractPlayerGroup#forEachMember(com.l2jserver.gameserver.model.interfaces.IL2Procedure)
	 */
	@Override
	public boolean forEachMember(IL2Procedure<L2PcInstance> procedure)
	{
		return procedure.execute(_player);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.AbstractPlayerGroup#getMemberCount()
	 */
	@Override
	public int getMemberCount()
	{
		return 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.AbstractPlayerGroup#containsPlayer(com.l2jserver.gameserver.model.actor.instance.L2PcInstance)
	 */
	@Override
	public boolean containsPlayer(L2PcInstance player)
	{
		return _player.equals(player);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.l2jserver.gameserver.model.AbstractPlayerGroup#getRandomPlayer()
	 */
	@Override
	public L2PcInstance getRandomPlayer()
	{
		return _player;
	}
}