/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.worldedit;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Comparator;

/**
 *
 * @author Chingo
 */
public class ConstructionBuildingClipboard extends ConstructionClipboard {

    private final Comparator<StructureBlock> constructionMode;
    
    public ConstructionBuildingClipboard(CuboidClipboard parent, Comparator<StructureBlock> comparator) {
        super(parent, comparator);
        this.constructionMode = comparator;
    }


    

    @Override
    public void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {
        BaseBlock worldBlock = session.getBlock(blockPos.add(pos));
        if (worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData()) {
            return;
        }
        session.rawSetBlock(blockPos.add(pos), b);
    }

}
