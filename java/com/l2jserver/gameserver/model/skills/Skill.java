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
package com.l2jserver.gameserver.model.skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.Config;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.datatables.SkillTreesData;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.handler.TargetHandler;
import com.l2jserver.gameserver.instancemanager.HandysBlockCheckerManager;
import com.l2jserver.gameserver.model.ArenaParticipantsHolder;
import com.l2jserver.gameserver.model.ChanceCondition;
import com.l2jserver.gameserver.model.L2ExtractableProductItem;
import com.l2jserver.gameserver.model.L2ExtractableSkill;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.PcCondOverride;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2BlockInstance;
import com.l2jserver.gameserver.model.actor.instance.L2CubicInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.entity.TvTEvent;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.interfaces.IChanceSkillTrigger;
import com.l2jserver.gameserver.model.interfaces.IIdentifiable;
import com.l2jserver.gameserver.model.skills.funcs.Func;
import com.l2jserver.gameserver.model.skills.funcs.FuncTemplate;
import com.l2jserver.gameserver.model.skills.targets.L2TargetType;
import com.l2jserver.gameserver.model.stats.BaseStats;
import com.l2jserver.gameserver.model.stats.Env;
import com.l2jserver.gameserver.model.stats.Formulas;
import com.l2jserver.gameserver.model.stats.TraitType;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.serverpackets.FlyToLocation.FlyType;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Util;
import com.l2jserver.util.Rnd;

public final class Skill implements IChanceSkillTrigger, IIdentifiable
{
	private static final Logger _log = Logger.getLogger(Skill.class.getName());
	
	private static final L2Object[] EMPTY_TARGET_LIST = new L2Object[0];
	
	public static final int SKILL_CUBIC_MASTERY = 143;
	public static final int SKILL_CREATE_DWARVEN = 172;
	public static final int SKILL_LUCKY = 194;
	public static final int SKILL_EXPERTISE = 239;
	public static final int SKILL_CRYSTALLIZE = 248;
	public static final int SKILL_CLAN_LUCK = 390;
	public static final int SKILL_ONYX_BEAST_TRANSFORMATION = 617;
	public static final int SKILL_CREATE_COMMON = 1320;
	public static final int SKILL_DIVINE_INSPIRATION = 1405;
	public static final int SKILL_SERVITOR_SHARE = 1557;
	public static final int SKILL_CARAVANS_SECRET_MEDICINE = 2341;
	public static final int SKILL_NPC_RACE = 4416;
	
	/** Skill ID. */
	private final int _id;
	/** Skill level. */
	private final int _level;
	/** Custom skill ID displayed by the client. */
	private final int _displayId;
	/** Custom skill level displayed by the client. */
	private final int _displayLevel;
	/** Skill client's name. */
	private final String _name;
	/** Operative type: passive, active, toggle. */
	private final SkillOperateType _operateType;
	private final int _magic;
	private final TraitType _traitType;
	private final boolean _staticReuse;
	/** MP consumption. */
	private final int _mpConsume;
	/** Initial MP consumption. */
	private final int _mpInitialConsume;
	/** MP consumption per channeling. */
	private final int _mpPerChanneling;
	/** HP consumption. */
	private final int _hpConsume;
	/** Amount of items consumed by this skill from caster. */
	private final int _itemConsumeCount;
	/** Id of item consumed by this skill from caster. */
	private final int _itemConsumeId;
	/** Cast range: how far can be the target. */
	private final int _castRange;
	/** Effect range: how far the skill affect the target. */
	private final int _effectRange;
	/** Abnormal instant, used for herbs mostly. */
	private final boolean _isAbnormalInstant;
	/** Abnormal level, global effect level. */
	private final int _abnormalLvl;
	/** Abnormal type: global effect "group". */
	private final AbnormalType _abnormalType;
	/** Abnormal time: global effect duration time. */
	private final int _abnormalTime;
	/** Abnormal visual effect: the visual effect displayed ingame. */
	private AbnormalVisualEffect[] _abnormalVisualEffects = null;
	/** Abnormal visual effect special: the visual effect displayed ingame. */
	private AbnormalVisualEffect[] _abnormalVisualEffectsSpecial = null;
	/** Abnormal visual effect event: the visual effect displayed ingame. */
	private AbnormalVisualEffect[] _abnormalVisualEffectsEvent = null;
	/** If {@code true} this skill's effect should stay after death. */
	private final boolean _stayAfterDeath;
	/** If {@code true} this skill's effect should stay after class-subclass change. */
	private final boolean _stayOnSubclassChange;
	
	private final int _refId;
	// all times in milliseconds
	private final int _hitTime;
	// private final int _skillInterruptTime;
	private final int _coolTime;
	private final int _reuseHashCode;
	private final int _reuseDelay;
	
	/** Target type of the skill : SELF, PARTY, CLAN, PET... */
	private final L2TargetType _targetType;
	private final int _feed;
	// base success chance
	private final double _power;
	private final double _pvpPower;
	private final double _pvePower;
	private final int _magicLevel;
	private final int _lvlBonusRate;
	private final int _activateRate;
	private final int _minChance;
	private final int _maxChance;
	private final int _blowChance;
	
	// Effecting area of the skill, in radius.
	// The radius center varies according to the _targetType:
	// "caster" if targetType = AURA/PARTY/CLAN or "target" if targetType = AREA
	private final int _affectRange;
	private final int[] _affectLimit = new int[2];
	
	private final boolean _nextActionIsAttack;
	
	private final boolean _removedOnAnyActionExceptMove;
	private final boolean _removedOnDamage;
	
	private final byte _element;
	private final int _elementPower;
	
	private final BaseStats _basicProperty;
	
	private final boolean _overhit;
	
	private final int _minPledgeClass;
	private final int _chargeConsume;
	private final int _triggeredId;
	private final int _triggeredLevel;
	private final String _chanceType;
	private final int _soulMaxConsume;
	private final boolean _dependOnTargetBuff;
	
	private final boolean _isHeroSkill; // If true the skill is a Hero Skill
	private final boolean _isGMSkill; // True if skill is GM skill
	private final boolean _isSevenSigns;
	
	private final int _baseCritRate; // percent of success for skill critical hit (especially for PhysicalAttack & Blow - they're not affected by rCrit values or buffs).
	private final boolean _directHpDmg; // If true then damage is being make directly
	private final boolean _isTriggeredSkill; // If true the skill will take activation buff slot instead of a normal buff slot
	private final int _effectPoint;
	// Condition lists
	private List<Condition> _preCondition;
	private List<Condition> _itemPreCondition;
	// Function lists
	private List<FuncTemplate> _funcTemplates;
	
	private final Map<EffectScope, List<AbstractEffect>> _effectLists = new EnumMap<>(EffectScope.class);
	
	protected ChanceCondition _chanceCondition = null;
	
	// Flying support
	private final FlyType _flyType;
	private final int _flyRadius;
	private final float _flyCourse;
	
	private final boolean _isDebuff;
	
	private final String _attribute;
	
	private final boolean _ignoreShield;
	
	private final boolean _isSuicideAttack;
	private final boolean _canBeDispeled;
	
	private final boolean _isClanSkill;
	private final boolean _excludedFromCheck;
	private final boolean _simultaneousCast;
	
	private L2ExtractableSkill _extractableItems = null;
	
	private final String _icon;
	
	private Byte[] _effectTypes;
	
	// Channeling data
	private final int _channelingSkillId;
	private final int _channelingTickInitialDelay;
	private final int _channelingTickInterval;
	
	public Skill(StatsSet set)
	{
		_id = set.getInt("skill_id");
		_level = set.getInt("level");
		_refId = set.getInt("referenceId", 0);
		_displayId = set.getInt("displayId", _id);
		_displayLevel = set.getInt("displayLevel", _level);
		_name = set.getString("name", "");
		_operateType = set.getEnum("operateType", SkillOperateType.class);
		_magic = set.getInt("isMagic", 0);
		_traitType = set.getEnum("trait", TraitType.class, TraitType.NONE);
		_staticReuse = set.getBoolean("staticReuse", false);
		_mpConsume = set.getInt("mpConsume", 0);
		_mpInitialConsume = set.getInt("mpInitialConsume", 0);
		_mpPerChanneling = set.getInt("mpPerChanneling", _mpConsume);
		_hpConsume = set.getInt("hpConsume", 0);
		_itemConsumeCount = set.getInt("itemConsumeCount", 0);
		_itemConsumeId = set.getInt("itemConsumeId", 0);
		
		_castRange = set.getInt("castRange", -1);
		_effectRange = set.getInt("effectRange", -1);
		_abnormalLvl = set.getInt("abnormalLvl", 0);
		_abnormalType = set.getEnum("abnormalType", AbnormalType.class, AbnormalType.NONE);
		
		int abnormalTime = set.getInt("abnormalTime", 0);
		if (Config.ENABLE_MODIFY_SKILL_DURATION && Config.SKILL_DURATION_LIST.containsKey(getId()))
		{
			if ((getLevel() < 100) || (getLevel() > 140))
			{
				abnormalTime = Config.SKILL_DURATION_LIST.get(getId());
			}
			else if ((getLevel() >= 100) && (getLevel() < 140))
			{
				abnormalTime += Config.SKILL_DURATION_LIST.get(getId());
			}
		}
		
		_abnormalTime = abnormalTime;
		_isAbnormalInstant = set.getBoolean("abnormalInstant", false);
		parseAbnormalVisualEffect(set.getString("abnormalVisualEffect", null));
		
		_attribute = set.getString("attribute", "");
		
		_stayAfterDeath = set.getBoolean("stayAfterDeath", false);
		_stayOnSubclassChange = set.getBoolean("stayOnSubclassChange", true);
		
		_hitTime = set.getInt("hitTime", 0);
		_coolTime = set.getInt("coolTime", 0);
		_isDebuff = set.getBoolean("isDebuff", false);
		_feed = set.getInt("feed", 0);
		_reuseHashCode = SkillData.getSkillHashCode(_id, _level);
		
		if (Config.ENABLE_MODIFY_SKILL_REUSE && Config.SKILL_REUSE_LIST.containsKey(_id))
		{
			if (Config.DEBUG)
			{
				_log.info("*** Skill " + _name + " (" + _level + ") changed reuse from " + set.getInt("reuseDelay", 0) + " to " + Config.SKILL_REUSE_LIST.get(_id) + " seconds.");
			}
			_reuseDelay = Config.SKILL_REUSE_LIST.get(_id);
		}
		else
		{
			_reuseDelay = set.getInt("reuseDelay", 0);
		}
		
		_affectRange = set.getInt("affectRange", 0);
		
		final String affectLimit = set.getString("affectLimit", null);
		if (affectLimit != null)
		{
			try
			{
				String[] valuesSplit = affectLimit.split("-");
				_affectLimit[0] = Integer.parseInt(valuesSplit[0]);
				_affectLimit[1] = Integer.parseInt(valuesSplit[1]);
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("SkillId: " + _id + " invalid affectLimit value: " + affectLimit + ", \"percent-percent\" required");
			}
		}
		
		_targetType = set.getEnum("targetType", L2TargetType.class);
		_power = set.getFloat("power", 0.f);
		_pvpPower = set.getFloat("pvpPower", (float) getPower());
		_pvePower = set.getFloat("pvePower", (float) getPower());
		_magicLevel = set.getInt("magicLvl", 0);
		_lvlBonusRate = set.getInt("lvlBonusRate", 0);
		_activateRate = set.getInt("activateRate", -1);
		_minChance = set.getInt("minChance", Config.MIN_ABNORMAL_STATE_SUCCESS_RATE);
		_maxChance = set.getInt("maxChance", Config.MAX_ABNORMAL_STATE_SUCCESS_RATE);
		_ignoreShield = set.getBoolean("ignoreShld", false);
		
		_nextActionIsAttack = set.getBoolean("nextActionAttack", false);
		
		_removedOnAnyActionExceptMove = set.getBoolean("removedOnAnyActionExceptMove", false);
		_removedOnDamage = set.getBoolean("removedOnDamage", false);
		
		_element = set.getByte("element", (byte) -1);
		_elementPower = set.getInt("elementPower", 0);
		
		_basicProperty = set.getEnum("basicProperty", BaseStats.class, BaseStats.NONE);
		
		_overhit = set.getBoolean("overHit", false);
		_isSuicideAttack = set.getBoolean("isSuicideAttack", false);
		
		_minPledgeClass = set.getInt("minPledgeClass", 0);
		_chargeConsume = set.getInt("chargeConsume", 0);
		_triggeredId = set.getInt("triggeredId", 0);
		_triggeredLevel = set.getInt("triggeredLevel", 1);
		_chanceType = set.getString("chanceType", "");
		if (!_chanceType.isEmpty())
		{
			_chanceCondition = ChanceCondition.parse(set);
		}
		
		_soulMaxConsume = set.getInt("soulMaxConsumeCount", 0);
		_blowChance = set.getInt("blowChance", 0);
		
		_isHeroSkill = SkillTreesData.getInstance().isHeroSkill(_id, _level);
		_isGMSkill = SkillTreesData.getInstance().isGMSkill(_id, _level);
		_isSevenSigns = (_id > 4360) && (_id < 4367);
		_isClanSkill = SkillTreesData.getInstance().isClanSkill(_id, _level);
		
		_baseCritRate = set.getInt("baseCritRate", 0);
		_directHpDmg = set.getBoolean("dmgDirectlyToHp", false);
		_isTriggeredSkill = set.getBoolean("isTriggeredSkill", false);
		_effectPoint = set.getInt("effectPoint", 0);
		
		_flyType = set.getEnum("flyType", FlyType.class, null);
		_flyRadius = set.getInt("flyRadius", 0);
		_flyCourse = set.getFloat("flyCourse", 0);
		
		_canBeDispeled = set.getBoolean("canBeDispeled", true);
		
		_excludedFromCheck = set.getBoolean("excludedFromCheck", false);
		_dependOnTargetBuff = set.getBoolean("dependOnTargetBuff", false);
		_simultaneousCast = set.getBoolean("simultaneousCast", false);
		
		String capsuled_items = set.getString("capsuled_items_skill", null);
		if (capsuled_items != null)
		{
			if (capsuled_items.isEmpty())
			{
				_log.warning("Empty Extractable Item Skill data in Skill Id: " + _id);
			}
			
			_extractableItems = parseExtractableSkill(_id, _level, capsuled_items);
		}
		
		_icon = set.getString("icon", "icon.skill0000");
		
		_channelingSkillId = set.getInt("channelingSkillId", 0);
		_channelingTickInterval = set.getInt("channelingTickInterval", 2) * 1000;
		_channelingTickInitialDelay = set.getInt("channelingTickInitialDelay", _channelingTickInterval / 1000) * 1000;
	}
	
	public TraitType getTraitType()
	{
		return _traitType;
	}
	
	public byte getElement()
	{
		return _element;
	}
	
	public int getElementPower()
	{
		return _elementPower;
	}
	
	/**
	 * Return the target type of the skill : SELF, PARTY, CLAN, PET...
	 * @return
	 */
	public L2TargetType getTargetType()
	{
		return _targetType;
	}
	
	public boolean isOverhit()
	{
		return _overhit;
	}
	
	public boolean isSuicideAttack()
	{
		return _isSuicideAttack;
	}
	
	public boolean allowOnTransform()
	{
		return isPassive();
	}
	
	/**
	 * Return the power of the skill.
	 * @param activeChar
	 * @param target
	 * @param isPvP
	 * @param isPvE
	 * @return
	 */
	public double getPower(L2Character activeChar, L2Character target, boolean isPvP, boolean isPvE)
	{
		if (activeChar == null)
		{
			return getPower(isPvP, isPvE);
		}
		
		if (hasEffectType(L2EffectType.DEATH_LINK))
		{
			return getPower(isPvP, isPvE) * (-((activeChar.getCurrentHp() * 2) / activeChar.getMaxHp()) + 2);
		}
		
		if (hasEffectType(L2EffectType.PHYSICAL_ATTACK_HP_LINK))
		{
			return getPower(isPvP, isPvE) * (-((target.getCurrentHp() * 2) / target.getMaxHp()) + 2);
		}
		return getPower(isPvP, isPvE);
	}
	
	public double getPower()
	{
		return _power;
	}
	
	public double getPower(boolean isPvP, boolean isPvE)
	{
		return isPvE ? _pvePower : isPvP ? _pvpPower : _power;
	}
	
	/**
	 * Verify if this skill is abnormal instant.<br>
	 * Herb buff skills yield {@code true} for this check.
	 * @return {@code true} if the skill is abnormal instant, {@code false} otherwise
	 */
	public boolean isAbnormalInstant()
	{
		return _isAbnormalInstant;
	}
	
	/**
	 * Gets the skill abnormal type.
	 * @return the abnormal type
	 */
	public AbnormalType getAbnormalType()
	{
		return _abnormalType;
	}
	
	/**
	 * Gets the skill abnormal level.
	 * @return the skill abnormal level
	 */
	public int getAbnormalLvl()
	{
		return _abnormalLvl;
	}
	
	/**
	 * Gets the skill abnormal time.<br>
	 * Is the base to calculate the duration of the continuous effects of this skill.
	 * @return the abnormal time
	 */
	public int getAbnormalTime()
	{
		return _abnormalTime;
	}
	
	/**
	 * Gets the skill abnormal visual effect.
	 * @return the abnormal visual effect
	 */
	public AbnormalVisualEffect[] getAbnormalVisualEffects()
	{
		return _abnormalVisualEffects;
	}
	
	/**
	 * Verify if the skill has abnormal visual effects.
	 * @return {@code true} if the skill has abnormal visual effects, {@code false} otherwise
	 */
	public boolean hasAbnormalVisualEffects()
	{
		return (_abnormalVisualEffects != null) && (_abnormalVisualEffects.length > 0);
	}
	
	/**
	 * Gets the skill special abnormal visual effect.
	 * @return the abnormal visual effect
	 */
	public AbnormalVisualEffect[] getAbnormalVisualEffectsSpecial()
	{
		return _abnormalVisualEffectsSpecial;
	}
	
	/**
	 * Verify if the skill has special abnormal visual effects.
	 * @return {@code true} if the skill has special abnormal visual effects, {@code false} otherwise
	 */
	public boolean hasAbnormalVisualEffectsSpecial()
	{
		return (_abnormalVisualEffectsSpecial != null) && (_abnormalVisualEffectsSpecial.length > 0);
	}
	
	/**
	 * Gets the skill event abnormal visual effect.
	 * @return the abnormal visual effect
	 */
	public AbnormalVisualEffect[] getAbnormalVisualEffectsEvent()
	{
		return _abnormalVisualEffectsEvent;
	}
	
	/**
	 * Verify if the skill has event abnormal visual effects.
	 * @return {@code true} if the skill has event abnormal visual effects, {@code false} otherwise
	 */
	public boolean hasAbnormalVisualEffectsEvent()
	{
		return (_abnormalVisualEffectsEvent != null) && (_abnormalVisualEffectsEvent.length > 0);
	}
	
	/**
	 * Gets the skill magic level.
	 * @return the skill magic level
	 */
	public int getMagicLevel()
	{
		return _magicLevel;
	}
	
	public int getLvlBonusRate()
	{
		return _lvlBonusRate;
	}
	
	public int getActivateRate()
	{
		return _activateRate;
	}
	
	/**
	 * Return custom minimum skill/effect chance.
	 * @return
	 */
	public int getMinChance()
	{
		return _minChance;
	}
	
	/**
	 * Return custom maximum skill/effect chance.
	 * @return
	 */
	public int getMaxChance()
	{
		return _maxChance;
	}
	
	/**
	 * Return true if skill effects should be removed on any action except movement
	 * @return
	 */
	public boolean isRemovedOnAnyActionExceptMove()
	{
		return _removedOnAnyActionExceptMove;
	}
	
	/**
	 * @return {@code true} if skill effects should be removed on damage
	 */
	public boolean isRemovedOnDamage()
	{
		return _removedOnDamage;
	}
	
	/**
	 * Return the additional effect Id.
	 * @return
	 */
	public int getChannelingSkillId()
	{
		return _channelingSkillId;
	}
	
	/**
	 * Return true if character should attack target after skill
	 * @return
	 */
	public boolean nextActionIsAttack()
	{
		return _nextActionIsAttack;
	}
	
	/**
	 * @return Returns the castRange.
	 */
	public int getCastRange()
	{
		return _castRange;
	}
	
	/**
	 * @return Returns the effectRange.
	 */
	public int getEffectRange()
	{
		return _effectRange;
	}
	
	/**
	 * @return Returns the hpConsume.
	 */
	public int getHpConsume()
	{
		return _hpConsume;
	}
	
	/**
	 * Gets the skill ID.
	 * @return the skill ID
	 */
	@Override
	public int getId()
	{
		return _id;
	}
	
	/**
	 * Verify if this skill is a debuff.
	 * @return {@code true} if this skill is a debuff, {@code false} otherwise
	 */
	public boolean isDebuff()
	{
		return _isDebuff;
	}
	
	public int getDisplayId()
	{
		return _displayId;
	}
	
	public int getDisplayLevel()
	{
		return _displayLevel;
	}
	
	public int getTriggeredId()
	{
		return _triggeredId;
	}
	
	public int getTriggeredLevel()
	{
		return _triggeredLevel;
	}
	
	public boolean triggerAnotherSkill()
	{
		return _triggeredId > 1;
	}
	
	/**
	 * Return skill basicProperty base stat (STR, INT ...).
	 * @return
	 */
	public BaseStats getBasicProperty()
	{
		return _basicProperty;
	}
	
	/**
	 * @return Returns the itemConsume.
	 */
	public int getItemConsume()
	{
		return _itemConsumeCount;
	}
	
	/**
	 * @return Returns the itemConsumeId.
	 */
	public int getItemConsumeId()
	{
		return _itemConsumeId;
	}
	
	/**
	 * @return Returns the level.
	 */
	public int getLevel()
	{
		return _level;
	}
	
	/**
	 * @return Returns true to set physical skills.
	 */
	public boolean isPhysical()
	{
		return _magic == 0;
	}
	
	/**
	 * @return Returns true to set magic skills.
	 */
	public boolean isMagic()
	{
		return _magic == 1;
	}
	
	/**
	 * @return Returns true to set static skills.
	 */
	public boolean isStatic()
	{
		return _magic == 2;
	}
	
	/**
	 * @return Returns true to set dance skills.
	 */
	public boolean isDance()
	{
		return _magic == 3;
	}
	
	/**
	 * @return Returns true to set static reuse.
	 */
	public boolean isStaticReuse()
	{
		return _staticReuse;
	}
	
	/**
	 * @return Returns the mpConsume.
	 */
	public int getMpConsume()
	{
		return _mpConsume;
	}
	
	/**
	 * @return Returns the mpInitialConsume.
	 */
	public int getMpInitialConsume()
	{
		return _mpInitialConsume;
	}
	
	/**
	 * @return Mana consumption per channeling tick.
	 */
	public int getMpPerChanneling()
	{
		return _mpPerChanneling;
	}
	
	/**
	 * @return the skill name
	 */
	public String getName()
	{
		return _name;
	}
	
	/**
	 * @return the reuse delay
	 */
	public int getReuseDelay()
	{
		return _reuseDelay;
	}
	
	public int getReuseHashCode()
	{
		return _reuseHashCode;
	}
	
	public int getHitTime()
	{
		return _hitTime;
	}
	
	/**
	 * @return the cool time
	 */
	public int getCoolTime()
	{
		return _coolTime;
	}
	
	public int getAffectRange()
	{
		return _affectRange;
	}
	
	public int getAffectLimit()
	{
		return (_affectLimit[0] + Rnd.get(_affectLimit[1]));
	}
	
	public boolean isActive()
	{
		return (_operateType != null) && _operateType.isActive();
	}
	
	public boolean isPassive()
	{
		return (_operateType != null) && _operateType.isPassive();
	}
	
	public boolean isToggle()
	{
		return (_operateType != null) && _operateType.isToggle();
	}
	
	public boolean isContinuous()
	{
		return (_operateType != null) && _operateType.isContinuous();
	}
	
	public boolean isChance()
	{
		return (_chanceCondition != null) && isPassive();
	}
	
	public boolean isChanneling()
	{
		return (_operateType != null) && _operateType.isChanneling();
	}
	
	public boolean isTriggeredSkill()
	{
		return _isTriggeredSkill;
	}
	
	/**
	 * Verify if the skill is a transformation skill.
	 * @return {@code true} if the skill is a transformation, {@code false} otherwise
	 */
	public boolean isTransformation()
	{
		return _abnormalType == AbnormalType.TRANSFORM;
	}
	
	public int getEffectPoint()
	{
		return _effectPoint;
	}
	
	public boolean useSoulShot()
	{
		return (hasEffectType(L2EffectType.PHYSICAL_ATTACK, L2EffectType.PHYSICAL_ATTACK_HP_LINK, L2EffectType.FATAL_BLOW, L2EffectType.ENERGY_ATTACK));
	}
	
	public boolean useSpiritShot()
	{
		return _magic == 1;
	}
	
	public boolean useFishShot()
	{
		return hasEffectType(L2EffectType.FISHING);
	}
	
	public int getMinPledgeClass()
	{
		return _minPledgeClass;
	}
	
	public boolean isHeroSkill()
	{
		return _isHeroSkill;
	}
	
	public boolean isGMSkill()
	{
		return _isGMSkill;
	}
	
	public boolean is7Signs()
	{
		return _isSevenSigns;
	}
	
	/**
	 * Verify if this is a healing potion skill.
	 * @return {@code true} if this is a healing potion skill, {@code false} otherwise
	 */
	public boolean isHealingPotionSkill()
	{
		switch (getId())
		{
			case 2031: // Lesser Healing Potion
			case 2032: // Healing Potion
			case 2037: // Greater Healing Potion
			case 2863: // Highest Power Healing Potion
			case 26025: // Powerful Healing Potion
			case 26026: // High-grade Healing Potion
			{
				return true;
			}
		}
		return false;
	}
	
	public int getChargeConsume()
	{
		return _chargeConsume;
	}
	
	public int getMaxSoulConsumeCount()
	{
		return _soulMaxConsume;
	}
	
	public int getBaseCritRate()
	{
		return _baseCritRate;
	}
	
	public boolean getDmgDirectlyToHP()
	{
		return _directHpDmg;
	}
	
	public FlyType getFlyType()
	{
		return _flyType;
	}
	
	public int getFlyRadius()
	{
		return _flyRadius;
	}
	
	public float getFlyCourse()
	{
		return _flyCourse;
	}
	
	public boolean isStayAfterDeath()
	{
		return _stayAfterDeath;
	}
	
	public boolean isStayOnSubclassChange()
	{
		return _stayOnSubclassChange;
	}
	
	public boolean isBad()
	{
		return _effectPoint < 0;
	}
	
	public boolean checkCondition(L2Character activeChar, L2Object target, boolean itemOrWeapon)
	{
		if (activeChar.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) && !Config.GM_SKILL_RESTRICTION)
		{
			return true;
		}
		
		final List<Condition> preCondition = itemOrWeapon ? _itemPreCondition : _preCondition;
		if ((preCondition == null) || preCondition.isEmpty())
		{
			return true;
		}
		
		final Env env = new Env();
		env.setCharacter(activeChar);
		if (target instanceof L2Character)
		{
			env.setTarget((L2Character) target);
		}
		env.setSkill(this);
		
		for (Condition cond : preCondition)
		{
			if (!cond.test(env))
			{
				final String msg = cond.getMessage();
				final int msgId = cond.getMessageId();
				if (msgId != 0)
				{
					final SystemMessage sm = SystemMessage.getSystemMessage(msgId);
					if (cond.isAddName())
					{
						sm.addSkillName(_id);
					}
					activeChar.sendPacket(sm);
				}
				else if (msg != null)
				{
					activeChar.sendMessage(msg);
				}
				return false;
			}
		}
		return true;
	}
	
	public L2Object[] getTargetList(L2Character activeChar, boolean onlyFirst)
	{
		// Init to null the target of the skill
		L2Character target = null;
		
		// Get the L2Objcet targeted by the user of the skill at this moment
		L2Object objTarget = activeChar.getTarget();
		// If the L2Object targeted is a L2Character, it becomes the L2Character target
		if (objTarget instanceof L2Character)
		{
			target = (L2Character) objTarget;
		}
		
		return getTargetList(activeChar, onlyFirst, target);
	}
	
	/**
	 * Return all targets of the skill in a table in function a the skill type.<br>
	 * <B><U>Values of skill type</U>:</B>
	 * <ul>
	 * <li>ONE : The skill can only be used on the L2PcInstance targeted, or on the caster if it's a L2PcInstance and no L2PcInstance targeted</li>
	 * <li>SELF</li>
	 * <li>HOLY, UNDEAD</li>
	 * <li>PET</li>
	 * <li>AURA, AURA_CLOSE</li>
	 * <li>AREA</li>
	 * <li>MULTIFACE</li>
	 * <li>PARTY, CLAN</li>
	 * <li>CORPSE_PLAYER, CORPSE_MOB, CORPSE_CLAN</li>
	 * <li>UNLOCKABLE</li>
	 * <li>ITEM</li>
	 * <ul>
	 * @param activeChar The L2Character who use the skill
	 * @param onlyFirst
	 * @param target
	 * @return
	 */
	public L2Object[] getTargetList(L2Character activeChar, boolean onlyFirst, L2Character target)
	{
		final ITargetTypeHandler handler = TargetHandler.getInstance().getHandler(getTargetType());
		if (handler != null)
		{
			try
			{
				return handler.getTargetList(this, activeChar, onlyFirst, target);
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Exception in L2Skill.getTargetList(): " + e.getMessage(), e);
			}
		}
		activeChar.sendMessage("Target type of skill is not currently handled.");
		return EMPTY_TARGET_LIST;
	}
	
	public L2Object[] getTargetList(L2Character activeChar)
	{
		return getTargetList(activeChar, false);
	}
	
	public L2Object getFirstOfTargetList(L2Character activeChar)
	{
		L2Object[] targets = getTargetList(activeChar, true);
		if (targets.length == 0)
		{
			return null;
		}
		return targets[0];
	}
	
	/**
	 * Check if should be target added to the target list false if target is dead, target same as caster,<br>
	 * target inside peace zone, target in the same party with caster, caster can see target Additional checks if not in PvP zones (arena, siege):<br>
	 * target in not the same clan and alliance with caster, and usual skill PvP check. If TvT event is active - performing additional checks. Caution: distance is not checked.
	 * @param caster
	 * @param target
	 * @param skill
	 * @param sourceInArena
	 * @return
	 */
	public static final boolean checkForAreaOffensiveSkills(L2Character caster, L2Character target, Skill skill, boolean sourceInArena)
	{
		if ((target == null) || target.isDead() || (target == caster))
		{
			return false;
		}
		
		final L2PcInstance player = caster.getActingPlayer();
		final L2PcInstance targetPlayer = target.getActingPlayer();
		if (player != null)
		{
			if (targetPlayer != null)
			{
				if ((targetPlayer == caster) || (targetPlayer == player))
				{
					return false;
				}
				
				if (targetPlayer.inObserverMode())
				{
					return false;
				}
				
				if (skill.isBad() && (player.getSiegeState() > 0) && player.isInsideZone(ZoneId.SIEGE) && (player.getSiegeState() == targetPlayer.getSiegeState()) && (player.getSiegeSide() == targetPlayer.getSiegeSide()))
				{
					return false;
				}
				
				if (skill.isBad() && target.isInsideZone(ZoneId.PEACE))
				{
					return false;
				}
				
				if (player.isInParty() && targetPlayer.isInParty())
				{
					// Same party
					if (player.getParty().getLeaderObjectId() == targetPlayer.getParty().getLeaderObjectId())
					{
						return false;
					}
					
					// Same command channel
					if (player.getParty().isInCommandChannel() && (player.getParty().getCommandChannel() == targetPlayer.getParty().getCommandChannel()))
					{
						return false;
					}
				}
				
				if (!TvTEvent.checkForTvTSkill(player, targetPlayer, skill))
				{
					return false;
				}
				
				if (!sourceInArena && !(targetPlayer.isInsideZone(ZoneId.PVP) && !targetPlayer.isInsideZone(ZoneId.SIEGE)))
				{
					if ((player.getAllyId() != 0) && (player.getAllyId() == targetPlayer.getAllyId()))
					{
						return false;
					}
					
					if ((player.getClanId() != 0) && (player.getClanId() == targetPlayer.getClanId()))
					{
						return false;
					}
					
					if (!player.checkPvpSkill(targetPlayer, skill, (caster instanceof L2Summon)))
					{
						return false;
					}
				}
			}
		}
		else
		{
			// target is mob
			if ((targetPlayer == null) && (target instanceof L2Attackable) && (caster instanceof L2Attackable))
			{
				if (((L2Attackable) caster).isInEnemyClan((L2Attackable) target))
				{
					return false;
				}
			}
		}
		
		if ((Config.GEODATA > 0) && !GeoData.getInstance().canSeeTarget(caster, target))
		{
			return false;
		}
		return true;
	}
	
	public static final boolean addSummon(L2Character caster, L2PcInstance owner, int radius, boolean isDead)
	{
		if (!owner.hasSummon())
		{
			return false;
		}
		return addCharacter(caster, owner.getSummon(), radius, isDead);
	}
	
	public static final boolean addCharacter(L2Character caster, L2Character target, int radius, boolean isDead)
	{
		if (isDead != target.isDead())
		{
			return false;
		}
		
		if ((radius > 0) && !Util.checkIfInRange(radius, caster, target, true))
		{
			return false;
		}
		return true;
	}
	
	public List<Func> getStatFuncs(AbstractEffect effect, L2Character player)
	{
		if (_funcTemplates == null)
		{
			return Collections.<Func> emptyList();
		}
		
		if (!(player instanceof L2Playable) && !(player instanceof L2Attackable))
		{
			return Collections.<Func> emptyList();
		}
		
		final List<Func> funcs = new ArrayList<>(_funcTemplates.size());
		final Env env = new Env();
		env.setCharacter(player);
		env.setSkill(this);
		for (FuncTemplate t : _funcTemplates)
		{
			Func f = t.getFunc(env, this); // skill is owner
			if (f != null)
			{
				funcs.add(f);
			}
		}
		return funcs;
	}
	
	/**
	 * Gets the skill effects.
	 * @param effectScope the effect scope
	 * @return the list of effects for the give scope
	 */
	public List<AbstractEffect> getEffects(EffectScope effectScope)
	{
		return _effectLists.get(effectScope);
	}
	
	/**
	 * Verify if this skill has effects for the given scope.
	 * @param effectScope the effect scope
	 * @return {@code true} if this skill has effects for the given scope, {@code false} otherwise
	 */
	public boolean hasEffects(EffectScope effectScope)
	{
		List<AbstractEffect> effects = _effectLists.get(effectScope);
		return (effects != null) && !effects.isEmpty();
	}
	
	/**
	 * Applies the effects from this skill to the target for the given effect scope.
	 * @param effectScope the effect scope
	 * @param info the buff info
	 * @param applyInstantEffects if {@code true} instant effects will be applied to the effected
	 * @param addContinuousEffects if {@code true} continuous effects will be applied to the effected
	 */
	public void applyEffectScope(EffectScope effectScope, BuffInfo info, boolean applyInstantEffects, boolean addContinuousEffects)
	{
		if ((effectScope != null) && hasEffects(effectScope))
		{
			for (AbstractEffect effect : getEffects(effectScope))
			{
				if (effect != null)
				{
					if (effect.isInstant())
					{
						if (applyInstantEffects && effect.calcSuccess(info))
						{
							effect.onStart(info);
						}
					}
					else if (addContinuousEffects)
					{
						if (effect.canStart(info))
						{
							info.addEffect(effect);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Method overload for {@link Skill#applyEffects(L2Character, L2CubicInstance, L2Character, boolean, boolean, boolean, int)}.<br>
	 * Simplify the calls.
	 * @param effector the caster of the skill
	 * @param effected the target of the effect
	 */
	public void applyEffects(L2Character effector, L2Character effected)
	{
		applyEffects(effector, null, effected, false, false, true, 0);
	}
	
	/**
	 * Method overload for {@link Skill#applyEffects(L2Character, L2CubicInstance, L2Character, boolean, boolean, boolean, int)}.<br>
	 * Simplify the calls, allowing abnormal time time customization.
	 * @param effector the caster of the skill
	 * @param effected the target of the effect
	 * @param instant if {@code true} instant effects will be applied to the effected
	 * @param abnormalTime custom abnormal time, if equal or lesser than zero will be ignored
	 */
	public void applyEffects(L2Character effector, L2Character effected, boolean instant, int abnormalTime)
	{
		applyEffects(effector, null, effected, false, false, instant, abnormalTime);
	}
	
	/**
	 * Applies the effects from this skill to the target.
	 * @param effector the caster of the skill
	 * @param cubic the cubic that cast the skill, can be {@code null}
	 * @param effected the target of the effect
	 * @param self if {@code true} self-effects will be casted on the caster
	 * @param passive if {@code true} passive effects will be applied to the effector
	 * @param instant if {@code true} instant effects will be applied to the effected
	 * @param abnormalTime custom abnormal time, if equal or lesser than zero will be ignored
	 */
	public void applyEffects(L2Character effector, L2CubicInstance cubic, L2Character effected, boolean self, boolean passive, boolean instant, int abnormalTime)
	{
		// null targets cannot receive any effects.
		if (effected == null)
		{
			return;
		}
		
		// Check bad skills against target.
		if ((effector != effected) && isBad() && (effected.isInvul() || (effector.isGM() && !effector.getAccessLevel().canGiveDamage())))
		{
			return;
		}
		
		if (effected.isInvulAgainst(getId(), getLevel()))
		{
			effected.sendDebugMessage("Skill " + toString() + " has been ignored (invul against)");
			return;
		}
		
		final Env env = new Env();
		env.setSkillMastery(Formulas.calcSkillMastery(effector, this));
		env.setCharacter(effector);
		env.setCubic(cubic);
		env.setTarget(effected);
		env.setSkill(this);
		final boolean addContinuousEffects = !passive && (_operateType.isToggle() || (_operateType.isContinuous() && Formulas.calcEffectSuccess(env)));
		if (!self && !passive)
		{
			final BuffInfo info = new BuffInfo(env);
			if (addContinuousEffects && (abnormalTime > 0))
			{
				info.setAbnormalTime(abnormalTime);
			}
			
			applyEffectScope(EffectScope.GENERAL, info, instant, addContinuousEffects);
			
			EffectScope pvpOrPveEffectScope = effector.isPlayable() && effected.isAttackable() ? EffectScope.PVE : effector.isPlayable() && effected.isPlayable() ? EffectScope.PVP : null;
			applyEffectScope(pvpOrPveEffectScope, info, instant, addContinuousEffects);
			
			applyEffectScope(EffectScope.CHANNELING, info, instant, addContinuousEffects);
			
			if (addContinuousEffects)
			{
				effected.getEffectList().add(info);
			}
			
			// Support for buff sharing feature.
			if (addContinuousEffects && effected.isPlayer() && effected.hasServitor() && isContinuous() && !isDebuff())
			{
				applyEffects(effector, effected.getSummon(), false, 0);
			}
		}
		
		if (self)
		{
			env.setTarget(effector);
			final BuffInfo info = new BuffInfo(env);
			if (addContinuousEffects && (abnormalTime > 0))
			{
				info.setAbnormalTime(abnormalTime);
			}
			
			applyEffectScope(EffectScope.SELF, info, instant, addContinuousEffects);
			
			if (addContinuousEffects)
			{
				effector.getEffectList().add(info);
			}
			
			// Support for buff sharing feature.
			// Avoiding Servitor Share since it's implementation already "shares" the effect.
			if (addContinuousEffects && effected.isPlayer() && effected.hasServitor() && isContinuous() && !isDebuff() && (getId() != SKILL_SERVITOR_SHARE))
			{
				applyEffects(effector, effected.getSummon(), false, 0);
			}
		}
		
		if (passive)
		{
			env.setTarget(effector);
			final BuffInfo info = new BuffInfo(env);
			applyEffectScope(EffectScope.PASSIVE, info, false, true);
			effector.getEffectList().add(info);
		}
	}
	
	/**
	 * Activates the skill to the targets.
	 * @param caster the caster
	 * @param targets the targets
	 */
	public void activateSkill(L2Character caster, L2Object[] targets)
	{
		switch (getId())
		{
		// TODO: replace with AI
			case 5852:
			case 5853:
			{
				final L2BlockInstance block = targets[0] instanceof L2BlockInstance ? (L2BlockInstance) targets[0] : null;
				final L2PcInstance player = caster.isPlayer() ? (L2PcInstance) caster : null;
				if ((block == null) || (player == null))
				{
					return;
				}
				
				final int arena = player.getBlockCheckerArena();
				if (arena != -1)
				{
					final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(arena);
					if (holder == null)
					{
						return;
					}
					
					final int team = holder.getPlayerTeam(player);
					final int color = block.getColorEffect();
					if ((team == 0) && (color == 0x00))
					{
						block.changeColor(player, holder, team);
					}
					else if ((team == 1) && (color == 0x53))
					{
						block.changeColor(player, holder, team);
					}
				}
				break;
			}
			default:
			{
				for (L2Character target : (L2Character[]) targets)
				{
					if (Formulas.calcBuffDebuffReflection(target, this))
					{
						applyEffects(target, caster);
					}
					else
					{
						applyEffects(caster, target);
					}
				}
				break;
			}
		}
		
		// Self Effect
		if (hasEffects(EffectScope.SELF))
		{
			if (caster.isAffectedBySkill(getId()))
			{
				caster.stopSkillEffects(true, getId());
			}
			applyEffects(caster, null, caster, true, false, true, 0);
		}
		
		if (useSpiritShot())
		{
			caster.setChargedShot(caster.isChargedShot(ShotType.BLESSED_SPIRITSHOTS) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS, false);
		}
		else
		{
			caster.setChargedShot(ShotType.SOULSHOTS, false);
		}
	}
	
	public void attach(FuncTemplate f)
	{
		if (_funcTemplates == null)
		{
			_funcTemplates = new ArrayList<>(1);
		}
		_funcTemplates.add(f);
	}
	
	/**
	 * Adds an effect to the effect list for the give effect scope.
	 * @param effectScope the effect scope
	 * @param effect the effect to add
	 */
	public void addEffect(EffectScope effectScope, AbstractEffect effect)
	{
		List<AbstractEffect> effects = _effectLists.get(effectScope);
		if (effects == null)
		{
			effects = new ArrayList<>(1);
			_effectLists.put(effectScope, effects);
		}
		effects.add(effect);
	}
	
	public void attach(Condition c, boolean itemOrWeapon)
	{
		if (itemOrWeapon)
		{
			if (_itemPreCondition == null)
			{
				_itemPreCondition = new ArrayList<>();
			}
			_itemPreCondition.add(c);
		}
		else
		{
			if (_preCondition == null)
			{
				_preCondition = new ArrayList<>();
			}
			_preCondition.add(c);
		}
	}
	
	@Override
	public String toString()
	{
		return "Skill " + _name + "(" + _id + "," + _level + ")";
	}
	
	/**
	 * @return pet food
	 */
	public int getFeed()
	{
		return _feed;
	}
	
	/**
	 * used for tracking item id in case that item consume cannot be used
	 * @return reference item id
	 */
	public int getReferenceItemId()
	{
		return _refId;
	}
	
	@Override
	public boolean triggersChanceSkill()
	{
		return (_triggeredId > 0) && isChance();
	}
	
	@Override
	public int getTriggeredChanceId()
	{
		return _triggeredId;
	}
	
	@Override
	public int getTriggeredChanceLevel()
	{
		return _triggeredLevel;
	}
	
	@Override
	public ChanceCondition getTriggeredChanceCondition()
	{
		return _chanceCondition;
	}
	
	public String getAttributeName()
	{
		return _attribute;
	}
	
	/**
	 * @return the _blowChance
	 */
	public int getBlowChance()
	{
		return _blowChance;
	}
	
	public boolean ignoreShield()
	{
		return _ignoreShield;
	}
	
	public boolean canBeDispeled()
	{
		return _canBeDispeled;
	}
	
	/**
	 * Verify if the skill can be stolen.
	 * @return {@code true} if skill can be stolen, {@code false} otherwise
	 */
	public boolean canBeStolen()
	{
		return !isPassive() && !isToggle() && !isDebuff() && !isHeroSkill() && !isGMSkill() && !(isStatic() && (getId() != SKILL_CARAVANS_SECRET_MEDICINE)) && canBeDispeled() && (getId() != SKILL_SERVITOR_SHARE);
	}
	
	public boolean isClanSkill()
	{
		return _isClanSkill;
	}
	
	public boolean isExcludedFromCheck()
	{
		return _excludedFromCheck;
	}
	
	public boolean getDependOnTargetBuff()
	{
		return _dependOnTargetBuff;
	}
	
	public boolean isSimultaneousCast()
	{
		return _simultaneousCast;
	}
	
	/**
	 * Parse an extractable skill.
	 * @param skillId the skill Id
	 * @param skillLvl the skill level
	 * @param values the values to parse
	 * @return the parsed extractable skill
	 * @author Zoey76
	 */
	private L2ExtractableSkill parseExtractableSkill(int skillId, int skillLvl, String values)
	{
		final String[] prodLists = values.split(";");
		final List<L2ExtractableProductItem> products = new ArrayList<>();
		String[] prodData;
		for (String prodList : prodLists)
		{
			prodData = prodList.split(",");
			if (prodData.length < 3)
			{
				_log.warning("Extractable skills data: Error in Skill Id: " + skillId + " Level: " + skillLvl + " -> wrong seperator!");
			}
			List<ItemHolder> items = null;
			double chance = 0;
			int prodId = 0;
			int quantity = 0;
			final int lenght = prodData.length - 1;
			try
			{
				items = new ArrayList<>(lenght / 2);
				for (int j = 0; j < lenght; j++)
				{
					prodId = Integer.parseInt(prodData[j]);
					quantity = Integer.parseInt(prodData[j += 1]);
					if ((prodId <= 0) || (quantity <= 0))
					{
						_log.warning("Extractable skills data: Error in Skill Id: " + skillId + " Level: " + skillLvl + " wrong production Id: " + prodId + " or wrond quantity: " + quantity + "!");
					}
					items.add(new ItemHolder(prodId, quantity));
				}
				chance = Double.parseDouble(prodData[lenght]);
			}
			catch (Exception e)
			{
				_log.warning("Extractable skills data: Error in Skill Id: " + skillId + " Level: " + skillLvl + " -> incomplete/invalid production data or wrong seperator!");
			}
			products.add(new L2ExtractableProductItem(items, chance));
		}
		
		if (products.isEmpty())
		{
			_log.warning("Extractable skills data: Error in Skill Id: " + skillId + " Level: " + skillLvl + " -> There are no production items!");
		}
		return new L2ExtractableSkill(SkillData.getSkillHashCode(skillId, skillLvl), products);
	}
	
	/**
	 * Parses all the abnormal visual effects.
	 * @param abnormalVisualEffects the abnormal visual effects list
	 */
	private void parseAbnormalVisualEffect(String abnormalVisualEffects)
	{
		if (abnormalVisualEffects != null)
		{
			final String[] data = abnormalVisualEffects.split(";");
			List<AbnormalVisualEffect> avesEvent = null;
			List<AbnormalVisualEffect> avesSpecial = null;
			List<AbnormalVisualEffect> aves = null;
			for (String ave2 : data)
			{
				final AbnormalVisualEffect ave = AbnormalVisualEffect.valueOf(ave2);
				if (ave != null)
				{
					if (ave.isEvent())
					{
						if (avesEvent == null)
						{
							avesEvent = new ArrayList<>(1);
						}
						avesEvent.add(ave);
						continue;
					}
					
					if (ave.isSpecial())
					{
						if (avesSpecial == null)
						{
							avesSpecial = new ArrayList<>(1);
						}
						avesSpecial.add(ave);
						continue;
					}
					
					if (aves == null)
					{
						aves = new ArrayList<>(1);
					}
					aves.add(ave);
				}
			}
			
			if (avesEvent != null)
			{
				_abnormalVisualEffectsEvent = avesEvent.toArray(new AbnormalVisualEffect[avesEvent.size()]);
			}
			
			if (avesSpecial != null)
			{
				_abnormalVisualEffectsSpecial = avesSpecial.toArray(new AbnormalVisualEffect[avesSpecial.size()]);
			}
			
			if (aves != null)
			{
				_abnormalVisualEffects = aves.toArray(new AbnormalVisualEffect[aves.size()]);
			}
		}
	}
	
	public L2ExtractableSkill getExtractableSkill()
	{
		return _extractableItems;
	}
	
	/**
	 * @param effectType Effect type to check if its present on this skill effects.
	 * @param effectTypes Effect types to check if are present on this skill effects.
	 * @return {@code true} if at least one of specified {@link L2EffectType} types is present on this skill effects, {@code false} otherwise.
	 */
	public boolean hasEffectType(L2EffectType effectType, L2EffectType... effectTypes)
	{
		if (_effectTypes == null)
		{
			synchronized (this)
			{
				if (_effectTypes == null)
				{
					Set<Byte> effectTypesSet = new HashSet<>();
					for (List<AbstractEffect> effectList : _effectLists.values())
					{
						if (effectList != null)
						{
							for (AbstractEffect effect : effectList)
							{
								if (effect == null)
								{
									continue;
								}
								effectTypesSet.add((byte) effect.getEffectType().ordinal());
							}
						}
					}
					
					Byte[] effectTypesArray = effectTypesSet.toArray(new Byte[effectTypesSet.size()]);
					Arrays.sort(effectTypesArray);
					_effectTypes = effectTypesArray;
				}
			}
		}
		
		if (Arrays.binarySearch(_effectTypes, (byte) effectType.ordinal()) >= 0)
		{
			return true;
		}
		
		for (L2EffectType type : effectTypes)
		{
			if (Arrays.binarySearch(_effectTypes, (byte) type.ordinal()) >= 0)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return icon of the current skill.
	 */
	public String getIcon()
	{
		return _icon;
	}
	
	public int getChannelingTickInterval()
	{
		return _channelingTickInterval;
	}
	
	public int getChannelingTickInitialDelay()
	{
		return _channelingTickInitialDelay;
	}
}