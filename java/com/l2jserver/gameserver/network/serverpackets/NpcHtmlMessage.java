/*
 * Copyright (C) 2004-2013 L2J Server
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

import com.l2jserver.gameserver.enums.HtmlActionScope;

/**
 * @author Unknown, FBIagent
 */
public final class NpcHtmlMessage extends AbstractHtmlPacket
{
	/**
	 * @author Battlecruiser
	 */
	public static class CommonStrings
	{
		
		public static final String APPLY = "%apply%";
		public static final String BLOODOATH = "%bloodoath%";
		public static final String BLUE_STONE_NEEDED = "%blueStoneNeeded%";
		public static final String BONUS_TABLE = "%bonusTable%";
		public static final String BUSYMESSAGE = "%busymessage%";
		public static final String CASTLE = "%castle%";
		public static final String CASTLE_NAME = "%castleName%";
		public static final String CASTLENAME = "%castlename%"; // FIXME: unify %castleName% and %castlename% in DP!
		public static final String CLANNAME = "%clanname%";
		public static final String CONTRIB_STONE_COLOR = "%contribStoneColor%";
		public static final String COST = "%cost%";
		public static final String CYCLE_MINS = "%cycleMins%";
		public static final String DATE = "%date%";
		public static final String EXP_PERIOD = "%exp_period%";
		public static final String EXP_RECOVERY = "%exp_recovery%";
		public static final String FEE = "%fee%";
		public static final String FESTIVAL_TYPE = "%festivalType%";
		public static final String GREEN_STONE_NEEDED = "%greenStoneNeeded%";
		public static final String HALLNAME = "%hallname%";
		public static final String HP_PERIOD = "%hp_period%";
		public static final String HP_RECOVERY = "%hp_recovery%";
		public static final String HP_REGEN = "%hp_regen%";
		public static final String HR = "%hr%";
		public static final String HR2 = "%hr2%";
		public static final String CHANGE_EXP = "%change_exp%";
		public static final String CHANGE_HP = "%change_hp%";
		public static final String CHANGE_MP = "%change_mp%";
		public static final String CHANGE_SUPPORT = "%change_support%";
		public static final String CHANGE_TELE = "%change_tele%";
		public static final String LEADERNAME = "%leadername%";
		public static final String MIN = "%min%";
		public static final String MIN_FESTIVAL_PARTY_MEMBERS = "%minFestivalPartyMembers%";
		public static final String MIN2 = "%min2%";
		public static final String MP = "%mp%";
		public static final String MP_PERIOD = "%mp_period%";
		public static final String MP_RECOVERY = "%mp_recovery%";
		public static final String MP_REGEN = "%mp_regen%";
		public static final String NAME = "%name%";
		public static final String NPC_ID = "%npcId%";
		public static final String NPCNAME = "%npcname%";
		public static final String OBJECT_ID = "%objectId%";
		public static final String PLAYERCOUNT = "%playercount%";
		public static final String PLAYERNAME = "%playername%";
		public static final String RED_STONE_NEEDED = "%redStoneNeeded%";
		public static final String RENT = "%rent%";
		public static final String REQ_ITEMS = "%req_items%";
		public static final String STATS_TABLE = "%statsTable%";
		public static final String STONE_COLOR = "%stoneColor%";
		public static final String STONE_COUNT = "%stoneCount%";
		public static final String STONE_ITEM_ID = "%stoneItemId%";
		public static final String STONE_VALUE = "%stoneValue%";
		public static final String SUPPLYLVL = "%supplylvl%";
		public static final String SUPPORT = "%support%";
		public static final String SUPPORT_PERIOD = "%support_period%";
		public static final String SUPPORT2 = "%support%";
		public static final String TEAM1PLAYERCOUNT = "%team1playercount%";
		public static final String TEAM1POINTS = "%team1points%";
		public static final String TEAM2NAME = "%team2name%";
		public static final String TEAM2PLAYERCOUNT = "%team2playercount%";
		public static final String TEAM2POINTS = "%team2points%";
		public static final String TELE = "%tele%";
		public static final String TELE_PERIOD = "%tele_period%";
		public static final String USE = "%use%";
		public static final String VAL = "%val%";
		public static final String XP_REGEN = "%xp_regen%";
		public static final String TEAM1NAME = "%team1name%";
		public static final String REPLACE = "%replace%";
		
		private CommonStrings()
		{
			
		}
	}
	
	private final int _itemId;
	
	public NpcHtmlMessage()
	{
		_itemId = 0;
	}
	
	public NpcHtmlMessage(int npcObjId)
	{
		super(npcObjId);
		_itemId = 0;
	}
	
	public NpcHtmlMessage(int npcObjId, int itemId)
	{
		super(npcObjId);
		
		if (itemId < 0)
		{
			throw new IllegalArgumentException();
		}
		
		_itemId = itemId;
	}
	
	public NpcHtmlMessage(int npcObjId, int itemId, String html)
	{
		super(npcObjId, html);
		
		if (itemId < 0)
		{
			throw new IllegalArgumentException();
		}
		
		_itemId = itemId;
	}
	
	public NpcHtmlMessage(int npcObjId, String html)
	{
		super(npcObjId, html);
		_itemId = 0;
	}
	
	public NpcHtmlMessage(String html)
	{
		super(html);
		_itemId = 0;
	}
	
	@Override
	public HtmlActionScope getScope()
	{
		return _itemId == 0 ? HtmlActionScope.NPC_HTML : HtmlActionScope.NPC_ITEM_HTML;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x19);
		
		writeD(getNpcObjId());
		writeS(getHtml());
		writeD(_itemId);
	}
}
