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

import java.sql.Connection; // 603	
import java.sql.PreparedStatement; // 603
import java.sql.ResultSet; // 603
import java.util.List;

import javolution.util.FastList;

import com.l2jserver.L2DatabaseFactory; // 603
import com.l2jserver.gameserver.datatables.CharNameTable;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Support for "Chat with Friends" dialog. <br />
 * This packet is sent only at login.
 * @author Tempy
 */
public class FriendList extends L2GameServerPacket
{
	private final List<FriendInfo> _info;
	
	private static class FriendInfo
	{
		int _objId;
		String _name;
		boolean _online;
		int _classid; // 603
		int _level; // 603
		
		public FriendInfo(int objId, String name, boolean online, int classid, int level) // 603
		{
			_objId = objId;
			_name = name;
			_online = online;
			_classid = classid; // 603
			_level = level; // 603
		}
	}
	
	public FriendList(L2PcInstance player)
	{
		_info = new FastList<>(player.getFriendList().size());
		for (int objId : player.getFriendList())
		{
			String name = CharNameTable.getInstance().getNameById(objId);
			L2PcInstance player1 = L2World.getInstance().getPlayer(objId);
			boolean online = false;
			int classid = 0; // 603
			int level = 0; // 603
 			// 603-Start
			if (player1 == null)
			{
				try (Connection con = L2DatabaseFactory.getInstance().getConnection();
					PreparedStatement statement = con.prepareStatement("SELECT char_name, online, classid, level FROM characters WHERE charId = ?"))
				{
					statement.setInt(1, objId);
					try (ResultSet rset = statement.executeQuery())
					{
						if (rset.next())
						{
							_info.add(new FriendInfo(objId, rset.getString(1), rset.getInt(2) == 1, rset.getInt(3), rset.getInt(4)));
						}
					}
				}
				catch (Exception e)
				{
					// Who cares?
				}
				continue;
			}
			// 603-End
			if (player1.isOnline())
			{
				online = true;
			}
			classid = player1.getClassId().getId(); // 603
			level = player1.getLevel(); // 603
			_info.add(new FriendInfo(objId, name, online, classid, level)); // 603
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x75);
		writeD(_info.size());
		for (FriendInfo info : _info)
		{
			writeD(info._objId); // character id
			writeS(info._name);
			writeD(info._online ? 0x01 : 0x00); // online
			writeD(info._online ? info._objId : 0x00); // object id if online
			writeD(info._level); // 603
			writeD(info._classid); // 603
			writeS(""); // 603
		}
	}
}
