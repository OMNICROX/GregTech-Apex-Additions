package com.omnicrox.gtaa.mixin;

import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import it.unimi.dsi.fastutil.ints.IntObjectPair;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = BlockPattern.class, remap = false)
public interface BlockPatternInvoker {

    @Invoker(value = "getMatchStackWithHandler", remap = false)
    static IntObjectPair<IItemHandler> gtaa$match(List<ItemStack> candidates, LazyOptional<IItemHandler> cap) {
        throw new AssertionError();
    }
}
