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

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;

public class ExUserInfoEquipSlot extends L2GameServerPacket
{
	private L2PcInstance _activeChar;
	private int _airShipHelm;
	
	public ExUserInfoEquipSlot(L2PcInstance character)
	{
		_activeChar = character;
		if (_activeChar.isInAirShip() && _activeChar.getAirShip().isCaptain(_activeChar))
			_airShipHelm = _activeChar.getAirShip().getHelmItemId();
		else
			_airShipHelm = 0;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xFE);
		writeH(0x156);
		writeD(_activeChar.getObjectId());
		writeH(33);
		writeC(255);
		writeD(-1);
		
		
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_UNDER));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_UNDER));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_UNDER));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_REAR));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_REAR));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_REAR));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEAR));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_LEAR));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_LEAR));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_NECK));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_NECK));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_RFINGER));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_RFINGER));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_LFINGER));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_LFINGER));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HEAD));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_HEAD));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_HEAD));
		writeD(0);
		if (_airShipHelm == 0)
		{
			writeH(18);
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
			writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_RHAND));
			writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
			writeD(0);
			writeH(18);
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND));
			writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_LHAND));
			writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_LHAND));
			writeD(0);
		}
		else
		{
			writeH(18);
			writeD(_airShipHelm);
			writeD(0);
			writeD(0);
			writeD(0);
			writeH(18);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_GLOVES));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_GLOVES));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CHEST));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_CHEST));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_CHEST));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEGS));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_LEGS));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_LEGS));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FEET));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_FEET));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_FEET));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CLOAK));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_CLOAK));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_CLOAK));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_RHAND));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIR));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_HAIR));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_HAIR));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIR2));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_HAIR2));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_HAIR2));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RBRACELET));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_RBRACELET));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_RBRACELET));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LBRACELET));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_LBRACELET));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_LBRACELET));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DECO1));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_DECO1));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_DECO1));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DECO2));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_DECO2));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_DECO2));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DECO3));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_DECO3));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_DECO3));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DECO4));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_DECO4));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_DECO4));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DECO5));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_DECO5));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_DECO5));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_DECO6));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_DECO6));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_DECO6));
		writeD(0);
		writeH(18);
		writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_BELT));
		writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_BELT));
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_BELT));
		writeD(0);
		///////////////////////////////////////////////////////////////
		writeH(18); // 603-1
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeH(18); // 603-2
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeH(18); // 603-3
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeH(18); // 603-4
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeH(18); // 603-5
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeH(18); // 603-6
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeH(18); // 603-7
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
	}
}
