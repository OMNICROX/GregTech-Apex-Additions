package com.omnicrox.gtaa.common.item;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.omnicrox.gtaa.common.data.GTAABlocks;
import com.omnicrox.gtaa.common.data.GTAAItems;
import com.omnicrox.gtaa.common.machine.multiblock.MysticalCultivationArrayMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Structure Upgrader: shift-right-click a formed multiblock's controller to get a prompt in chat listing
 * <ul>
 * <li>every tiered block family in the structure (GT coils, GTAA tiered casings, Cultivation Array soil / glass)
 * with buttons for each tier you could swap to, and</li>
 * <li>every tiered hatch/bus that you carry a higher tier of, with an upgrade button each (hatches are never
 * changed unless you click).</li>
 * </ul>
 * Swaps take the new blocks from your inventory (creative: free) and give the old ones back. Protected blocks
 * (claims) are skipped.
 */
public class StructureUpgrader extends Item {

    public StructureUpgrader(Properties props) {
        super(props.stacksTo(1));
    }

    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(StructureUpgrader.class);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Shift + Right-Click a formed multiblock controller").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Upgrades coils, tiered casings and (if you choose) hatches").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Uses blocks from your inventory and gives the old ones back").withStyle(ChatFormatting.DARK_GRAY));
    }

    // ------------------------------------------------------------------ families

    /** A tiered block family: tiers.get(i) = blocks counted as tier i+1; the first block is the one placed. */
    public record Family(String id, String name, List<List<Block>> tiers) {
        int tierOf(Block b) {
            for (int i = 0; i < tiers.size(); i++) if (tiers.get(i).contains(b)) return i + 1;
            return 0;
        }

        Block block(int tier) {
            return tiers.get(tier - 1).get(0);
        }
    }

    private static List<List<Block>> single(Block... blocks) {
        List<List<Block>> l = new ArrayList<>();
        for (Block b : blocks) l.add(List.of(b));
        return l;
    }

    private static List<List<Block>> grouped(Map<Block, Integer> map) {
        int max = map.values().stream().max(Integer::compare).orElse(0);
        List<List<Block>> l = new ArrayList<>();
        for (int t = 1; t <= max; t++) {
            List<Block> blocks = new ArrayList<>();
            for (var e : map.entrySet()) if (e.getValue() == t) blocks.add(e.getKey());
            if (blocks.isEmpty()) return l;
            l.add(blocks);
        }
        return l;
    }

    public static List<Family> families() {
        List<Family> out = new ArrayList<>();
        List<ICoilType> coilTypes = new ArrayList<>(GTCEuAPI.HEATING_COILS.keySet());
        coilTypes.sort(Comparator.comparingInt(ICoilType::getTier));
        List<List<Block>> coilTiers = new ArrayList<>();
        for (ICoilType type : coilTypes) coilTiers.add(List.of((Block) GTCEuAPI.HEATING_COILS.get(type).get()));
        out.add(new Family("coils", "Heating Coils", coilTiers));
        out.add(new Family("qft_manipulators", "QFT Manipulators", single(GTAABlocks.qftManipulators())));
        out.add(new Family("qft_shielding", "QFT Shielding", single(GTAABlocks.qftShieldings())));
        out.add(new Family("field_restriction", "Field Restriction Coils", single(GTAABlocks.fieldRestrictionCoils())));
        out.add(new Family("defc_cores", "Fusion Core Casings", single(GTAABlocks.defcCores())));
        List<Block> coal = new ArrayList<>();
        for (var b : GTAABlocks.COAL_CASINGS) if (b != null) coal.add(b.get());
        out.add(new Family("coal_casings", "Component Assembly Casings", single(coal.toArray(Block[]::new))));
        out.add(new Family("mca_soil", "Cultivation Soil", grouped(MysticalCultivationArrayMachine.soilTiers())));
        out.add(new Family("mca_glass", "Cultivation Glass", grouped(MysticalCultivationArrayMachine.glassTiers())));
        return out;
    }

    // ------------------------------------------------------------------ use

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Player player = ctx.getPlayer();
        if (player == null || !player.isShiftKeyDown()) return InteractionResult.PASS;
        Level level = ctx.getLevel();
        if (!(MetaMachine.getMachine(level, ctx.getClickedPos()) instanceof IMultiController controller))
            return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        prompt((ServerPlayer) player, controller);
        return InteractionResult.CONSUME;
    }

    private static Component button(String label, String cmd, String hover, ChatFormatting color) {
        return Component.literal("[" + label + "]").setStyle(Style.EMPTY.withColor(color)
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(hover))));
    }

    private static String posArg(BlockPos p) {
        return p.getX() + " " + p.getY() + " " + p.getZ();
    }

    static void prompt(ServerPlayer player, IMultiController controller) {
        BlockPos cpos = controller.self().getPos();
        if (!controller.isFormed()) {
            player.sendSystemMessage(Component.literal("That multiblock isn't formed yet.").withStyle(ChatFormatting.RED));
            return;
        }
        Level level = controller.self().getLevel();
        var cache = controller.getMultiblockState().getCache();
        player.sendSystemMessage(Component.literal("Structure Upgrader: ").withStyle(ChatFormatting.GOLD)
                .append(controller.self().getDefinition().getBlock().getName().copy().withStyle(ChatFormatting.WHITE)));
        boolean any = false;
        for (Family fam : families()) {
            Map<Integer, Integer> count = new LinkedHashMap<>();
            for (BlockPos p : cache) {
                int t = fam.tierOf(level.getBlockState(p).getBlock());
                if (t > 0) count.merge(t, 1, Integer::sum);
            }
            if (count.isEmpty()) continue;
            any = true;
            int total = count.values().stream().mapToInt(i -> i).sum();
            int cur = count.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
            MutableComponent line = Component.literal(" " + fam.name + " (" + total + "): ").withStyle(ChatFormatting.YELLOW)
                    .append(fam.block(cur).getName().copy().withStyle(ChatFormatting.WHITE)).append(Component.literal("  "));
            for (int t = 1; t <= fam.tiers.size(); t++) {
                if (t == cur && count.size() == 1) continue;
                Block b = fam.block(t);
                int have = player.isCreative() ? total : countItem(player, b.asItem());
                line.append(button(b.getName().getString(), "/gtapexadditions upgrade " + posArg(cpos) + " family " + fam.id + " " + t,
                        "Swap all " + total + " to " + b.getName().getString() + " (you have " + have + ")",
                        have >= total ? ChatFormatting.GREEN : ChatFormatting.GRAY)).append(" ");
            }
            player.sendSystemMessage(line);
        }
        List<IMultiPart> upgradable = new ArrayList<>();
        for (IMultiPart part : controller.getParts()) {
            MachineDefinition next = nextTier(part, player);
            if (next == null) continue;
            upgradable.add(part);
            BlockPos pp = part.self().getPos();
            player.sendSystemMessage(Component.literal(" Hatch ").withStyle(ChatFormatting.AQUA)
                    .append(part.self().getDefinition().getBlock().getName().copy().withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(" -> ").withStyle(ChatFormatting.GRAY))
                    .append(next.getBlock().getName().copy().withStyle(ChatFormatting.WHITE)).append("  ")
                    .append(button("Upgrade", "/gtapexadditions upgrade " + posArg(cpos) + " hatch " + posArg(pp),
                            "Replace this hatch. Its contents drop on the ground.", ChatFormatting.GREEN)));
        }
        if (upgradable.size() > 1)
            player.sendSystemMessage(Component.literal(" ").append(button("Upgrade all " + upgradable.size() + " hatches",
                    "/gtapexadditions upgrade " + posArg(cpos) + " hatches", "Replace every listed hatch", ChatFormatting.GREEN)));
        if (!any && upgradable.isEmpty())
            player.sendSystemMessage(Component.literal(" Nothing to upgrade (no tiered blocks, and no better hatches in your inventory).")
                    .withStyle(ChatFormatting.GRAY));
    }

    private static int countItem(Player player, Item item) {
        int n = 0;
        for (ItemStack s : player.getInventory().items) if (s.is(item)) n += s.getCount();
        return n;
    }

    private static boolean take(Player player, Item item) {
        if (player.isCreative()) return true;
        for (ItemStack s : player.getInventory().items) {
            if (s.is(item)) {
                s.shrink(1);
                return true;
            }
        }
        return false;
    }

    private static void give(Player player, ItemStack stack) {
        if (player.isCreative() || stack.isEmpty()) return;
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    /** Next-higher tier definition of the same hatch type that the player carries (or any, in creative). */
    @Nullable
    private static MachineDefinition nextTier(IMultiPart part, Player player) {
        if (!(part instanceof ITieredMachine tiered)) return null;
        ResourceLocation id = part.self().getDefinition().getId();
        String path = id.getPath();
        String cur = GTValues.VN[tiered.getTier()].toLowerCase(Locale.ROOT) + "_";
        if (!path.startsWith(cur)) return null;
        String rest = path.substring(cur.length());
        for (int t = GTValues.MAX; t > tiered.getTier(); t--) {
            MachineDefinition def = GTRegistries.MACHINES.get(new ResourceLocation(id.getNamespace(),
                    GTValues.VN[t].toLowerCase(Locale.ROOT) + "_" + rest));
            if (def == null) continue;
            if (player.isCreative() || countItem(player, def.getItem()) > 0) return def;
        }
        return null;
    }

    private static boolean allowed(ServerPlayer player, BlockPos pos) {
        Level level = player.level();
        if (!level.mayInteract(player, pos)) return false;
        var event = new BlockEvent.BreakEvent(level, pos, level.getBlockState(pos), player);
        return !MinecraftForge.EVENT_BUS.post(event);
    }

    private static BlockState copyProps(BlockState from, BlockState to) {
        for (Property<?> prop : from.getProperties()) if (to.hasProperty(prop)) to = copy(from, to, prop);
        return to;
    }

    private static <T extends Comparable<T>> BlockState copy(BlockState from, BlockState to, Property<T> prop) {
        return to.setValue(prop, from.getValue(prop));
    }

    // ------------------------------------------------------------------ actions

    private static IMultiController controllerFor(ServerPlayer player, BlockPos pos) {
        if (player.distanceToSqr(pos.getCenter()) > 32 * 32) return null;
        boolean holding = player.getMainHandItem().is(GTAAItems.STRUCTURE_UPGRADER.get()) ||
                player.getOffhandItem().is(GTAAItems.STRUCTURE_UPGRADER.get());
        if (!holding) {
            player.sendSystemMessage(Component.literal("Hold the Structure Upgrader.").withStyle(ChatFormatting.RED));
            return null;
        }
        return MetaMachine.getMachine(player.level(), pos) instanceof IMultiController c && c.isFormed() ? c : null;
    }

    private static int upgradeFamily(ServerPlayer player, BlockPos cpos, String famId, int tier) {
        IMultiController c = controllerFor(player, cpos);
        if (c == null) return 0;
        Family fam = families().stream().filter(f -> f.id.equals(famId)).findFirst().orElse(null);
        if (fam == null || tier < 1 || tier > fam.tiers.size()) return 0;
        ServerLevel level = player.serverLevel();
        Block target = fam.block(tier);
        int done = 0, missing = 0, blocked = 0;
        for (BlockPos p : c.getMultiblockState().getCache()) {
            BlockState old = level.getBlockState(p);
            int t = fam.tierOf(old.getBlock());
            if (t == 0 || t == tier) continue;
            if (!allowed(player, p)) {
                blocked++;
                continue;
            }
            if (!take(player, target.asItem())) {
                missing++;
                continue;
            }
            level.setBlock(p, copyProps(old, target.defaultBlockState()), Block.UPDATE_ALL);
            give(player, new ItemStack(old.getBlock().asItem()));
            done++;
        }
        player.sendSystemMessage(Component.literal("Swapped " + done + " to " + target.getName().getString() +
                (missing > 0 ? ", " + missing + " skipped (not enough blocks)" : "") +
                (blocked > 0 ? ", " + blocked + " protected" : "")).withStyle(done > 0 ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        return done;
    }

    private static boolean upgradeHatch(ServerPlayer player, IMultiPart part) {
        MachineDefinition next = nextTier(part, player);
        if (next == null) return false;
        ServerLevel level = player.serverLevel();
        BlockPos p = part.self().getPos();
        if (!allowed(player, p)) return false;
        BlockState old = level.getBlockState(p);
        if (!take(player, next.getItem())) return false;
        // the old hatch and anything inside it drop on the ground, so nothing is lost
        level.destroyBlock(p, !player.isCreative(), player);
        level.setBlock(p, copyProps(old, next.getBlock().defaultBlockState()), Block.UPDATE_ALL);
        return true;
    }

    private static int upgradeHatchAt(ServerPlayer player, BlockPos cpos, BlockPos hpos) {
        IMultiController c = controllerFor(player, cpos);
        if (c == null) return 0;
        for (IMultiPart part : c.getParts()) {
            if (part.self().getPos().equals(hpos)) {
                boolean ok = upgradeHatch(player, part);
                player.sendSystemMessage(Component.literal(ok ? "Hatch upgraded." : "Couldn't upgrade that hatch.")
                        .withStyle(ok ? ChatFormatting.GREEN : ChatFormatting.RED));
                return ok ? 1 : 0;
            }
        }
        return 0;
    }

    private static int upgradeAllHatches(ServerPlayer player, BlockPos cpos) {
        IMultiController c = controllerFor(player, cpos);
        if (c == null) return 0;
        int n = 0;
        for (IMultiPart part : new ArrayList<>(c.getParts())) if (upgradeHatch(player, part)) n++;
        player.sendSystemMessage(Component.literal("Upgraded " + n + " hatches.").withStyle(ChatFormatting.GREEN));
        return n;
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("gtapexadditions").then(Commands.literal("upgrade")
                .then(Commands.argument("controller", BlockPosArgument.blockPos())
                        .then(Commands.literal("family").then(Commands.argument("family", StringArgumentType.word())
                                .then(Commands.argument("tier", IntegerArgumentType.integer(1, 64)).executes(ctx ->
                                        upgradeFamily(ctx.getSource().getPlayerOrException(),
                                                BlockPosArgument.getLoadedBlockPos(ctx, "controller"),
                                                StringArgumentType.getString(ctx, "family"),
                                                IntegerArgumentType.getInteger(ctx, "tier"))))))
                        .then(Commands.literal("hatch").then(Commands.argument("hatch", BlockPosArgument.blockPos()).executes(ctx ->
                                upgradeHatchAt(ctx.getSource().getPlayerOrException(),
                                        BlockPosArgument.getLoadedBlockPos(ctx, "controller"),
                                        BlockPosArgument.getLoadedBlockPos(ctx, "hatch")))))
                        .then(Commands.literal("hatches").executes(ctx -> upgradeAllHatches(ctx.getSource().getPlayerOrException(),
                                BlockPosArgument.getLoadedBlockPos(ctx, "controller")))))));
    }
}
