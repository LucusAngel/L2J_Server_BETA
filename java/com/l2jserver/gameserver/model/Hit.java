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
package com.l2jserver.gameserver.model;

/**
 * @author UnAfraid
 */
public class Hit
{
	//FIXME: Validate this!!! (Battlecruiser)
	private static final int HITFLAG_USESS = 0x80; // l2jtw add
	private static final int HITFLAG_CRIT = 0x40; // l2jtw add
	private static final int HITFLAG_SHLD = 0x20; // l2jtw add
	private static final int HITFLAG_MISS = 0x10; // l2jtw add
	
	private final int _targetId;
	private final int _damage;
	private final int _ssGrade; // l2jtw add
	private int _flags = 0;
	
	public Hit(L2Object target, int damage, boolean miss, boolean crit, byte shld, boolean soulshot, int ssGrade)
	{
		_targetId = target.getObjectId();
		_damage = damage;
		_ssGrade = ssGrade; // l2jtw add
		
		if (soulshot)
		{
			/* l2jtw add
			_flags |= HITFLAG_USESS | ssGrade;
			 */
			_flags |= HITFLAG_USESS;
		}
		
		if (crit)
		{
			_flags |= HITFLAG_CRIT;
		}
		
		if (shld > 0)
		{
			_flags |= HITFLAG_SHLD;
		}
		
		if (miss)
		{
			_flags |= HITFLAG_MISS;
		}
	}
	
	public int getTargetId()
	{
		return _targetId;
	}
	
	public int getDamage()
	{
		return _damage;
	}
	
	public int getFlags()
	{
		return _flags;
	}
	// l2jtw add start
	public int getSSGrade()
	{
		return _ssGrade;
	}
	// l2jtw add end
}
