package com.omnicrox.gtaa.client.guide;

import com.omnicrox.apexguide.compiler.PageCompiler;
import com.omnicrox.apexguide.compiler.tags.FlowTagCompiler;
import com.omnicrox.apexguide.document.flow.LytFlowLink;
import com.omnicrox.apexguide.document.flow.LytFlowParent;
import com.omnicrox.apexguide.document.interaction.TextTooltip;
import com.omnicrox.apexguide.libs.mdast.mdx.model.MdxJsxElementFields;
import com.omnicrox.gtaa.GTAA;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

import java.util.Set;

/**
 * {@code <QuestLink id="0123ABCD...">Quest title</QuestLink>}: a link that opens that quest in the FTB Quests book.
 * The other half of the quest book's "Read more in the Apex Guide" lines. Without FTB Quests it is plain text.
 */
public class QuestLinkTag extends FlowTagCompiler {

    @Override
    public Set<String> getTagNames() {
        return Set.of("QuestLink");
    }

    @Override
    protected void compile(PageCompiler compiler, LytFlowParent parent, MdxJsxElementFields el) {
        String hex = el.getAttributeString("id", "");
        long id;
        try {
            id = Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            parent.appendError(compiler, "QuestLink needs a hex quest id", el);
            return;
        }
        if (!ModList.get().isLoaded("ftbquests")) {
            compiler.compileFlowContext(el.children(), parent);
            return;
        }
        var link = new LytFlowLink();
        link.setTooltip(new TextTooltip(Component.literal("Open this quest in the quest book")));
        link.setClickCallback(uiHost -> openQuest(id));
        compiler.compileFlowContext(el.children(), link);
        parent.append(link);
    }

    /** FTB Quests is optional, so it is reached by reflection: ClientQuestFile.openBookToQuestObject(long). */
    private static void openQuest(long id) {
        try {
            Class<?> file = Class.forName("dev.ftb.mods.ftbquests.client.ClientQuestFile");
            if (!(boolean) file.getMethod("exists").invoke(null)) return;
            Minecraft.getInstance().setScreen(null);
            file.getMethod("openBookToQuestObject", long.class).invoke(null, id);
        } catch (ReflectiveOperationException | RuntimeException e) {
            GTAA.LOGGER.warn("[Apex Guide] could not open quest {}", Long.toHexString(id), e);
        }
    }
}
