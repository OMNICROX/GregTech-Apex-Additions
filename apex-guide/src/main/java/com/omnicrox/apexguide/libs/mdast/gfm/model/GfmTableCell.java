package com.omnicrox.apexguide.libs.mdast.gfm.model;

import com.omnicrox.apexguide.libs.mdast.model.MdAstAnyContent;
import com.omnicrox.apexguide.libs.mdast.model.MdAstParent;
import com.omnicrox.apexguide.libs.mdast.model.MdAstPhrasingContent;

public class GfmTableCell extends MdAstParent<MdAstPhrasingContent> implements MdAstAnyContent {
    public GfmTableCell() {
        super("tableCell");
    }

    @Override
    protected Class<MdAstPhrasingContent> childClass() {
        return MdAstPhrasingContent.class;
    }
}
