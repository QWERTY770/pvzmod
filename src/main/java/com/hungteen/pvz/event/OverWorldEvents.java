package com.hungteen.pvz.event;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.hungteen.pvz.PVZConfig;
import com.hungteen.pvz.register.EntitySpawnRegister;
import com.hungteen.pvz.utils.PlayerUtil;
import com.hungteen.pvz.utils.enums.Events;
import com.hungteen.pvz.utils.enums.Zombies;
import com.hungteen.pvz.world.FogManager;
import com.hungteen.pvz.world.WaveManager;
import com.hungteen.pvz.world.data.WorldEventData;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;


public class OverWorldEvents {

	private static final ITextComponent ZOMBIE_ATTACK = new TranslationTextComponent("event.pvz.zombie_attack").applyTextStyle(TextFormatting.DARK_RED);
	private static final ITextComponent ATTACK_FINISH = new TranslationTextComponent("event.pvz.attack_finish").applyTextStyle(TextFormatting.GREEN);
	
	public static void tick(TickEvent.WorldTickEvent ev) {
		World world = ev.world;
		long totalTime = world.getDayTime();
		if(world.getGameTime() < PVZConfig.COMMON_CONFIG.WorldSettings.WorldEventSettings.SafeDayLength.get() * 24000) {
			return ;// still in safe day, do not have any event. 
		}
		int dayTime = (int) (totalTime % 24000);
		switch(dayTime) {
		case 99:{
			WorldEventData data = WorldEventData.getOverWorldEventData(world);
			data.setChanged(false);
			break;
		}
		case 100:{
			WorldEventData data = WorldEventData.getOverWorldEventData(world);
			if(! data.hasChanged()) {
				data.setChanged(true);
				deactivateZombieAttackEvents(world, false);
				activateZombieAttackEvents(world);
			}
			break;
		}
		case 23899:{
			WorldEventData data = WorldEventData.getOverWorldEventData(world);
			data.setChanged(false);
			break;
		}
		case 23900:{
			WorldEventData data = WorldEventData.getOverWorldEventData(world);
			if(! data.hasChanged()) {
				data.setChanged(true);
				deactivateZombieAttackEvents(world, true);
			}
			break;
		}
		}
		WaveManager.tickWave(world, dayTime);
		FogManager.tickFog(world);
	}
	
	/**
	 * check and activate attack event,
	 * do not activate in peaceful mode.
	 */
	public static void activateZombieAttackEvents(World world) {
		if(world.getDifficulty() != Difficulty.PEACEFUL && world.rand.nextInt(100) < PVZConfig.COMMON_CONFIG.WorldSettings.WorldEventSettings.ZombieAttackChance.get()) {
		    for(PlayerEntity pl : world.getPlayers()) {
			    pl.sendMessage(ZOMBIE_ATTACK);
			    PlayerUtil.playClientSound(pl, 3);
			    WaveManager.resetPlayerWaveTime(pl);
		    }
		    //activate event
		    Events event = EntitySpawnRegister.getCurrentEventByRandom(world);
		    activateEvent(world, event);//activate event
		    if(world.rand.nextInt(PVZConfig.COMMON_CONFIG.WorldSettings.WorldEventSettings.EventChanceSettings.FogEventChance.get()) == 0) {
		    	activateEvent(world, Events.FOG);
		    }
		    //get zombie spawn list
		    WorldEventData data = WorldEventData.getOverWorldEventData(world);
		    List<Zombies> zombieList = new ArrayList<>();
		    for(Zombies zombie : Zombies.values()) {
		    	if(data.hasZombieSpawnEntry(zombie)){
		    		zombieList.add(zombie);
		    	}
		    }
		    if(PVZConfig.COMMON_CONFIG.WorldSettings.WorldEventSettings.ShowEventMessages.get()) {
		    	world.getPlayers().forEach((player) -> {
			        for(Events ev : Events.values()) {
				        if(data.hasEvent(ev)) {
					        player.sendMessage(Events.getEventText(ev));
				        }
			        }
			        String zombieInfo = "";
			        for(int i = 0; i < zombieList.size(); ++ i) {
			        	zombieInfo += new TranslationTextComponent("entity.pvz." + zombieList.get(i).toString().toLowerCase()).getFormattedText()
			        			+ (i == zombieList.size() - 1 ? "" : ",");
			        }
			        player.sendMessage(new StringTextComponent(zombieInfo));
			    });
		    }
		}
	}
	
	/**
	 * activate zombie attack event.
	 */
	public static void activateEvent(World world, @Nonnull Events event) {
		WorldEventData data = WorldEventData.getOverWorldEventData(world);
		if(! data.hasEvent(event)) {
			data.addEvent(event);
			EntitySpawnRegister.addEventSpawns(world, event);
		}
	}
	
	/**
	 * deactivate all events and remove zombie spawn
	 */
	public static void deactivateZombieAttackEvents(World world, boolean isNatural) {
		if(isNatural) {
			for(PlayerEntity pl : world.getPlayers()) {
		        pl.sendMessage(ATTACK_FINISH);
		        PlayerUtil.playClientSound(pl, 4);
		        WaveManager.giveInvasionBonusToPlayer(world, pl);
	        }
		}
		for(Events ev : Events.values()) {
			deactivateEvent(world, ev);
		}
		EntitySpawnRegister.removeEventSpawns(world);
	}
	
	/**
	 * deactivate all events
	 */
	private static void deactivateEvent(World world, @Nonnull Events event) {
		WorldEventData data = WorldEventData.getOverWorldEventData(world);
		if(data.hasEvent(event)) {
			data.removeEvent(event);
		}
	}
	
}
