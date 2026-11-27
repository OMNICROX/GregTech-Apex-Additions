package com.omnicrox.apexguide.libs.mdast.gfm.model;

import com.omnicrox.apexguide.libs.mdast.model.MdAstAnyContent;
import com.omnicrox.apexguide.libs.mdast.model.MdAstParent;

public class GfmTableRow extends MdAstParent<GfmTableCell> implements MdAstAnyContent {
    public GfmTableRow() {
        super("tableRow");
    }

    @Override
    protected Class<GfmTableCell> childClass() {
        return GfmTableCell.class;
    }
}
