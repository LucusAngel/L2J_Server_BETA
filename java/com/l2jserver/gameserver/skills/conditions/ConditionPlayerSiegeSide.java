/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.gameserver.skills.conditions;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.skills.Env;

/**
 * The Class ConditionPlayerSiegeSide.
 */
public class ConditionPlayerSiegeSide extends Condition
{
	
	private final int _siegeSide;
	
	/**
	 * Instantiates a new condition player siege side.
	 *
	 * @param side the side
	 */
	public ConditionPlayerSiegeSide(int side)
	{
		_siegeSide = side;
	}
	
	@Override
	public boolean testImpl(Env env)
	{
		return ((L2PcInstance)env.player).getSiegeSide() == _siegeSide;
	}
}
