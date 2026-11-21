package com.omnicrox.gtaa.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IInteractedMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.omnicrox.gtaa.common.block.GateChevronBlock;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.item.MaxwellItem;
import com.omnicrox.gtaa.common.network.GTAANetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Ascension Gate: the end of Apex Ascension. A ring with 15 chevrons, one per tier from ULV to MAX. Each chevron
 * locks when its milestone items are in an input bus (and the gate has the energy); with all 15 locked,
 * shift-right-click the controller with an empty hand to dial. The gate opens, everyone hears about it,
 * fireworks go off, the sky glows pink, and Maxwell walks out. He does nothing. He is perfect.
 */
public class AscensionGateMachine extends WorkableElectricMultiblockMachine implements IInteractedMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AscensionGateMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    /** one milestone item: candidate ids (first that exists; '#' = tag) and how many */
    public record Need(int count, String... ids) {}

    public record Chevron(String tier, Need... needs) {}

    public static final Chevron[] ADDRESS = {
            new Chevron("ULV", new Need(16, "gtceu:bronze_gear", "minecraft:copper_block")),
            new Chevron("LV", new Need(16, "#gtceu:circuits/lv")),
            new Chevron("MV", new Need(4, "botania:terrasteel_block", "gtceu:aluminium_block")),
            new Chevron("HV", new Need(1, "ad_astra:tier_1_rocket", "gtceu:stainless_steel_block")),
            new Chevron("EV", new Need(8, "botania:life_essence", "gtceu:titanium_block")),
            new Chevron("IV", new Need(4, "mythicbotany:alfsteel_block", "gtceu:tungsten_steel_block")),
            new Chevron("LuV", new Need(4, "botanicadds:gaiasteel_block", "gtceu:hssg_block")),
            new Chevron("ZPM", new Need(16, "mysticalagriculture:gt_naquadah_seeds", "gtceu:naquadah_alloy_block")),
            new Chevron("UV", new Need(16, "gtna:infinity_ingot", "gtceu:tritanium_block")),
            new Chevron("UHV", new Need(4, "gtapexadditions:hero_core")),
            new Chevron("UEV", new Need(1, "gtapexadditions:apex_helmet"), new Need(1, "gtapexadditions:apex_chestplate"),
                    new Need(1, "gtapexadditions:apex_leggings"), new Need(1, "gtapexadditions:apex_boots")),
            new Chevron("UIV", new Need(4, "gtceu:uiv_field_generator", "gtceu:uev_field_generator")),
            new Chevron("UXV", new Need(1, "avaritia:infinity_catalyst", "gtceu:uxv_field_generator")),
            new Chevron("OpV", new Need(4, "gtceu:opv_field_generator", "gtceu:uxv_field_generator")),
            new Chevron("MAX", new Need(1, "avaritia:eternal_singularity", "avaritia:infinity_catalyst"),
                    new Need(64, "gtceu:neutronium_block")),
    };

    @Persisted @DescSynced private int locked;
    @Persisted @DescSynced private boolean open;
    @Persisted @DescSynced private String ascendedBy = "";
    @DescSynced private String status = "";
    /** where the horizon and chevrons were, so they can be cleaned up after the ring breaks (the cache is gone then) */
    @Persisted private long[] horizonCells = new long[0];
    private List<BlockPos> chevronCells = new ArrayList<>();
    private int celebrateTicks;
    private TickableSubscription tick;

    public AscensionGateMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public int getLocked() {
        return locked;
    }

    public boolean isOpen() {
        return open;
    }

    //////////////////////////////////////
    // ***** milestones *****
    //////////////////////////////////////

    private static Object resolve(String id) {
        if (id.startsWith("#")) return TagKey.create(net.minecraft.core.registries.Registries.ITEM, new ResourceLocation(id.substring(1)));
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
        return item == null || item == Items.AIR ? null : item;
    }

    /** the item or tag a Need asks for in this pack (first candidate that exists) */
    private static Object pick(Need need) {
        for (String id : need.ids()) {
            Object o = resolve(id);
            if (o != null) return o;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static boolean matches(Object want, ItemStack s) {
        if (s.isEmpty() || want == null) return false;
        if (want instanceof Item i) return s.is(i);
        return s.is((TagKey<Item>) want);
    }

    @SuppressWarnings("unchecked")
    public static String describe(Need need) {
        Object o = pick(need);
        String name = o instanceof Item i ? i.getDescription().getString() :
                o instanceof TagKey<?> t ? "any " + t.location().getPath().replace("circuits/", "").toUpperCase() + " circuit" : "?";
        return need.count() + "x " + name;
    }

    public static String describe(Chevron c) {
        List<String> parts = new ArrayList<>();
        for (Need n : c.needs()) parts.add(describe(n));
        return String.join(", ", parts);
    }

    private List<NotifiableItemStackHandler> inputs() {
        List<NotifiableItemStackHandler> out = new ArrayList<>();
        for (IMultiPart part : getParts())
            for (var handler : part.getRecipeHandlers())
                if (handler.getHandlerIO() == IO.IN)
                    for (var h : handler.getCapability(ItemRecipeCapability.CAP))
                        if (h instanceof NotifiableItemStackHandler inv) out.add(inv);
        return out;
    }

    private int count(List<NotifiableItemStackHandler> invs, Object want) {
        int n = 0;
        for (var inv : invs)
            for (int i = 0; i < inv.getSlots(); i++) if (matches(want, inv.getStackInSlot(i))) n += inv.getStackInSlot(i).getCount();
        return n;
    }

    private void take(List<NotifiableItemStackHandler> invs, Object want, int amount) {
        for (var inv : invs)
            for (int i = 0; i < inv.getSlots() && amount > 0; i++)
                if (matches(want, inv.getStackInSlot(i))) amount -= inv.extractItemInternal(i, amount, false).getCount();
    }

    /** half of one energy hatch of that tier's buffer: you need power at the chevron's tier to lock it */
    public static long lockCost(int index) {
        return GTValues.V[Math.min(GTValues.UHV, index)] * 32L;
    }

    //////////////////////////////////////
    // ***** ring geometry *****
    //////////////////////////////////////

    /** the ring's chevron positions, clockwise from the top */
    private List<BlockPos> chevronPositions() {
        List<BlockPos> chev = new ArrayList<>();
        Level level = getLevel();
        if (level == null) return chev;
        for (BlockPos p : getMultiblockState().getCache())
            if (level.getBlockState(p).getBlock() instanceof GateChevronBlock) chev.add(p.immutable());
        if (chev.isEmpty()) return chev;
        boolean alongX = chev.stream().mapToInt(BlockPos::getX).distinct().count() > 1;
        double cx = chev.stream().mapToDouble(p -> alongX ? p.getX() : p.getZ()).average().orElse(0);
        double cy = chev.stream().mapToDouble(BlockPos::getY).average().orElse(0);
        chev.sort(Comparator.comparingDouble(p -> {
            double h = (alongX ? p.getX() : p.getZ()) - cx, v = p.getY() - cy;
            double a = Math.atan2(h, v);
            return a < 0 ? a + 2 * Math.PI : a;
        }));
        return chev;
    }

    /** the air inside the ring */
    private List<BlockPos> interior() {
        List<BlockPos> ring = new ArrayList<>();
        Level level = getLevel();
        List<BlockPos> out = new ArrayList<>();
        if (level == null) return out;
        for (BlockPos p : getMultiblockState().getCache()) {
            var b = level.getBlockState(p).getBlock();
            if (b instanceof GateChevronBlock || b == GTAABlocks.GATE_RING.get()) ring.add(p.immutable());
        }
        if (ring.isEmpty()) return out;
        boolean alongX = ring.stream().mapToInt(BlockPos::getX).distinct().count() > 1;
        int fixed = alongX ? ring.get(0).getZ() : ring.get(0).getX();
        int minY = ring.stream().mapToInt(BlockPos::getY).min().getAsInt();
        int maxY = ring.stream().mapToInt(BlockPos::getY).max().getAsInt();
        for (int y = minY + 1; y < maxY; y++) {
            final int yy = y;
            int lo = ring.stream().filter(p -> p.getY() == yy).mapToInt(p -> alongX ? p.getX() : p.getZ()).min().orElse(0);
            int hi = ring.stream().filter(p -> p.getY() == yy).mapToInt(p -> alongX ? p.getX() : p.getZ()).max().orElse(0);
            for (int h = lo + 1; h < hi; h++) out.add(alongX ? new BlockPos(h, y, fixed) : new BlockPos(fixed, y, h));
        }
        return out;
    }

    private void setChevron(BlockPos p, boolean lit) {
        Level level = getLevel();
        if (level == null) return;
        BlockState s = level.getBlockState(p);
        if (s.getBlock() instanceof GateChevronBlock && s.getValue(GateChevronBlock.LIT) != lit)
            level.setBlock(p, s.setValue(GateChevronBlock.LIT, lit), 3);
    }

    private void showChevrons() {
        List<BlockPos> chev = chevronPositions();
        chevronCells = chev;
        for (int i = 0; i < chev.size(); i++) setChevron(chev.get(i), open || i < locked);
    }

    private void setHorizon(boolean on) {
        Level level = getLevel();
        if (level == null) return;
        BlockState horizon = GTAABlocks.EVENT_HORIZON.get().defaultBlockState();
        if (on) {
            List<BlockPos> cells = interior();
            horizonCells = cells.stream().mapToLong(BlockPos::asLong).toArray();
            for (BlockPos p : cells) if (level.getBlockState(p).isAir()) level.setBlock(p, horizon, 3);
        } else {
            for (long l : horizonCells) {
                BlockPos p = BlockPos.of(l);
                if (level.getBlockState(p).is(GTAABlocks.EVENT_HORIZON.get())) level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
            }
            horizonCells = new long[0];
        }
    }

    //////////////////////////////////////
    // ***** lifecycle *****
    //////////////////////////////////////

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (!isRemote()) {
            showChevrons();
            if (open) setHorizon(true);
            tick = subscribeServerTick(tick, this::gateTick);
        }
    }

    @Override
    public void onStructureInvalid() {
        if (!isRemote()) {
            setHorizon(false);
            for (BlockPos p : chevronCells) setChevron(p, false);
            chevronCells = new ArrayList<>();
        }
        super.onStructureInvalid();
        if (tick != null) {
            tick.unsubscribe();
            tick = null;
        }
    }

    private void gateTick() {
        if (!(getLevel() instanceof ServerLevel level)) return;
        if (celebrateTicks > 0) celebrate(level);
        // the gate edits blocks inside its own ring, so it double-checks itself instead of waiting on GT's async check
        if (getOffsetTimer() % 40 == 0 && !checkPattern()) {
            onStructureInvalid();
            return;
        }
        if (open || getOffsetTimer() % 20 != 0) return;
        if (locked >= ADDRESS.length) {
            status = "All 15 chevrons locked. Shift-right-click with an empty hand to dial.";
            return;
        }
        Chevron c = ADDRESS[locked];
        var invs = inputs();
        for (Need n : c.needs()) {
            Object want = pick(n);
            if (want == null || count(invs, want) < n.count()) {
                status = "";
                return;
            }
        }
        long cost = lockCost(locked);
        if (energyContainer == null || energyContainer.getEnergyStored() < cost) {
            status = "Needs " + String.format("%,d", cost) + " EU stored to lock this chevron";
            return;
        }
        energyContainer.removeEnergy(cost);
        for (Need n : c.needs()) take(invs, pick(n), n.count());
        locked++;
        status = "";
        showChevrons();
        BlockPos at = getPos();
        level.playSound(null, at, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.5f, 0.6f + locked * 0.05f);
        List<BlockPos> chev = chevronPositions();
        if (locked - 1 < chev.size()) {
            BlockPos p = chev.get(locked - 1);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 30, 0.4, 0.4, 0.4, 0.1);
        }
        if (locked == ADDRESS.length)
            level.playSound(null, at, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 2f, 0.8f);
    }

    //////////////////////////////////////
    // ***** dialing *****
    //////////////////////////////////////

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.isShiftKeyDown() || !player.getItemInHand(hand).isEmpty() || !isFormed()) return InteractionResult.PASS;
        if (world.isClientSide) return InteractionResult.SUCCESS;
        if (open) {
            player.displayClientMessage(Component.literal("The gate is open. " + ascendedBy + " ascended here. Maxwell is pleased.")
                    .withStyle(ChatFormatting.LIGHT_PURPLE), true);
        } else if (locked < ADDRESS.length) {
            player.displayClientMessage(Component.literal("Chevron " + (locked + 1) + " (" + ADDRESS[locked].tier() + ") needs " +
                    describe(ADDRESS[locked]) + " in an input bus").withStyle(ChatFormatting.YELLOW), true);
        } else if (player instanceof ServerPlayer sp) {
            dial(sp);
        }
        return InteractionResult.CONSUME;
    }

    /** for tests */
    public void dialForTest(ServerPlayer player) {
        locked = ADDRESS.length;
        dial(player);
    }

    private void dial(ServerPlayer player) {
        if (!(getLevel() instanceof ServerLevel level)) return;
        open = true;
        ascendedBy = player.getGameProfile().getName();
        showChevrons();
        setHorizon(true);
        BlockPos at = getPos();
        level.playSound(null, at, SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, 2f, 1.2f);
        level.playSound(null, at, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 2f, 1f);
        level.getServer().getPlayerList().broadcastSystemMessage(Component.literal("✦ " + ascendedBy + " has Ascended. ✦")
                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD), false);

        // Maxwell steps out of the gate
        List<BlockPos> inside = interior();
        BlockPos spawn = inside.isEmpty() ? at.above() : inside.get(inside.size() / 2);
        Cat maxwell = EntityType.CAT.create(level);
        if (maxwell != null) {
            var variant = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.CAT_VARIANT)
                    .getHolder(CatVariant.BLACK);
            variant.ifPresent(v -> maxwell.setVariant(v.value()));
            maxwell.moveTo(spawn.getX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, 0, 0);
            maxwell.tame(player);
            maxwell.setCustomName(Component.literal("Maxwell").withStyle(ChatFormatting.LIGHT_PURPLE));
            maxwell.setCustomNameVisible(true);
            maxwell.setInvulnerable(true);
            maxwell.setPersistenceRequired();
            maxwell.setCollarColor(DyeColor.PINK);
            level.addFreshEntity(maxwell);
            level.sendParticles(ParticleTypes.HEART, spawn.getX() + 0.5, spawn.getY() + 1, spawn.getZ() + 0.5, 12, 0.5, 0.5, 0.5, 0);
            level.playSound(null, spawn, SoundEvents.CAT_AMBIENT, SoundSource.NEUTRAL, 1.5f, 1f);
        }
        ItemStack prize = new ItemStack(GTAAItems.MAXWELL.get());
        if (!player.getInventory().add(prize)) player.drop(prize, false);
        GTAANetwork.send(player, new GTAANetwork.AscensionPacket(true, MaxwellItem.stats(player)));
        celebrateTicks = 200;
    }

    /** ten seconds of fireworks around the ring */
    private void celebrate(ServerLevel level) {
        celebrateTicks--;
        if (celebrateTicks % 8 != 0) return;
        List<BlockPos> chev = chevronPositions();
        if (chev.isEmpty()) return;
        BlockPos p = chev.get(level.random.nextInt(chev.size()));
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag fw = new CompoundTag();
        CompoundTag star = new CompoundTag();
        star.putByte("Type", (byte) level.random.nextInt(5));
        star.putIntArray("Colors", new int[] { 0xFF9AD5, 0xFFC94A, 0x5FF3FF, 0xC77DFF });
        star.putBoolean("Twinkle", true);
        ListTag ex = new ListTag();
        ex.add(star);
        fw.put(FireworkRocketItem.TAG_EXPLOSIONS, ex);
        fw.putByte("Flight", (byte) 1);
        rocket.getOrCreateTag().put(FireworkRocketItem.TAG_FIREWORKS, fw);
        level.addFreshEntity(new FireworkRocketEntity(level, p.getX() + 0.5, p.getY() + 1, p.getZ() + 0.5, rocket));
    }

    //////////////////////////////////////
    // ***** display *****
    //////////////////////////////////////

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        if (open) {
            textList.add(Component.literal("✦ The gate is open ✦").withStyle(ChatFormatting.LIGHT_PURPLE));
            textList.add(Component.literal(ascendedBy + " ascended here.").withStyle(ChatFormatting.GRAY));
            return;
        }
        textList.add(Component.literal("Chevrons locked: " + locked + " / " + ADDRESS.length).withStyle(ChatFormatting.GOLD));
        if (locked < ADDRESS.length) {
            Chevron c = ADDRESS[locked];
            textList.add(Component.literal("Next: chevron " + (locked + 1) + " (" + c.tier() + ")").withStyle(ChatFormatting.AQUA));
            textList.add(Component.literal("  " + describe(c)).withStyle(ChatFormatting.WHITE));
            textList.add(Component.literal("  + " + String.format("%,d", lockCost(locked)) + " EU").withStyle(ChatFormatting.GRAY));
        }
        if (!status.isEmpty()) textList.add(Component.literal(status).withStyle(ChatFormatting.YELLOW));
    }
}
