package com.hungteen.pvz.item.tool;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hungteen.pvz.capability.CapabilityHandler;
import com.hungteen.pvz.entity.bullet.PeaEntity;
import com.hungteen.pvz.gui.container.PeaGunContainer;
import com.hungteen.pvz.gui.inventory.ItemInventory;
import com.hungteen.pvz.item.tool.card.PlantCardItem;
import com.hungteen.pvz.network.PVZPacketHandler;
import com.hungteen.pvz.network.PlaySoundPacket;
import com.hungteen.pvz.register.EntityRegister;
import com.hungteen.pvz.register.GroupRegister;
import com.hungteen.pvz.register.ItemRegister;
import com.hungteen.pvz.utils.enums.Plants;
import com.hungteen.pvz.utils.enums.Resources;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.PowerEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class PeaGunItem extends Item {

	public static final int PEA_GUN_SLOT_NUM = 28;
	public static final float PEA_SPEED = 2.1f;
	public static final double SHOOT_OFFSET = 0.5;
	public static final Plants[] PEA_PLANTS = new Plants[] { Plants.PEA_SHOOTER, Plants.SNOW_PEA, Plants.REPEATER,
			Plants.THREE_PEATER };
//	private Inventory backpack = new Inventory(PEA_GUN_SLOT_NUM);

	public PeaGunItem() {
		super(new Properties().group(GroupRegister.PVZ_MISC).maxStackSize(1));
	}

	@Nonnull
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {
		return new InvProvider(stack);
	}

	public static Inventory getInventory(ItemStack stack) {
		return new ItemInventory(stack, PEA_GUN_SLOT_NUM) {
			@Override
			public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
				if (slot == 0) {
					return true;
				} else {
					return stack.getItem() == ItemRegister.PEA.get() || stack.getItem() == ItemRegister.SNOW_PEA.get()
							|| stack.getItem() == ItemRegister.FLAME_PEA.get();
				}
			}
		};
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (enchantment instanceof PowerEnchantment || enchantment instanceof InfinityEnchantment) {
			return true;
		}
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemEnchantability() {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (!worldIn.isRemote) {
			if (handIn == Hand.MAIN_HAND) {
				if(!this.checkAndShootPea(worldIn, playerIn, playerIn.getHeldItemMainhand())) {
					return ActionResult.resultFail(playerIn.getHeldItem(handIn));
				}
			} else { // offhand open gui
				if (playerIn instanceof ServerPlayerEntity) {
					NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {

						@Override
						public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_,
								PlayerEntity p_createMenu_3_) {
							return new PeaGunContainer(p_createMenu_1_, p_createMenu_3_);
						}

						@Override
						public ITextComponent getDisplayName() {
							return new TranslationTextComponent("gui.pvz.pea_gun.show");
						}
					});
				}
			}
		}
		return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
	}

	public boolean checkAndShootPea(World world, PlayerEntity player, ItemStack itemStack) {
		Inventory backpack = getInventory(itemStack);
		ItemStack special = backpack.getStackInSlot(0);
//		System.out.println(itemStack);
		if (special.getItem() instanceof PlantCardItem) {
			Plants plant = ((PlantCardItem) special.getItem()).plantType;
			for (int i = 1; i < PEA_GUN_SLOT_NUM; i++) {
				ItemStack stack = backpack.getStackInSlot(i);
				if (stack.isEmpty()) {
					continue;
				}
				if (this.canShoot(player, plant, stack, itemStack)) {
					shrinkItemStack(player, backpack, i, itemStack);
					PVZPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(()->{
						return (ServerPlayerEntity) player;
					}), new PlaySoundPacket(1));
					setPlayerCoolDownTick(player);
//					stack.damageItem(1, player, (p) -> {
//						InventoryHelper.dropInventoryItems(world, p, backpack);
////						p.sendBreakAnimation(Hand.MAIN_HAND)
//						;
//					});
					return true;
				}
				break;
			}
		}
		return false;
	}

	private void shrinkItemStack(PlayerEntity player, Inventory backpack, int i, ItemStack stack) {
		player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
			boolean flag = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
			int lvl = l.getPlayerData().getPlayerStats().getPlayerStats(Resources.TREE_LVL);
			int cnt = (lvl - 1) / 10 * 5 + 5;
			if (!flag || random.nextInt(100) >= cnt) {
				backpack.decrStackSize(i, 1);
			}
		});
	}

	protected boolean canShoot(PlayerEntity player, Plants plant, ItemStack stack, ItemStack peaGun) {
		switch (plant) {
		case PEA_SHOOTER:
		case SNOW_PEA: {
			this.performShoot(player, plant, stack, peaGun, 0); // normal shoot
			return true;
		}
		case REPEATER: {
			this.performShoot(player, plant, stack, peaGun, 0);
			this.performShoot(player, plant, stack, peaGun, 1);
			return true;
		}
		case THREE_PEATER: {
			this.performShoot(player, plant, stack, peaGun, 0);
			this.performShoot(player, plant, stack, peaGun, 1);
			this.performShoot(player, plant, stack, peaGun, 2);
			return true;
		}
		default: {
			return false;
		}
		}
	}

	private void performShoot(PlayerEntity player, Plants plant, ItemStack stack, ItemStack peaGun, int type) {
		player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
			int plantLvl = l.getPlayerData().getPlantStats().getPlantLevel(plant);
			int lvl = l.getPlayerData().getPlayerStats().getPlayerStats(Resources.TREE_LVL);
			PeaEntity.Type peaType = PeaEntity.Type.NORMAL;
			if(plantLvl > 6) {
				int now = (lvl + 19) / 20;
				int bigChance = now * 5;
				int hugeChance = bigChance + ((plantLvl > 13) ? now : 0);
				int tmp = random.nextInt(100);
				if(tmp < bigChance) {
					peaType = PeaEntity.Type.BIG;
				}else if(tmp < hugeChance) {
					peaType = PeaEntity.Type.HUGE;
				}
			}
			PeaEntity pea = new PeaEntity(EntityRegister.PEA.get(), player.world, player, peaType, getPeaState(plant, stack.getItem()));
			pea.setPower(EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, peaGun));// Power Enchantment can affect pea gun.
			Vec3d vec = player.getLookVec();
			Vec3d offset = vec.scale(SHOOT_OFFSET);
			pea.setPosition(player.getPosX() + offset.x, player.getPosY() + player.getEyeHeight() + offset.y,
					player.getPosZ() + offset.z);
			if (plant == Plants.REPEATER) {
				if (type == 1) {
					pea.setPosition(player.getPosX() + offset.x, player.getPosY() + player.getEyeHeight() + offset.y,
							player.getPosZ() + offset.z);
				}
			} else if (plant == Plants.THREE_PEATER) {
				offset = offset.scale(2);
				if (type == 1) {
					pea.setPosition(player.getPosX() + offset.x + offset.z,
							player.getPosY() + player.getEyeHeight() + offset.y,
							player.getPosZ() + offset.z - offset.x);
				} else if (type == 2) {
					pea.setPosition(player.getPosX() + offset.x - offset.z,
							player.getPosY() + player.getEyeHeight() + offset.y,
							player.getPosZ() + offset.z + offset.x);
				}
			}
			pea.setMotion(vec.scale(PEA_SPEED));
			player.world.addEntity(pea);
		});
	}

	/**
	 * get Shoot Pea State
	 */
	private PeaEntity.State getPeaState(Plants plant, Item item) {
		if (plant == Plants.SNOW_PEA) {
			return PeaEntity.State.ICE;
		}else if (item == ItemRegister.SNOW_PEA.get()) {
			return PeaEntity.State.ICE;
		}else if (item == ItemRegister.FLAME_PEA.get()) {
			return PeaEntity.State.FIRE;
		}else if (item == ItemRegister.BLUE_FLAME_PEA.get()) {
			return PeaEntity.State.BLUE_FIRE;
		}else {
			return PeaEntity.State.NORMAL;
		}
	}

	protected void setPlayerCoolDownTick(PlayerEntity player) {
		player.getCapability(CapabilityHandler.PLAYER_DATA_CAPABILITY).ifPresent((l) -> {
			int lvl = l.getPlayerData().getPlayerStats().getPlayerStats(Resources.TREE_LVL);
			int now = (lvl - 1) / 10;
			int CD = 30 - 2 * now;
			if (lvl > 90)
				CD -= 2;
			player.getCooldownTracker().setCooldown(this, CD);
		});
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.pvz.pea_gun").applyTextStyle(TextFormatting.GREEN));
	}

	@Override
	public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
		return armorType == EquipmentSlotType.HEAD;
	}

	private static class InvProvider implements ICapabilityProvider {

		private final LazyOptional<IItemHandler> opt;

		private InvProvider(ItemStack stack) {
			opt = LazyOptional.of(() -> new InvWrapper(getInventory(stack)));
		}

		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(capability, opt);
		}
	}

}
