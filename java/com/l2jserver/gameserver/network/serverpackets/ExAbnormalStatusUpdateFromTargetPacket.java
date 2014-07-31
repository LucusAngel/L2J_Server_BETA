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
package com.l2jserver.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import javolution.util.FastList;

import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.BuffInfo;

public class ExAbnormalStatusUpdateFromTargetPacket extends L2GameServerPacket
{
	private final int _objectId;
	private List<BuffInfo> _effects = new ArrayList<>();
	
	public ExAbnormalStatusUpdateFromTargetPacket(int ObjectId)
	{
		
		L2Character target = null;
		target = (L2Character) L2World.getInstance().findObject(ObjectId);
		_objectId = ObjectId;
		if (target != null)
		{
			_effects = target.getEffectList().getEffects();
		}
	}
	
	public ExAbnormalStatusUpdateFromTargetPacket(L2Object object)
	{
		_objectId = object.getObjectId();
		if (object instanceof L2Attackable)
		{
			_effects = ((L2Character) ((L2Attackable) object).getTarget()).getEffectList().getEffects();
		}
	}
	
	public ExAbnormalStatusUpdateFromTargetPacket(L2Character target)
	{
		_objectId = target.getObjectId();
		_effects = target.getEffectList().getEffects();
	}
	
	@Override
	protected final void writeImpl()
	{
		List<BuffInfo> el = new FastList<>();
		for (BuffInfo e : _effects)
		{
			if(e != null)
			{
				el.add(e);
			}
		}
		
		writeC(0xfe);
		writeH(0xe6); // 603
		writeD(_objectId);
		writeH(el.size());
		for (BuffInfo e : el)
		{
			if (e != null)
			{
				writeD(e.getSkill().getId());
				writeH(e.getSkill().getLevel());
				writeH(0); // 603-index?
				writeH(e.getAbnormalTime() - e.getTime()); // 603
				writeD(0); // GS-comment-037
			}
		}
	}
}
