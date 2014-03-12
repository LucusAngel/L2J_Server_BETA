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
package com.l2jserver.gameserver.model.drops;

import com.l2jserver.gameserver.model.drops.strategy.IAmountMultiplierStrategy;
import com.l2jserver.gameserver.model.drops.strategy.IChanceMultiplierStrategy;
import com.l2jserver.gameserver.model.drops.strategy.IKillerChanceModifierStrategy;
import com.l2jserver.gameserver.model.drops.strategy.IPreciseDeterminationStrategy;


/**
 * @author Nos
 */
public enum DropListScope implements IDropItemFactory
{
	DEATH(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new GeneralDropItem(itemId, min, max, chance, IAmountMultiplierStrategy.DROP, IChanceMultiplierStrategy.DROP);
		}
		
	}),
	CORPSE(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new GeneralDropItem(itemId, min, max, chance, IAmountMultiplierStrategy.SPOIL, IChanceMultiplierStrategy.SPOIL);
		}
		
	}),
	/**
	 * This droplist scope isn't affected by ANY rates, nor Champion, etc...
	 */
	STATIC(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new GeneralDropItem(itemId, min, max, chance, IAmountMultiplierStrategy.STATIC, IChanceMultiplierStrategy.STATIC, IPreciseDeterminationStrategy.ALWAYS, IKillerChanceModifierStrategy.NO_RULES);
		}
		
	}),
	QUEST(new IDropItemFactory()
	{
		
		@Override
		public IDropItem newDropItem(int itemId, long min, long max, double chance)
		{
			return new GeneralDropItem(itemId, min, max, chance, IAmountMultiplierStrategy.STATIC, IChanceMultiplierStrategy.QUEST, IPreciseDeterminationStrategy.ALWAYS, IKillerChanceModifierStrategy.NO_RULES);
		}
	});
	
	private final IDropItemFactory _factory;
	
	private DropListScope(IDropItemFactory factory)
	{
		_factory = factory;
	}
	
	@Override
	public IDropItem newDropItem(int itemId, long min, long max, double chance)
	{
		return _factory.newDropItem(itemId, min, max, chance);
	}
	
	public GroupedGeneralDropItem newGroupedDropItem(double chance)
	{
		return new GroupedGeneralDropItem(chance);
	}
}
