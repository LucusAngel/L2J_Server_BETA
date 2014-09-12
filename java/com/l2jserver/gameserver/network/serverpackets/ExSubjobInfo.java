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

import com.l2jserver.gameserver.model.base.ClassLevel;
import com.l2jserver.gameserver.model.base.SubClass;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class ExSubjobInfo extends L2GameServerPacket
{
	private final L2PcInstance _player;
	
	public ExSubjobInfo(L2PcInstance _cha)
	{
		_player = _cha;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(0xEA);
		writeC(0x00);
		writeD(_player.getClassId().getId());
		writeD(_player.getRace().ordinal());
		writeD(_player.getSubClasses().size() + 1);
		
		writeD(_player.getClassIndex());
		writeD(_player.getBaseClass());
		writeD(_player.getStat().getBaseLevel());
		writeC(0x00); // 0 main, 1 dual, 2 sub
		
		for (SubClass sc : _player.getSubClasses().values())
		{
			writeD(sc.getClassIndex());
			writeD(sc.getClassId());
			writeD(sc.getLevel());
			writeC(sc.getClassDefinition().isOfLevel(ClassLevel.Awaken) ? 1 : 2); // 0 main, 1 dual, 2 sub
		}
	}
}