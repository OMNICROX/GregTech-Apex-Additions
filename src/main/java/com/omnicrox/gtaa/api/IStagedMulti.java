package com.omnicrox.gtaa.api;

import com.gregtechceu.gtceu.api.pattern.BlockPattern;

/** A multiblock that grows in stages, each its own structure (the Structure Upgrader can build the next one). */
public interface IStagedMulti {

    int stageCount();

    /** 0 = not formed */
    int currentStage();

    BlockPattern stagePattern(int stage);

    String stageName(int stage);

    /** what this stage adds to the build */
    String stageAdds(int stage);
}
