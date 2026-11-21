package com.omnicrox.gtaa.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * GT Nexus Additions' Forge of the Gods says it boots on "Star Fuel (the item)" but checks for a GregTech Neutronium
 * Block. With Re-Avaritia installed, the Star Fuel item is the startup fuel (GTAA adds a GT recipe that makes it from
 * Neutronium Blocks). Without Re-Avaritia nothing changes.
 */
@Pseudo
@Mixin(targets = "com.raishxn.gtna.common.machine.multiblock.godforge.ForgeOfGodsMachine", remap = false)
public abstract class ForgeOfGodsStarFuelMixin {

    @Inject(method = "stellarFuel", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void gtaa$starFuel(CallbackInfoReturnable<ItemStack> cir) {
        Item star = ForgeRegistries.ITEMS.getValue(new ResourceLocation("avaritia", "star_fuel"));
        if (star != null && star != Items.AIR) cir.setReturnValue(new ItemStack(star));
    }
}
