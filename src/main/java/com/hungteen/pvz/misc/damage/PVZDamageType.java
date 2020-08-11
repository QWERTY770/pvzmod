package com.hungteen.pvz.misc.damage;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;

public enum PVZDamageType {
	NORMAL, 
	WEAK,
	APPEASE,
	EAT,
	EXPLOSION;
//	SNOW, 
//	ICE,
//	FIRE, 
//	BUTTER, 
//	LIGHTNING,
//	MAGIC, 
//	SUN,
//	EAT,
//	DEAD, 
//	MOTAL;
	
	@Override
	public String toString() {
		switch(this) {
		case WEAK:return ChatFormatting.GRAY+I18n.format("pvz.damage_type.weak",new Object[0]);
		case APPEASE:return ChatFormatting.GREEN+I18n.format("pvz.damage_type.appease",new Object[0]);
		case EAT:return ChatFormatting.DARK_GRAY+I18n.format("pvz.damage_type.eat",new Object[0]);
		case EXPLOSION:return ChatFormatting.DARK_RED+I18n.format("pvz.damage_type.explosion",new Object[0]);
//			case SNOW:
//				return ChatFormatting.WHITE+I18n.format("pvz.DamageType.SNOW",new Object[0]);
//			case ICE:
//				return ChatFormatting.AQUA+I18n.format("pvz.DamageType.ICE",new Object[0]);
//			case FIRE:
//				return ChatFormatting.RED+I18n.format("pvz.DamageType.FIRE",new Object[0]);
//			case BUTTER:
//				return ChatFormatting.YELLOW+I18n.format("pvz.DamageType.BUTTER",new Object[0]);
//			case LIGHTNING:
//				return ChatFormatting.DARK_BLUE+I18n.format("pvz.DamageType.LIGHTNING",new Object[0]);
//			case MAGIC:
//				return ChatFormatting.LIGHT_PURPLE+I18n.format("pvz.DamageType.MAGIC",new Object[0]);
//			case SUN:
//			    return ChatFormatting.YELLOW+I18n.format("pvz.DamageType.SUN",new Object[0]);
			
//			case DEAD:
//			    return ChatFormatting.DARK_RED+I18n.format("pvz.DamageType.DEAD",new Object[0]);
//			case MOTAL:
//			    return ChatFormatting.BLACK+I18n.format("pvz.DamageType.MOTAL",new Object[0]);
		default:return ChatFormatting.WHITE+I18n.format("pvz.damageType.normal",new Object[0]);
		}
	}
	
	
}