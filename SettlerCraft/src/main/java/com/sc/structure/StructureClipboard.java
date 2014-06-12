/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structure;

import com.sc.StructureBlock;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author Chingo
 */
public class StructureClipboard extends CuboidClipboard {


    private boolean demolishing = false;

    public StructureClipboard(CuboidClipboard parrent) {
        super(parrent.getSize());
        this.setOffset(parrent.getOffset());
        this.setOrigin(parrent.getOrigin());
        
       
        for (int x = 0; x < getWidth(); x++) {
            for (int z = 0; z < getLength(); z++) {
                for (int y = 0; y < getHeight(); y++) {
                    BlockVector pt = new BlockVector(x, y, z);
                    BaseBlock block = parrent.getBlock(pt);
                    this.setBlock(pt, block);
                }
                
            }
        }
    }
    
    public void setDemolishing(boolean demolishing) {
        this.demolishing = demolishing;
    }
    
    


    /**
     * Place blocks that
     *
     * @param editSession
     * @param pos
     * @param noAir
     * @throws MaxChangedBlocksException
     */
    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {

        
        
        for (int y = 0; y < getHeight(); y++) {
            Queue<StructureBlock> blocks = new PriorityQueue<>();
            for (int x = 0; x < getWidth(); x++) {
                for (int z = 0; z < getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);
                    
                    BaseBlock b = getBlock(v);
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }
                    
                    BaseBlock worldBlock = editSession.getBlock(v.add(pos));
                    if(worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData()) {
                        continue;
                    }
                    
                   
                    
                    
                    if (isLava(b) || isWater(b) || BlockType.shouldPlaceLast(b.getId()) || BlockType.shouldPlaceFinal(b.getId())) {
                        blocks.add(new StructureBlock(v, b));
                    } else {
                        buildBlock(editSession, b, v, pos, noAir);
                    }
                }
            }
            while (blocks.peek() != null) {
                StructureBlock b = blocks.poll();
                buildBlock(editSession, b.getBlock(), b.getPosition(), pos, noAir);
            }

        }

    }

    private void buildBlock(EditSession session, BaseBlock b, Vector blockPos, Vector pos, boolean noAir) {
        BaseBlock worldBlock = session.getBlock(blockPos.add(pos));
//        if (worldBlock.getId() != BlockID.BEDROCK && (worldBlock.getId() != b.getId() && worldBlock.getData() != b.getData())) {
             session.rawSetBlock(blockPos.add(pos), b);
//        }
    }
    
    

    private boolean isLava(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.LAVA || bi == BlockID.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    private boolean isWater(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.WATER || bi == BlockID.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

    

}
