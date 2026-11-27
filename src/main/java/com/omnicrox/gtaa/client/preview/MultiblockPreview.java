package com.omnicrox.gtaa.client.preview;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.block.ICoilType;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.predicates.SimplePredicate;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.lowdragmc.lowdraglib.utils.TrackedDummyWorld;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.joml.Vector3f;

import java.util.*;

/**
 * One multiblock's interactive preview (GT New Horizons NEI style): a 3D scene of one of its shapes, with a layer
 * slider, a tier slider for tiered blocks (coils, field generators...), a hatch toggle, rotation, zoom and pan.
 * Kept in a small cache by {@link #of}; its scene is built lazily and rebuilt when an option changes.
 */
public final class MultiblockPreview {

    // ---------------- tier families: blocks the tier slider swaps (tier lists, lowest first) ----------------
    /** each family: its tiers, lowest first; a tier can hold several interchangeable blocks */
    private static final List<List<List<Block>>> FAMILIES = new ArrayList<>();
    private static boolean familiesLoaded;
    /** other code (the Structure Upgrader's families) adds its tier lists here */
    public static final List<java.util.function.Supplier<List<List<List<Block>>>>> FAMILY_SOURCES = new ArrayList<>();

    private static synchronized List<List<List<Block>>> families() {
        if (!familiesLoaded) {
            familiesLoaded = true;
            try {
                List<Map.Entry<ICoilType, java.util.function.Supplier<? extends Block>>> coils = new ArrayList<>();
                for (var e : GTCEuAPI.HEATING_COILS.entrySet()) coils.add(Map.entry(e.getKey(), e.getValue()));
                coils.sort(Comparator.comparingInt(e -> e.getKey().getTier()));
                List<List<Block>> c = new ArrayList<>();
                for (var e : coils) c.add(List.of(e.getValue().get()));
                if (c.size() > 1) FAMILIES.add(c);
            } catch (Throwable ignored) {}
            for (var src : FAMILY_SOURCES) {
                try {
                    for (List<List<Block>> f : src.get())
                        if (f.size() > 1 && FAMILIES.stream().noneMatch(x -> x.equals(f))) FAMILIES.add(f);
                } catch (Throwable ignored) {}
            }
        }
        return FAMILIES;
    }

    private static int tierIn(List<List<Block>> fam, Block b) {
        for (int i = 0; i < fam.size(); i++) if (fam.get(i).contains(b)) return i;
        return -1;
    }

    // ---------------- cache ----------------
    private static final int CACHE = 8;
    private static final LinkedHashMap<MultiblockMachineDefinition, MultiblockPreview> PREVIEWS = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<MultiblockMachineDefinition, MultiblockPreview> e) {
            if (size() > CACHE) {
                e.getValue().release();
                return true;
            }
            return false;
        }
    };

    public static MultiblockPreview of(MultiblockMachineDefinition def) {
        return PREVIEWS.computeIfAbsent(def, MultiblockPreview::new);
    }

    public static void releaseAll() {
        PREVIEWS.values().forEach(MultiblockPreview::release);
        PREVIEWS.clear();
    }

    // ---------------- state ----------------
    public final MultiblockMachineDefinition def;
    private List<MultiblockShapeInfo> shapes;
    public int page;
    /** 0 = every layer, otherwise one layer counted from the bottom */
    public int layer;
    /** -1 = as designed, otherwise the tier index for every tier family */
    public int tier = -1;
    public boolean hatches = true;
    public float yaw = -135, pitch = 25, zoom = 1;
    public final Vector3f pan = new Vector3f();

    private TrackedDummyWorld world;
    private ApexSceneRenderer renderer;
    private int fboW, fboH;
    private boolean dirty = true;
    private final Map<BlockPos, BlockInfo> placed = new HashMap<>();
    private Map<BlockPos, TraceabilityPredicate> predicates = Map.of();
    private int minY, maxY, maxTier;
    private float baseRadius = 10;
    private final Vector3f center = new Vector3f();
    private String error;

    private MultiblockPreview(MultiblockMachineDefinition def) {
        this.def = def;
    }

    public List<MultiblockShapeInfo> shapes() {
        if (shapes == null) {
            try {
                shapes = def.getMatchingShapes();
            } catch (Throwable t) {
                shapes = List.of();
                error = "Couldn't build the preview: " + t;
            }
        }
        return shapes;
    }

    public int pages() {
        return Math.max(1, shapes().size());
    }

    public int layers() {
        return maxY - minY + 1;
    }

    public int maxTier() {
        return maxTier;
    }

    public void markDirty() {
        dirty = true;
    }

    public void resetView() {
        yaw = -135;
        pitch = 25;
        zoom = 1;
        pan.set(0);
    }

    public String error() {
        return error;
    }

    void release() {
        if (renderer != null) {
            RenderSystem.recordRenderCall(() -> {
                try {
                    renderer.deleteCacheBuffer();
                    renderer.release();
                } catch (Throwable ignored) {}
            });
        }
        renderer = null;
        world = null;
    }

    // ---------------- building the scene ----------------
    private static final BlockPos ORIGIN = new BlockPos(0, 64, 0);

    private BlockInfo transform(BlockInfo info, boolean controllerCell) {
        BlockState s = info.getBlockState();
        Block b = s.getBlock();
        if (!controllerCell && !hatches && b instanceof MetaMachineBlock) {
            try {
                BlockState app = def.getAppearance() == null ? null : def.getAppearance().get();
                if (app != null) return BlockInfo.fromBlockState(app);
            } catch (Throwable ignored) {}
        }
        if (tier >= 0) {
            for (List<List<Block>> fam : families()) {
                if (tierIn(fam, b) >= 0) return BlockInfo.fromBlockState(fam.get(Math.min(tier, fam.size() - 1)).get(0).defaultBlockState());
            }
        }
        return info;
    }

    private void build() {
        dirty = false;
        placed.clear();
        predicates = Map.of();
        world = new TrackedDummyWorld();
        List<MultiblockShapeInfo> sh = shapes();
        if (sh.isEmpty()) return;
        page = Math.floorMod(page, sh.size());
        BlockInfo[][][] blocks = sh.get(page).getBlocks();
        IMultiController controller = null;
        BlockPos controllerPos = null;
        int mnX = Integer.MAX_VALUE, mnY = Integer.MAX_VALUE, mnZ = Integer.MAX_VALUE;
        int mxX = Integer.MIN_VALUE, mxY = Integer.MIN_VALUE, mxZ = Integer.MIN_VALUE;
        maxTier = 0;
        List<List<List<Block>>> fams = families();
        for (int x = 0; x < blocks.length; x++)
            for (int y = 0; y < blocks[x].length; y++)
                for (int z = 0; z < blocks[x][y].length; z++) {
                    BlockInfo info = blocks[x][y][z];
                    if (info == null) continue;
                    BlockState s = info.getBlockState();
                    if (s.isAir()) continue;
                    BlockPos pos = ORIGIN.offset(x, y, z);
                    boolean isController = s.getBlock() == def.get().self();
                    for (List<List<Block>> f : fams) if (tierIn(f, s.getBlock()) >= 0) maxTier = Math.max(maxTier, f.size());
                    BlockInfo use = transform(info, isController);
                    if (isController) {
                        use = BlockInfo.fromBlockState(s);
                        controllerPos = pos;
                    }
                    placed.put(pos, use);
                    mnX = Math.min(mnX, pos.getX()); mnY = Math.min(mnY, pos.getY()); mnZ = Math.min(mnZ, pos.getZ());
                    mxX = Math.max(mxX, pos.getX()); mxY = Math.max(mxY, pos.getY()); mxZ = Math.max(mxZ, pos.getZ());
                }
        if (placed.isEmpty()) return;
        minY = mnY;
        maxY = mxY;
        world.addBlocks(placed);
        if (controllerPos != null) {
            try {
                var be = world.getBlockEntity(controllerPos);
                if (be != null) {
                    be.setLevel(world);
                    world.setInnerBlockEntity(be);
                }
                if (be instanceof IMachineBlockEntity holder && holder.getMetaMachine() instanceof IMultiController c) controller = c;
            } catch (Throwable ignored) {}
        }
        // form it, so the hatches take the casing look and we learn what every spot accepts
        if (controller != null && hatches && tier < 0) {
            try {
                if (controller.checkPattern() && controller.getPattern() != null &&
                        controller.getPattern().checkPatternAt(controller.getMultiblockState(), true)) {
                    controller.onStructureFormed();
                    Map<BlockPos, TraceabilityPredicate> p = controller.getMultiblockState().getMatchContext().get("predicates");
                    if (p != null) predicates = new HashMap<>(p);
                }
            } catch (Throwable ignored) {}
        }
        center.set((mnX + mxX + 1) / 2f, (mnY + mxY + 1) / 2f, (mnZ + mxZ + 1) / 2f);
        int dim = Math.max(mxX - mnX, Math.max(mxY - mnY, mxZ - mnZ)) + 1;
        baseRadius = Math.max(4f, dim * 1.25f + 2f);
        if (layer > layers()) layer = 0;
        if (renderer != null) {
            try {
                renderer.deleteCacheBuffer();
                renderer.release();
            } catch (Throwable ignored) {}
            renderer = null;
        }
    }

    private void ensureRenderer(int w, int h) {
        if (renderer != null) {
            if (fboW != w || fboH != h) renderer.resize(w, h);
            fboW = w;
            fboH = h;
            return;
        }
        fboW = w;
        fboH = h;
        renderer = new ApexSceneRenderer(world, w, h);
        renderer.setClearColor(0x00000000);
        renderer.useCacheBuffer(placed.size() > 200);
        applyLayer();
    }

    private void applyLayer() {
        if (renderer == null) return;
        List<BlockPos> shown = new ArrayList<>();
        for (BlockPos p : placed.keySet()) if (layer == 0 || p.getY() == minY + layer - 1) shown.add(p);
        renderer.renderedBlocksMap.clear();
        renderer.addRenderedBlocks(shown, null);
        if (renderer.isUseCache()) renderer.needCompileCache();
    }

    public void setLayer(int l) {
        l = Math.max(0, Math.min(layers(), l));
        if (l == layer) return;
        layer = l;
        applyLayer();
    }

    /** draws the scene in the given local rect (the pose stack carries the screen transform) */
    public BlockPos render(net.minecraft.client.gui.GuiGraphics g, int x, int y, int w, int h, int mouseX, int mouseY) {
        if (dirty || world == null) build();
        if (placed.isEmpty()) return null;
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        ensureRenderer(Math.max(16, (int) (w * scale)), Math.max(16, (int) (h * scale)));
        Vector3f look = new Vector3f(center).add(pan);
        renderer.setCameraLookAt(look, baseRadius * zoom, Math.toRadians(yaw), Math.toRadians(pitch));
        boolean inside = mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h;
        renderer.draw(g.pose(), x, y, w, h, inside ? mouseX : -1000, inside ? mouseY : -1000);
        if (!inside) return null;
        BlockHitResult hit = renderer.getLastTraceResult();
        if (hit == null || hit.getType() == HitResult.Type.MISS) return null;
        BlockPos p = hit.getBlockPos();
        return placed.containsKey(p) ? p : null;
    }

    public boolean isCompiling() {
        return renderer != null && renderer.isCompiling();
    }

    public double compileProgress() {
        return renderer == null ? 1 : renderer.getCompileProgress();
    }

    /** the hovered block's name and what that spot accepts */
    public List<Component> tooltip(BlockPos p) {
        List<Component> out = new ArrayList<>();
        BlockInfo info = placed.get(p);
        if (info == null) return out;
        out.add(info.getBlockState().getBlock().getName());
        TraceabilityPredicate tp = predicates.get(p);
        if (tp != null) {
            List<SimplePredicate> all = new ArrayList<>(tp.limited);
            all.addAll(tp.common);
            LinkedHashSet<String> names = new LinkedHashSet<>();
            for (SimplePredicate sp : all) {
                try {
                    for (ItemStack s : sp.getCandidates()) if (!s.isEmpty()) names.add(s.getHoverName().getString());
                } catch (Throwable ignored) {}
            }
            if (names.size() > 1) {
                out.add(Component.literal("Accepts:").withStyle(ChatFormatting.GRAY));
                int n = 0;
                for (String s : names) {
                    if (n++ >= 8) {
                        out.add(Component.literal("  ... and " + (names.size() - 8) + " more").withStyle(ChatFormatting.DARK_GRAY));
                        break;
                    }
                    out.add(Component.literal("  " + s).withStyle(ChatFormatting.DARK_AQUA));
                }
            }
        }
        return out;
    }

    /** blocks needed for a shape, controller first, biggest counts next */
    public static List<ItemStack> parts(MultiblockShapeInfo shape, Block controller) {
        Map<net.minecraft.world.item.Item, Integer> counts = new LinkedHashMap<>();
        BlockInfo[][][] blocks = shape.getBlocks();
        for (BlockInfo[][] a : blocks)
            for (BlockInfo[] r : a)
                for (BlockInfo info : r) {
                    if (info == null) continue;
                    BlockState s = info.getBlockState();
                    if (s.isAir()) continue;
                    net.minecraft.world.item.Item item = s.getBlock().asItem();
                    if (item == Items.AIR && !s.getFluidState().isEmpty()) item = s.getFluidState().getType().getBucket();
                    if (item == Items.AIR) continue;
                    counts.merge(item, 1, Integer::sum);
                }
        List<ItemStack> out = new ArrayList<>();
        net.minecraft.world.item.Item ci = controller.asItem();
        counts.remove(ci);
        out.add(new ItemStack(ci));
        counts.entrySet().stream().sorted((a, b) -> {
            boolean ma = Block.byItem(a.getKey()) instanceof MetaMachineBlock, mb = Block.byItem(b.getKey()) instanceof MetaMachineBlock;
            if (ma != mb) return ma ? 1 : -1;
            return b.getValue() - a.getValue();
        }).forEach(e -> out.add(new ItemStack(e.getKey(), e.getValue())));
        return out;
    }

    static boolean isAir(Block b) {
        return b == Blocks.AIR || b == Blocks.CAVE_AIR || b == Blocks.VOID_AIR;
    }
}
