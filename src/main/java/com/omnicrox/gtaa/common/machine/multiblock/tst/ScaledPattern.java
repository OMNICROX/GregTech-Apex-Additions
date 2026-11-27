package com.omnicrox.gtaa.common.machine.multiblock.tst;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.world.level.Level;

import java.util.Objects;

/**
 * An AE2 pattern with every input and output multiplied by {@code magnification}: one push crafts that many times.
 * This is the Extreme Craft Center's "pattern magnification" from Twist Space Technology.
 */
public final class ScaledPattern implements IPatternDetails {

    private final IPatternDetails base;
    private final long magnification;
    private final IInput[] inputs;
    private final GenericStack[] outputs;

    public ScaledPattern(IPatternDetails base, long magnification) {
        this.base = base;
        this.magnification = Math.max(1, magnification);
        IInput[] in = base.getInputs();
        this.inputs = new IInput[in.length];
        for (int i = 0; i < in.length; i++) inputs[i] = new Scaled(in[i], this.magnification);
        GenericStack[] out = base.getOutputs();
        this.outputs = new GenericStack[out.length];
        for (int i = 0; i < out.length; i++)
            outputs[i] = new GenericStack(out[i].what(), out[i].amount() * this.magnification);
    }

    public IPatternDetails base() {
        return base;
    }

    public long magnification() {
        return magnification;
    }

    @Override
    public AEItemKey getDefinition() {
        return base.getDefinition();
    }

    @Override
    public IInput[] getInputs() {
        return inputs;
    }

    @Override
    public GenericStack[] getOutputs() {
        return outputs;
    }

    @Override
    public GenericStack getPrimaryOutput() {
        return outputs[0];
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ScaledPattern s && s.magnification == magnification && s.base.equals(base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, magnification);
    }

    private record Scaled(IInput in, long magnification) implements IInput {

        @Override
        public GenericStack[] getPossibleInputs() {
            return in.getPossibleInputs();
        }

        @Override
        public long getMultiplier() {
            return in.getMultiplier() * magnification;
        }

        @Override
        public boolean isValid(AEKey input, Level level) {
            return in.isValid(input, level);
        }

        @Override
        public AEKey getRemainingKey(AEKey template) {
            return in.getRemainingKey(template);
        }
    }
}
