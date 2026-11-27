package com.omnicrox.gtaa.common.item;

import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockState;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.error.PatternError;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
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
import com.omnicrox.gtaa.common.machine.multiblock.elevator.SpaceElevatorMachine;
import com.omnicrox.gtaa.common.network.GTAANetwork;
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
        tooltip.add(Component.literal("Shift + Right-Click any multiblock controller (formed or not)").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Upgrades tiered blocks of any multi and hatches; unformed: shows what's wrong and can build it").withStyle(ChatFormatting.GRAY));
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
        out.add(new Family("suit_casings", "Suit Station Casings", single(GTAABlocks.suitCasings())));
        out.add(new Family("mana_cores", "Mana Cores", single(GTAABlocks.manaCores())));
        out.add(new Family("mca_soil", "Cultivation Soil", grouped(MysticalCultivationArrayMachine.soilTiers())));
        out.add(new Family("mca_glass", "Tiered Glass", grouped(MysticalCultivationArrayMachine.glassTiers())));
        out.add(new Family("se_motors", "Space Elevator Motors", single(GTAABlocks.SE_MOTOR_MK1.get(), GTAABlocks.SE_MOTOR_MK2.get(),
                GTAABlocks.SE_MOTOR_MK3.get(), GTAABlocks.SE_MOTOR_MK4.get(), GTAABlocks.SE_MOTOR_MK5.get())));
        return out;
    }

    /**
     * Families for this machine: the hand-made ones above, plus every tiered slot of its own pattern (any mod's
     * multi): a pattern position that accepts several different blocks, none of them a machine, counts its
     * candidates in the order the pattern lists them (GregTech and addons list tiered blocks lowest first).
     */
    public static List<Family> families(@Nullable IMultiController controller) {
        List<Family> out = families();
        if (controller == null) return out;
        Set<Set<Block>> known = new HashSet<>();
        for (Family f : out) {
            Set<Block> all = new HashSet<>();
            f.tiers().forEach(all::addAll);
            known.add(all);
        }
        int n = 0;
        for (List<Block> cands : patternTierLists(controller)) {
            Set<Block> set = new HashSet<>(cands);
            boolean dup = false;
            for (Set<Block> k : known) if (k.containsAll(set)) { dup = true; break; }
            if (dup) continue;
            known.add(set);
            List<List<Block>> tiers = new ArrayList<>();
            for (Block b : cands) tiers.add(List.of(b));
            out.add(new Family("p" + n++, familyName(cands), tiers));
        }
        return out;
    }

    private static String familyName(List<Block> cands) {
        String a = cands.get(0).getName().getString(), b = cands.get(cands.size() - 1).getName().getString();
        // the words every tier shares make a decent name ("Ring Casing", "Glass"...)
        Set<String> common = new LinkedHashSet<>(Arrays.asList(a.split(" ")));
        common.retainAll(Arrays.asList(b.split(" ")));
        String name = String.join(" ", common).trim();
        return (name.isEmpty() ? a + " .. " + b : name) + " (" + cands.size() + " tiers)";
    }

    /** every distinct multi-block candidate list of the controller's pattern, in pattern order */
    private static List<List<Block>> patternTierLists(IMultiController controller) {
        List<List<Block>> out = new ArrayList<>();
        try {
            BlockPattern pattern = controller.getPattern();
            if (pattern == null) return out;
            java.lang.reflect.Field f = BlockPattern.class.getDeclaredField("blockMatches");
            f.setAccessible(true);
            TraceabilityPredicate[][][] matches = (TraceabilityPredicate[][][]) f.get(pattern);
            Set<List<Block>> seen = new HashSet<>();
            for (TraceabilityPredicate[][] a : matches) for (TraceabilityPredicate[] b : a) for (TraceabilityPredicate tp : b) {
                if (tp == null || tp.isController) continue;
                List<SimplePredicate> preds = new ArrayList<>(tp.common);
                preds.addAll(tp.limited);
                for (SimplePredicate sp : preds) {
                    if (sp.candidates == null) continue;
                    List<Block> blocks = new ArrayList<>();
                    boolean machine = false;
                    for (BlockInfo bi : sp.candidates.get()) {
                        if (bi == null || bi.getBlockState() == null) continue;
                        Block blk = bi.getBlockState().getBlock();
                        if (blk instanceof com.gregtechceu.gtceu.api.block.MetaMachineBlock) { machine = true; break; }
                        if (blk.defaultBlockState().isAir() || blocks.contains(blk)) continue;
                        blocks.add(blk);
                    }
                    if (machine || blocks.size() < 2) continue;
                    if (seen.add(blocks)) out.add(blocks);
                }
            }
        } catch (Throwable t) {
            com.omnicrox.gtaa.GTAA.LOGGER.debug("[GTAA] Structure Upgrader couldn't read a pattern: {}", t.toString());
        }
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

    public static void promptForTest(ServerPlayer player, IMultiController controller) {
        prompt(player, controller);
    }

    static void prompt(ServerPlayer player, IMultiController controller) {
        if (!controller.isFormed()) {
            diagnose(player, controller);
            return;
        }
        if (GTAANetwork.CHANNEL.isRemotePresent(player.connection.connection)) {
            screen(player, controller);
            return;
        }
        chatPrompt(player, controller);
    }

    private static String itemId(net.minecraft.world.level.ItemLike item) {
        return net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.asItem()).toString();
    }

    /** Tier counts of one family in this structure: tier -> blocks. */
    private static Map<Integer, Integer> census(Family fam, Level level, Iterable<BlockPos> cache) {
        Map<Integer, Integer> count = new LinkedHashMap<>();
        for (BlockPos p : cache) {
            int t = fam.tierOf(level.getBlockState(p).getBlock());
            if (t > 0) count.merge(t, 1, Integer::sum);
        }
        return count;
    }

    /**
     * The Structure Upgrader screen: a status line for the machine, then every tiered block family and every hatch
     * you carry a better version of, each saying what it does here, whether it's needed (recipes locked) or only
     * makes the machine faster, and what each tier changes.
     */
    private static void screen(ServerPlayer player, IMultiController controller) {
        BlockPos cpos = controller.self().getPos();
        Level level = controller.self().getLevel();
        var cache = controller.getMultiblockState().getCache();
        UpgradeAdvisor advisor = new UpgradeAdvisor(controller);
        List<GTAANetwork.Row> rows = new ArrayList<>();
        List<String> needs = new ArrayList<>();

        List<GTAANetwork.Row> familyRows = new ArrayList<>();
        for (Family fam : families(controller)) {
            Map<Integer, Integer> count = census(fam, level, cache);
            if (count.isEmpty()) continue;
            int total = count.values().stream().mapToInt(i -> i).sum();
            int cur = count.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
            boolean mixed = count.size() > 1;
            UpgradeAdvisor.FamilyAdvice adv = advisor.family(fam, cur);
            List<GTAANetwork.Choice> choices = new ArrayList<>();
            // upgrades first (recommended one stands out), downgrades last
            List<Integer> order = new ArrayList<>();
            for (int t = cur + 1; t <= fam.tiers.size(); t++) order.add(t);
            if (mixed) order.add(cur);
            // downgrades only when nothing is above (keeps the screen short)
            if (order.isEmpty()) for (int t = cur - 1; t >= 1; t--) order.add(t);
            for (int t : order) {
                Block b = fam.block(t);
                int have = player.isCreative() ? total : countItem(player, b.asItem());
                String detail = adv.details().get(t - 1) + "\nSwaps all " + total + " blocks; you have " + have + ".";
                choices.add(new GTAANetwork.Choice(b.getName().getString(),
                        "/gtapexadditions upgrade " + posArg(cpos) + " family " + fam.id + " " + t,
                        detail, have >= total, itemId(b), adv.tags().get(t - 1)));
            }
            String verdictText = (mixed ? "Mixed tiers: the structure uses its lowest. " : "") + adv.verdictText();
            if (adv.verdict() == UpgradeAdvisor.NEEDED) needs.add(fam.name);
            Block curBlock = fam.block(cur);
            familyRows.add(new GTAANetwork.Row(fam.name + " \u00d7" + total, curBlock.getName().getString(), itemId(curBlock),
                    choices, adv.info(), adv.verdict(), verdictText));
        }

        List<GTAANetwork.Row> partRows = new ArrayList<>();
        List<IMultiPart> upgradable = new ArrayList<>();
        for (IMultiPart part : controller.getParts()) {
            MachineDefinition next = nextTier(part, player);
            if (next == null) continue;
            upgradable.add(part);
            BlockPos pp = part.self().getPos();
            int nextTier = next.getTier();
            UpgradeAdvisor.PartAdvice adv = advisor.part(part, nextTier);
            if (adv.verdict() == UpgradeAdvisor.NEEDED && !needs.contains("Energy hatches")) needs.add("Energy hatches");
            partRows.add(new GTAANetwork.Row("Hatch", part.self().getDefinition().getBlock().getName().getString(),
                    itemId(part.self().getDefinition().getBlock()),
                    List.of(new GTAANetwork.Choice(next.getBlock().getName().getString(),
                            "/gtapexadditions upgrade " + posArg(cpos) + " hatch " + posArg(pp),
                            adv.detail(), true, itemId(next.getBlock()), adv.tag())),
                    adv.info(), adv.verdict(), adv.verdictText()));
        }

        List<GTAANetwork.Row> moduleRows = controller instanceof SpaceElevatorMachine se ? moduleRows(player, se) : List.of();

        // status first: what this machine can't run right now and why
        List<String> status = new ArrayList<>();
        int locked = advisor.powerLocked();
        if (advisor.recipeCount() > 0) status.add(advisor.recipeCount() + " recipes in this machine" +
                (advisor.maxVoltage() > 0 ? "; your energy hatches give up to " +
                        com.gregtechceu.gtceu.api.GTValues.VN[voltageTier(advisor.maxVoltage())] + "." : "."));
        if (locked > 0) status.add(locked + " of them need more power than that.");
        String verdict = needs.isEmpty() ? "Nothing here is required. Everything listed only makes it faster or bigger."
                : "To unlock locked recipes: " + String.join(", ", needs) + ".";
        rows.add(new GTAANetwork.Row("Status", "", "", List.of(), status,
                needs.isEmpty() ? UpgradeAdvisor.OPTIONAL : UpgradeAdvisor.NEEDED, verdict));
        if (controller instanceof com.omnicrox.apexlib.multiblock.IStagedMulti sm) {
            int cur = sm.currentStage();
            List<GTAANetwork.Choice> grow = new ArrayList<>();
            for (int st = cur + 1; st <= sm.stageCount(); st++)
                grow.add(new GTAANetwork.Choice("Build " + sm.stageName(st), "/gtapexadditions upgrade " + posArg(cpos) + " stage " + st,
                        "Stage " + st + " adds " + sm.stageAdds(st) + ".\nPlaces what's missing from your inventory (creative: free).",
                        true, "", st == cur + 1 ? "★" : ""));
            List<String> info = new ArrayList<>();
            for (int st = 1; st <= sm.stageCount(); st++) info.add(st + ". " + sm.stageName(st) + ": " + sm.stageAdds(st));
            rows.add(new GTAANetwork.Row("Growth stage", cur + "/" + sm.stageCount() + " " + sm.stageName(Math.max(1, cur)), "",
                    grow, info, cur >= sm.stageCount() ? UpgradeAdvisor.MAXED : UpgradeAdvisor.OPTIONAL,
                    cur >= sm.stageCount() ? "Fully grown." : "Each stage raises what it can do (up to its core tier)."));
        }
        if (!familyRows.isEmpty() || !upgradable.isEmpty())
            rows.add(new GTAANetwork.Row("Everything", "best you can afford", "",
                    List.of(new GTAANetwork.Choice("Upgrade all to max",
                            "/gtapexadditions upgrade " + posArg(cpos) + " max",
                            "Every tiered block family goes to the highest tier you carry enough of for the whole structure" +
                                    (player.isCreative() ? " (creative: the top tier)" : "") +
                                    ", then every hatch to the best one you carry.", true, "", "★")),
                    List.of(), UpgradeAdvisor.OPTIONAL, ""));
        familyRows.sort(Comparator.comparingInt((GTAANetwork.Row r) -> -r.verdict()));
        partRows.sort(Comparator.comparingInt((GTAANetwork.Row r) -> -r.verdict()));
        rows.addAll(familyRows);
        rows.addAll(moduleRows);
        rows.addAll(partRows);
        if (upgradable.size() > 1)
            rows.add(new GTAANetwork.Row("All hatches", upgradable.size() + " upgradable", "",
                    List.of(new GTAANetwork.Choice("Upgrade all " + upgradable.size(),
                            "/gtapexadditions upgrade " + posArg(cpos) + " hatches",
                            "Replace every listed hatch with the best one you carry. Old hatches and their contents drop on the ground.",
                            true, "", "")),
                    List.of(), UpgradeAdvisor.OPTIONAL, ""));
        GTAANetwork.send(player, new GTAANetwork.UpgraderScreenPacket(
                controller.self().getDefinition().getBlock().getName().getString(), rows,
                "Hover a row or button for details  |  blocks come from your inventory, old ones go back"));
    }

    private static int voltageTier(long v) {
        for (int t = 0; t < com.gregtechceu.gtceu.api.GTValues.V.length; t++)
            if (com.gregtechceu.gtceu.api.GTValues.V[t] >= v) return t;
        return com.gregtechceu.gtceu.api.GTValues.MAX;
    }

    private static void chatPrompt(ServerPlayer player, IMultiController controller) {
        BlockPos cpos = controller.self().getPos();
        Level level = controller.self().getLevel();
        var cache = controller.getMultiblockState().getCache();
        player.sendSystemMessage(Component.literal("Structure Upgrader: ").withStyle(ChatFormatting.GOLD)
                .append(controller.self().getDefinition().getBlock().getName().copy().withStyle(ChatFormatting.WHITE)));
        boolean any = false;
        for (Family fam : families(controller)) {
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
            UpgradeAdvisor.FamilyAdvice adv = new UpgradeAdvisor(controller).family(fam, cur);
            player.sendSystemMessage(Component.literal("   " + adv.verdictText()).withStyle(
                    adv.verdict() == UpgradeAdvisor.NEEDED ? ChatFormatting.RED : ChatFormatting.DARK_GRAY));
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

    /** The next tier up of the same hatch type that the player carries (creative: simply the next tier). */
    @Nullable
    private static MachineDefinition nextTier(IMultiPart part, Player player) {
        if (!(part instanceof ITieredMachine tiered)) return null;
        ResourceLocation id = part.self().getDefinition().getId();
        String path = id.getPath();
        String cur = GTValues.VN[tiered.getTier()].toLowerCase(Locale.ROOT) + "_";
        if (!path.startsWith(cur)) return null;
        String rest = path.substring(cur.length());
        for (int t = tiered.getTier() + 1; t <= GTValues.MAX; t++) {
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
        Family fam = families(c).stream().filter(f -> f.id.equals(famId)).findFirst().orElse(null);
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


    /** every family to the highest tier the player can swap the whole structure to, then every hatch */
    private static int upgradeMax(ServerPlayer player, BlockPos cpos) {
        IMultiController c = controllerFor(player, cpos);
        if (c == null) return 0;
        Level level = c.self().getLevel();
        int changed = 0;
        for (Family fam : families(c)) {
            Map<Integer, Integer> count = census(fam, level, c.getMultiblockState().getCache());
            if (count.isEmpty()) continue;
            int total = count.values().stream().mapToInt(i -> i).sum();
            int cur = count.keySet().stream().min(Integer::compare).orElse(1);
            int best = 0;
            for (int t = fam.tiers().size(); t > cur; t--) {
                if (player.isCreative() || countItem(player, fam.block(t).asItem()) >= total) { best = t; break; }
            }
            if (best > 0) changed += upgradeFamily(player, cpos, fam.id(), best);
        }
        changed += upgradeAllHatches(player, cpos);
        if (changed == 0) player.sendSystemMessage(Component.literal("Nothing to upgrade with what you carry.").withStyle(ChatFormatting.YELLOW));
        return changed;
    }

    /**
     * An unformed multi: check its pattern and say exactly what's wrong (the first block that doesn't fit, where it
     * is relative to the controller, and what it accepts), mark the spot with particles, and offer to build the
     * missing blocks from your inventory.
     */
    private static void diagnose(ServerPlayer player, IMultiController controller) {
        BlockPos cpos = controller.self().getPos();
        ServerLevel level = player.serverLevel();
        String name = controller.self().getDefinition().getBlock().getName().getString();
        BlockPattern pattern = controller.getPattern();
        if (pattern == null) {
            player.sendSystemMessage(Component.literal(name + " has no structure to check.").withStyle(ChatFormatting.RED));
            return;
        }
        MultiblockState state = controller.getMultiblockState();
        boolean ok;
        try {
            ok = pattern.checkPatternAt(state, false);
        } catch (Throwable t) {
            ok = false;
        }
        player.sendSystemMessage(Component.literal("Structure Upgrader: ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(name).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(ok ? "  structure is complete, it forms on the next check" : "  not formed").withStyle(
                        ok ? ChatFormatting.GREEN : ChatFormatting.RED)));
        if (!ok && state.error != null) {
            PatternError err = state.error;
            BlockPos at = null;
            try { at = err.getPos(); } catch (Throwable ignored) {}
            if (at != null) {
                BlockState found = level.getBlockState(at);
                player.sendSystemMessage(Component.literal(" Wrong block at " + posArg(at) + " (" + relative(cpos, at, controller) + ")")
                        .withStyle(ChatFormatting.YELLOW));
                player.sendSystemMessage(Component.literal("  found: ").withStyle(ChatFormatting.GRAY)
                        .append((found.isAir() ? Component.literal("nothing") : found.getBlock().getName().copy()).withStyle(ChatFormatting.WHITE)));
                mark(player, level, at);
            }
            try {
                MutableComponent want = Component.literal("  needs: ").withStyle(ChatFormatting.GRAY);
                int shown = 0;
                for (List<ItemStack> group : err.getCandidates()) for (ItemStack st : group) {
                    if (st.isEmpty() || shown >= 8) continue;
                    if (shown++ > 0) want.append(Component.literal(" / ").withStyle(ChatFormatting.DARK_GRAY));
                    want.append(st.getHoverName().copy().withStyle(ChatFormatting.AQUA));
                }
                if (shown > 0) player.sendSystemMessage(want);
            } catch (Throwable ignored) {}
            try {
                Component info = err.getErrorInfo();
                if (info != null) player.sendSystemMessage(Component.literal("  ").append(info).withStyle(ChatFormatting.DARK_GRAY));
            } catch (Throwable ignored) {}
        }
        player.sendSystemMessage(Component.literal(" ").append(button("Build missing blocks",
                "/gtapexadditions upgrade " + posArg(cpos) + " build",
                "Places every missing block of the structure from your inventory (creative: free), like a GregTech Terminal.",
                ChatFormatting.GREEN)).append(Component.literal("  ")).append(button("Check again",
                "/gtapexadditions upgrade " + posArg(cpos) + " check", "Re-check the structure", ChatFormatting.AQUA)));
    }

    /** "3 left, 1 up, 2 behind" from the controller's point of view */
    private static String relative(BlockPos c, BlockPos p, IMultiController controller) {
        net.minecraft.core.Direction front = controller.self().getFrontFacing();
        if (!front.getAxis().isHorizontal()) front = net.minecraft.core.Direction.NORTH;
        net.minecraft.core.Direction right = front.getCounterClockWise();
        int dx = p.getX() - c.getX(), dy = p.getY() - c.getY(), dz = p.getZ() - c.getZ();
        int r = dx * right.getStepX() + dz * right.getStepZ();
        int f = dx * front.getStepX() + dz * front.getStepZ();
        List<String> parts = new ArrayList<>();
        if (r != 0) parts.add(Math.abs(r) + (r > 0 ? " right" : " left"));
        if (dy != 0) parts.add(Math.abs(dy) + (dy > 0 ? " up" : " down"));
        if (f != 0) parts.add(Math.abs(f) + (f > 0 ? " in front" : " behind"));
        return parts.isEmpty() ? "the controller" : String.join(", ", parts) + " of the controller, looking at its face";
    }

    /** a column of particles on the wrong block, visible from far away */
    private static void mark(ServerPlayer player, ServerLevel level, BlockPos at) {
        for (int i = 0; i < 12; i++) {
            double y = at.getY() + 0.5 + i * 0.35;
            level.sendParticles(player, net.minecraft.core.particles.ParticleTypes.END_ROD, true,
                    at.getX() + 0.5, y, at.getZ() + 0.5, 3, 0.15, 0.05, 0.15, 0.0);
        }
        level.sendParticles(player, net.minecraft.core.particles.ParticleTypes.FLAME, true,
                at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, 30, 0.35, 0.35, 0.35, 0.0);
    }

    private static int build(ServerPlayer player, BlockPos cpos) {
        if (!(MetaMachine.getMachine(player.level(), cpos) instanceof IMultiController c)) return 0;
        if (!allowed(player, cpos)) return 0;
        BlockPattern pattern = c.getPattern();
        if (pattern == null) return 0;
        try {
            pattern.autoBuild(player, c.getMultiblockState());
        } catch (Throwable t) {
            player.sendSystemMessage(Component.literal("Couldn't build it: " + t.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
        c.checkPatternWithLock();
        if (!c.isFormed()) diagnose(player, c);
        else player.sendSystemMessage(Component.literal("Formed.").withStyle(ChatFormatting.GREEN));
        return 1;
    }

    /** staged multis: build stage N from the inventory and say what's still missing */
    private static int buildStage(ServerPlayer player, BlockPos cpos, int st) {
        if (!(MetaMachine.getMachine(player.level(), cpos) instanceof IMultiController c)) return 0;
        if (!(c instanceof com.omnicrox.apexlib.multiblock.IStagedMulti sm) || !allowed(player, cpos)) return 0;
        BlockPattern pattern = sm.stagePattern(st);
        try {
            pattern.autoBuild(player, c.getMultiblockState());
        } catch (Throwable t) {
            player.sendSystemMessage(Component.literal("Couldn't build it: " + t.getMessage()).withStyle(ChatFormatting.RED));
            return 0;
        }
        MultiblockState state = c.getMultiblockState();
        boolean ok = pattern.checkPatternAt(state, false);
        if (!ok && state.error != null) {
            BlockPos at = null;
            try { at = state.error.getPos(); } catch (Throwable ignored) {}
            MutableComponent msg = Component.literal(sm.stageName(st) + " stage isn't complete").withStyle(ChatFormatting.YELLOW);
            if (at != null) {
                msg.append(Component.literal(": " + posArg(at) + " (" + relative(cpos, at, c) + ") needs ").withStyle(ChatFormatting.GRAY));
                try {
                    var cands = state.error.getCandidates();
                    if (!cands.isEmpty() && !cands.get(0).isEmpty())
                        msg.append(cands.get(0).get(0).getHoverName().copy().withStyle(ChatFormatting.AQUA));
                } catch (Throwable ignored) {}
                mark(player, player.serverLevel(), at);
            }
            player.sendSystemMessage(msg);
        }
        c.checkPatternWithLock();
        if (ok) player.sendSystemMessage(Component.literal(sm.stageName(st) + " stage built.").withStyle(ChatFormatting.GREEN));
        return ok ? 1 : 0;
    }

    // ------------------------------------------------------------------ Space Elevator modules

    private static java.util.List<MachineDefinition> moduleDefs() {
        java.util.List<MachineDefinition> out = new ArrayList<>();
        for (MachineDefinition d : GTRegistries.MACHINES)
            if (d.getId().getNamespace().equals("gtapexadditions") && d.getId().getPath().matches(".*_module_mk\\d+")) out.add(d);
        out.sort(Comparator.comparing(d -> d.getId().getPath()));
        return out;
    }

    /** the next Mk of the same module the player carries (or any, in creative) */
    private static MachineDefinition nextModule(MachineDefinition cur, Player player) {
        String path = cur.getId().getPath();
        int mk = Integer.parseInt(path.replaceAll(".*_mk(\\d+)$", "$1"));
        String base = path.replaceAll("_mk\\d+$", "_mk");
        for (int m = mk + 1; m <= 9; m++) {
            MachineDefinition d = GTRegistries.MACHINES.get(new ResourceLocation(cur.getId().getNamespace(), base + m));
            if (d != null && (player.isCreative() || countItem(player, d.getItem()) > 0)) return d;
        }
        return null;
    }

    private static List<GTAANetwork.Row> moduleRows(ServerPlayer player, SpaceElevatorMachine se) {
        List<GTAANetwork.Row> rows = new ArrayList<>();
        BlockPos cpos = se.getPos();
        Level level = se.getLevel();
        int free = 0;
        for (BlockPos p : se.getSlotPositions()) if (level.getBlockState(p).is(GTAABlocks.SE_BASE_CASING.get())) free++;
        int slots = SpaceElevatorMachine.moduleSlots(se.getMotorTier());
        int installed = se.getModulePositions().size();
        List<GTAANetwork.Choice> place = new ArrayList<>();
        for (MachineDefinition d : moduleDefs()) {
            int have = player.isCreative() ? 1 : countItem(player, d.getItem());
            if (have <= 0) continue;
            int needMotor = com.omnicrox.gtaa.common.data.GTAAMachines.SE_MODULE_MOTOR.getOrDefault(d.getId().getPath(), 1);
            place.add(new GTAANetwork.Choice("Place " + d.getBlock().getName().getString(),
                    "/gtapexadditions upgrade " + posArg(cpos) + " module " + d.getId().getPath(),
                    "Puts this module in the next free slot, facing outward, and builds its casing/hatch frame from your inventory." +
                            (needMotor > se.getMotorTier() ? "\nNeeds Motor Mk-" + needMotor + "; it stays disconnected until then." : ""),
                    free > 0 && installed < slots, itemId(d.getBlock()), ""));
        }
        List<String> info = new ArrayList<>();
        info.add(installed + " of " + slots + " module slots used (motor tier " + se.getMotorTier() + "), " + free + " free slot spots in the base.");
        if (place.isEmpty()) info.add("Carry a module controller to place it from here.");
        rows.add(new GTAANetwork.Row("Modules", installed + " / " + slots, "", place, info, UpgradeAdvisor.OPTIONAL,
                installed >= slots ? "All motor slots used: better motors give more." : ""));
        for (BlockPos p : se.getModulePositions()) {
            if (!(MetaMachine.getMachine(level, p) instanceof IMultiController m)) continue;
            MachineDefinition cur = m.self().getDefinition();
            MachineDefinition next = nextModule(cur, player);
            if (next == null) continue;
            rows.add(new GTAANetwork.Row("Module", cur.getBlock().getName().getString(), itemId(cur.getBlock()),
                    List.of(new GTAANetwork.Choice(next.getBlock().getName().getString(),
                            "/gtapexadditions upgrade " + posArg(cpos) + " moduleup " + posArg(p),
                            "Swaps this module for the next Mk you carry; its hatches stay. The old controller goes back to you.",
                            true, itemId(next.getBlock()), "")),
                    List.of("Higher Mk modules run more parallels and higher-tier recipes."), UpgradeAdvisor.OPTIONAL, ""));
        }
        return rows;
    }

    private static BlockState facing(MachineDefinition d, net.minecraft.core.Direction dir) {
        BlockState st = d.getBlock().defaultBlockState();
        for (Property<?> prop : st.getProperties())
            if (prop instanceof net.minecraft.world.level.block.state.properties.DirectionProperty dp && dp.getPossibleValues().contains(dir))
                return st.setValue(dp, dir);
        return st;
    }

    private static net.minecraft.core.Direction outward(SpaceElevatorMachine se, BlockPos p) {
        BlockPos c = se.getCenter();
        int dx = p.getX() - c.getX(), dz = p.getZ() - c.getZ();
        if (Math.abs(dx) >= Math.abs(dz)) return dx >= 0 ? net.minecraft.core.Direction.EAST : net.minecraft.core.Direction.WEST;
        return dz >= 0 ? net.minecraft.core.Direction.SOUTH : net.minecraft.core.Direction.NORTH;
    }

    private static void finishModule(ServerPlayer player, BlockPos p) {
        if (MetaMachine.getMachine(player.level(), p) instanceof IMultiController m) {
            m.self().setOwnerUUID(player.getUUID());
            if (!m.isFormed()) m.getPattern().autoBuild(player, m.getMultiblockState());
        }
    }

    private static int placeModule(ServerPlayer player, BlockPos cpos, String path) {
        if (!(controllerFor(player, cpos) instanceof SpaceElevatorMachine se)) return 0;
        MachineDefinition d = GTRegistries.MACHINES.get(new ResourceLocation("gtapexadditions", path));
        if (d == null) return 0;
        if (se.getModulePositions().size() >= SpaceElevatorMachine.moduleSlots(se.getMotorTier())) {
            player.sendSystemMessage(Component.literal("Every slot this motor tier allows is used.").withStyle(ChatFormatting.RED));
            return 0;
        }
        ServerLevel level = player.serverLevel();
        // nearest free slot to the player
        BlockPos best = null;
        for (BlockPos p : se.getSlotPositions())
            if (level.getBlockState(p).is(GTAABlocks.SE_BASE_CASING.get()) && allowed(player, p) &&
                    (best == null || p.distSqr(player.blockPosition()) < best.distSqr(player.blockPosition()))) best = p;
        if (best == null) {
            player.sendSystemMessage(Component.literal("No free module slot.").withStyle(ChatFormatting.RED));
            return 0;
        }
        if (!take(player, d.getItem())) {
            player.sendSystemMessage(Component.literal("You don't carry that module.").withStyle(ChatFormatting.RED));
            return 0;
        }
        BlockState old = level.getBlockState(best);
        // a module's frame sits on the elevator base behind it: use the facing whose frame fits (inward, normally)
        net.minecraft.core.Direction dir = outward(se, best).getOpposite();
        for (net.minecraft.core.Direction d2 : java.util.List.of(dir, dir.getOpposite(), dir.getClockWise(), dir.getCounterClockWise())) {
            level.setBlock(best, facing(d, d2), Block.UPDATE_ALL);
            if (MetaMachine.getMachine(level, best) instanceof IMultiController mc && mc.checkPattern()) {
                dir = d2;
                break;
            }
        }
        level.setBlock(best, facing(d, dir), Block.UPDATE_ALL);
        give(player, new ItemStack(old.getBlock().asItem()));
        finishModule(player, best);
        player.sendSystemMessage(Component.literal("Placed " + d.getBlock().getName().getString() + ".").withStyle(ChatFormatting.GREEN));
        return 1;
    }

    private static int upgradeModule(ServerPlayer player, BlockPos cpos, BlockPos mpos) {
        if (!(controllerFor(player, cpos) instanceof SpaceElevatorMachine se) || !se.getModulePositions().contains(mpos)) return 0;
        if (!(MetaMachine.getMachine(player.level(), mpos) instanceof IMultiController m)) return 0;
        MachineDefinition next = nextModule(m.self().getDefinition(), player);
        if (next == null || !allowed(player, mpos) || !take(player, next.getItem())) return 0;
        ServerLevel level = player.serverLevel();
        BlockState old = level.getBlockState(mpos);
        level.destroyBlock(mpos, !player.isCreative(), player);
        level.setBlock(mpos, copyProps(old, next.getBlock().defaultBlockState()), Block.UPDATE_ALL);
        finishModule(player, mpos);
        player.sendSystemMessage(Component.literal("Module upgraded to " + next.getBlock().getName().getString() + ".").withStyle(ChatFormatting.GREEN));
        return 1;
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
                                BlockPosArgument.getLoadedBlockPos(ctx, "controller"))))
                        .then(Commands.literal("max").executes(ctx -> upgradeMax(ctx.getSource().getPlayerOrException(),
                                BlockPosArgument.getLoadedBlockPos(ctx, "controller"))))
                        .then(Commands.literal("build").executes(ctx -> build(ctx.getSource().getPlayerOrException(),
                                BlockPosArgument.getLoadedBlockPos(ctx, "controller"))))
                        .then(Commands.literal("stage").then(Commands.argument("stage", IntegerArgumentType.integer(1, 16)).executes(ctx ->
                                buildStage(ctx.getSource().getPlayerOrException(), BlockPosArgument.getLoadedBlockPos(ctx, "controller"),
                                        IntegerArgumentType.getInteger(ctx, "stage")))))
                        .then(Commands.literal("check").executes(ctx -> {
                            ServerPlayer pl = ctx.getSource().getPlayerOrException();
                            if (MetaMachine.getMachine(pl.level(), BlockPosArgument.getLoadedBlockPos(ctx, "controller")) instanceof IMultiController mc)
                                prompt(pl, mc);
                            return 1;
                        }))
                        .then(Commands.literal("module").then(Commands.argument("module", StringArgumentType.word()).executes(ctx ->
                                placeModule(ctx.getSource().getPlayerOrException(), BlockPosArgument.getLoadedBlockPos(ctx, "controller"),
                                        StringArgumentType.getString(ctx, "module")))))
                        .then(Commands.literal("moduleup").then(Commands.argument("module", BlockPosArgument.blockPos()).executes(ctx ->
                                upgradeModule(ctx.getSource().getPlayerOrException(), BlockPosArgument.getLoadedBlockPos(ctx, "controller"),
                                        BlockPosArgument.getLoadedBlockPos(ctx, "module"))))))));
    }
}
