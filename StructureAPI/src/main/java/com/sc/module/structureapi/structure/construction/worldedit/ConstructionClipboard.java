/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.construction.worldedit;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author Chingo
 */
public abstract class ConstructionClipboard extends SmartClipboard {

    private final Comparator<StructureBlock> constructionMode;
    
    public ConstructionClipboard(CuboidClipboard parent, Comparator<StructureBlock> comparator) {
        super(parent);
        this.constructionMode = comparator;
    }
    
    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {
        Queue<StructureBlock> structurequeu = new PriorityQueue<>(constructionMode);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                for (int z = 0; z < getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);
                    BaseBlock b = getBlock(v);
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }

                    structurequeu.add(new StructureBlock(v, b));
                }
            }
        }

        while (structurequeu.peek() != null) {
            StructureBlock b = structurequeu.poll();
            doblock(editSession, b.getBlock(), b.getPosition(), pos);
        }
    }

    public abstract void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos);

}
