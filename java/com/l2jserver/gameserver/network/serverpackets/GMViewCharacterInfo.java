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

import com.l2jserver.gameserver.datatables.ExperienceTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class GMViewCharacterInfo extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final int _runSpd, _walkSpd;
	private final int _swimRunSpd, _swimWalkSpd;
	private final int _flyRunSpd, _flyWalkSpd;
	private final double _moveMultiplier;
	
	public GMViewCharacterInfo(L2PcInstance cha)
	{
		_activeChar = cha;
		_moveMultiplier = cha.getMovementSpeedMultiplier();
		_runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
		_swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
		_swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
		_flyRunSpd = cha.isFlying() ? _runSpd : 0;
		_flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x95);
		
		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());
		writeD(_activeChar.getHeading());
		writeD(_activeChar.getObjectId());
		writeS(_activeChar.getName());
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getAppearance().getSex() ? 1 : 0);
		writeD(_activeChar.getClassId().getId());
		writeD(_activeChar.getLevel());
		writeQ(_activeChar.getExp());
		writeF((float) (_activeChar.getExp() - ExperienceTable.getInstance().getExpForLevel(_activeChar.getLevel())) / (ExperienceTable.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - ExperienceTable.getInstance().getExpForLevel(_activeChar.getLevel()))); // High Five exp %
		writeD(_activeChar.getSTR());
		writeD(_activeChar.getDEX());
		writeD(_activeChar.getCON());
		writeD(_activeChar.getINT());
		writeD(_activeChar.getWIT());
		writeD(_activeChar.getMEN());
		//looks like the two new stats (Battlecruiser)
		writeD(0); // 603-New-1
		writeD(0); // 603-New-2
		writeD(_activeChar.getMaxHp());
		writeD((int) _activeChar.getCurrentHp());
		writeD(_activeChar.getMaxMp());
		writeD((int) _activeChar.getCurrentMp());
		writeQ(_activeChar.getSp()); // 603
		writeD(_activeChar.getCurrentLoad());
		writeD(_activeChar.getMaxLoad());
		writeD(0); // 603
		
		for (int slot : getPaperdollOrder())
		{
			writeD(_activeChar.getInventory().getPaperdollObjectId(slot));
		}
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		
		for (int slot : getPaperdollOrder())
		{
			writeD(_activeChar.getInventory().getPaperdollItemDisplayId(slot));
		}
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		
		for (int slot : getPaperdollOrder())
		{
			writeD(_activeChar.getInventory().getPaperdollAugmentationId(slot));
		}
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		writeD(0); // 603
		
		writeC(_activeChar.getInventory().getTalismanSlots()); // 603 // CT2.3
		writeC(_activeChar.getInventory().canEquipCloak() ? 1 : 0); // 603 // CT2.3
		writeD(0x00); // 603
		writeH(0x00); // 603
		writeD(_activeChar.getPAtk(null));
		writeD(_activeChar.getPAtkSpd());
		writeD(_activeChar.getPDef(null));
		writeD(_activeChar.getEvasionRate(null));
		writeD(_activeChar.getAccuracy());
		writeD(_activeChar.getCriticalHit(null, null));
		writeD(_activeChar.getMAtk(null, null));
		
		writeD(_activeChar.getMAtkSpd());
		writeD(0x00); // 603
		
		writeD(_activeChar.getMDef(null, null));
		writeD(_activeChar.getEvasionRate(null)); // 603-Magic tEvasionRate
		writeD(_activeChar.getAccuracy()); // 603-Magic Accuracy
		writeD(_activeChar.getMCriticalHit(null, null)); // 603-Magic MCriticalHit
		
		writeD(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		int Karma = 0 - _activeChar.getKarma(); // 603-Test
		writeD(Karma); // 603-Test
		
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_swimRunSpd);
		writeD(_swimWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeD(_flyRunSpd);
		writeD(_flyWalkSpd);
		writeF(_moveMultiplier);
		writeF(_activeChar.getAttackSpeedMultiplier()); // 2.9);//
		writeF(_activeChar.getCollisionRadius()); // scale
		writeF(_activeChar.getCollisionHeight()); // y offset ??!? fem dwarf 4033
		writeD(_activeChar.getAppearance().getHairStyle());
		writeD(_activeChar.getAppearance().getHairColor());
		writeD(_activeChar.getAppearance().getFace());
		writeD(_activeChar.isGM() ? 0x01 : 0x00); // builder level
		
		writeS(_activeChar.getTitle());
		writeD(_activeChar.getClanId()); // pledge id
		writeD(_activeChar.getClanCrestId()); // pledge crest id
		writeD(_activeChar.getAllyId()); // ally id
		writeC(_activeChar.getMountType().ordinal()); // mount type
		writeC(_activeChar.getPrivateStoreType().getId());
		writeC(_activeChar.hasDwarvenCraft() ? 1 : 0);
		writeD(_activeChar.getPkKills());
		writeD(_activeChar.getPvpKills());
		
		writeH(_activeChar.getRecomLeft());
		writeH(_activeChar.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		writeD(_activeChar.getClassId().getId());
		writeD(0x00); // special effects? circles around player...
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		
		writeC(_activeChar.isRunning() ? 0x01 : 0x00); // changes the Speed display on Status Window
		
		writeC(321);
		
		writeD(_activeChar.getPledgeClass()); // changes the text above CP on Status Window
		
		writeC(_activeChar.isNoble() ? 0x01 : 0x00);
		writeC(_activeChar.isHero() ? 0x01 : 0x00);
		
		writeD(_activeChar.getAppearance().getNameColor());
		writeD(_activeChar.getAppearance().getTitleColor());
		
		byte attackAttribute = _activeChar.getAttackElement();
		writeH(attackAttribute);
		writeH(_activeChar.getAttackElementValue(attackAttribute));
		for (byte i = 0; i < 6; i++)
		{
			writeH(_activeChar.getDefenseElementValue(i));
		}
		writeD(_activeChar.getFame());
		writeD(_activeChar.getVitalityPoints());
		writeD(0); // 603
		writeD(0); // 603
	}
}
