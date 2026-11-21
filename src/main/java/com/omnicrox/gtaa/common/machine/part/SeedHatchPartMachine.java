package com.omnicrox.gtaa.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;

/**
 * Seed Hatch for the Mystical Cultivation Array. Holds seeds only, put in by hand: pipes, hoppers and ME can't
 * insert or extract. Each slot is one crop type and holds up to 256 seeds. LV 16 slots, x1.25 per tier, LuV 50.
 */
public class SeedHatchPartMachine extends ItemBusPartMachine {

    public static final int SLOT_LIMIT = 256;
    /** slots by tier, LV..LuV */
    private static final int[] SLOTS = { 16, 20, 25, 32, 40, 50 };

    public SeedHatchPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, IO.IN);
    }

    public static int slots(int tier) {
        return SLOTS[Math.max(0, Math.min(SLOTS.length - 1, tier - GTValues.LV))];
    }

    public static boolean isSeed(ItemStack s) {
        return s.getItem() instanceof BlockItem bi && bi.getBlock() instanceof CropBlock;
    }

    @Override
    protected int getInventorySize() {
        return slots(getTier());
    }

    @Override
    protected NotifiableItemStackHandler createInventory(Object... args) {
        // handler IO IN (the array reads it), capability IO NONE (nothing outside the GUI can touch it)
        return new NotifiableItemStackHandler(this, getInventorySize(), IO.IN, IO.NONE, n -> new CustomItemStackHandler(n) {
            @Override
            public int getSlotLimit(int slot) {
                return SLOT_LIMIT;
            }

            @Override
            protected int getStackLimit(int slot, ItemStack stack) {
                return SLOT_LIMIT;
            }
        }) {
            @Override
            public int getSlotLimit(int slot) {
                return SLOT_LIMIT;
            }
        }.setFilter(SeedHatchPartMachine::isSeed);
    }

    @Override
    protected boolean isHasCircuitSlot() {
        return false;
    }

    @Override
    protected void autoIO() {}

    @Override
    public boolean swapIO() {
        return false;
    }

    /** total seeds and types, for the array's display */
    public int[] census() {
        int seeds = 0, types = 0;
        for (int i = 0; i < getInventory().getSlots(); i++) {
            ItemStack s = getInventory().getStackInSlot(i);
            if (s.isEmpty() || !isSeed(s)) continue;
            seeds += s.getCount();
            types++;
        }
        return new int[] { seeds, types };
    }
}
