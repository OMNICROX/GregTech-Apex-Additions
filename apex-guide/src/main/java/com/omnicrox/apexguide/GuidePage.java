package com.omnicrox.apexguide;

import com.omnicrox.apexguide.document.block.LytDocument;
import net.minecraft.resources.ResourceLocation;

public record GuidePage(String sourcePack, ResourceLocation id, LytDocument document) {
}
