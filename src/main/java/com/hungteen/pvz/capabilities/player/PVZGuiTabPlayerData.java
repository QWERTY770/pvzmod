package com.hungteen.pvz.capabilities.player;

import java.util.HashMap;

import com.hungteen.pvz.utils.enums.Resources;

public class PVZGuiTabPlayerData{

	private static HashMap<Resources, Integer> resources = new HashMap<>(Resources.values().length);
//	private static HashMap<Plants, Integer> plantCardXp = new HashMap<Plants, Integer>(Plants.values().length);
//	private static HashMap<Plants, Integer> plantCardLevel = new HashMap<Plants, Integer>(Plants.values().length);
//	private static HashMap<Plants, Boolean> isPlantLocked = new HashMap<Plants, Boolean>(Plants.values().length);
	
	public static void setPlayerData(int type,int data)
	{
		resources.put(Resources.values()[type], data);
	}
	
	public static int getPlayerStats(Resources res)
	{
		return resources.get(res);
	}
	
//	public static int getPlayerPlantCardLvl(Plants plant)
//	{
//		return plantCardLevel.get(plant);
//	}
//	
//	public static int getPlayerPlantCardXp(Plants plant)
//	{
//		return plantCardXp.get(plant);
//	}
	
}