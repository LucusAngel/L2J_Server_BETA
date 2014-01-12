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
package com.l2jserver.gameserver.model.actor.instance;

import com.l2jserver.Config;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jserver.gameserver.network.serverpackets.NpcHtmlMessage.CommonStrings;

public class L2TvTEventNpcInstance extends L2Npc
{
	
	private static final String HTML_PATH = "data/html/mods/TvTEvent/";
	
	public L2TvTEventNpcInstance(int objectId, L2NpcTemplate template)
	{
		super(objectId, template);
		setInstanceType(InstanceType.L2TvTEventNpcInstance);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance playerInstance, String command)
	{
		TvTEvent.onBypass(command, playerInstance);
	}
	
	@Override
	public void showChatWindow(L2PcInstance playerInstance, int val)
	{
		if (playerInstance == null)
		{
			return;
		}
		
		if (TvTEvent.isParticipating())
		{
			final boolean isParticipant = TvTEvent.isPlayerParticipant(playerInstance.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), HTML_PATH + "Participation.htm");
			}
			else
			{
				htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), HTML_PATH + "RemoveParticipation.htm");
			}
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
				final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace(CommonStrings.OBJECT_ID, String.valueOf(getObjectId()));
				npcHtmlMessage.replace(CommonStrings.TEAM1NAME, Config.TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace(CommonStrings.TEAM1PLAYERCOUNT, String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace(CommonStrings.TEAM2NAME, Config.TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace(CommonStrings.TEAM2PLAYERCOUNT, String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace(CommonStrings.PLAYERCOUNT, String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));
				if (!isParticipant)
				{
					npcHtmlMessage.replace(CommonStrings.FEE, TvTEvent.getParticipationFee());
				}
				
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (TvTEvent.isStarting() || TvTEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), HTML_PATH + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
				int[] teamsPointsCounts = TvTEvent.getTeamsPoints();
				final NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				// npcHtmlMessage.replace(CommonStrings.OBJECT_ID, String.valueOf(getObjectId()));
				npcHtmlMessage.replace(CommonStrings.TEAM1NAME, Config.TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace(CommonStrings.TEAM1PLAYERCOUNT, String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace(CommonStrings.TEAM1POINTS, String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace(CommonStrings.TEAM2NAME, Config.TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace(CommonStrings.TEAM2PLAYERCOUNT, String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace(CommonStrings.TEAM2POINTS, String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		
		playerInstance.sendPacket(ActionFailed.STATIC_PACKET);
	}
}
