/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.structure.construction.StructureBlock;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.iterator.CuboidIterator;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BlockMask;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BlockPredicate;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.PlacementOptions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author Chingo
 */
public abstract class AbstractBlockPlacement<T extends PlacementOptions> extends AbstractPlacement<T> implements BlockPlacement<T> {
    
    private static final int PRIORITY_FIRST = 4;
    private static final int PRIORITY_LIQUID = 3;
    private static final int PRIORITY_LATER = 2;
    private static final int PRIORITY_FINAL = 1;
    
    private final int BLOCK_BETWEEN;
    private final int MAX_PLACE_LATER_TO_PLACE = 10;

    public AbstractBlockPlacement(int width, int height, int length) {
        super(width, height, length);
        this.BLOCK_BETWEEN = Math.round((float) ((getBlocks() * 0.001)));
    }

    public AbstractBlockPlacement(Direction direction, Vector relativePosition, int width, int height, int length) {
        super(direction, relativePosition, width, height, length);
        this.BLOCK_BETWEEN = Math.round((float) ((getBlocks() * 0.001)));
    }

    @Override
    public final int getBlocks() {
        return getWidth() * getHeight() * getLength();
    }
    
    

    @Override
    public final void place(EditSession editSession, Vector pos, T option) {
        Iterator<Vector> traversal = new CuboidIterator(option.getCubeX(), option.getCubeY(), option.getCubeZ()).iterate(getSize());
        PriorityQueue<StructureBlock> placeLater = new PriorityQueue<>();
        
        int placeLaterPlaced = 0;
        int placeLaterPause = 0;
        
        // Cube traverse this clipboard
        while (traversal.hasNext()) {
            Vector v = traversal.next();
            BaseBlock clipboardBlock = getBlock(v);
            
            if (clipboardBlock == null) {
                continue;
            }

            int priority = getPriority(clipboardBlock);

            if (priority == PRIORITY_FIRST) {
                doBlock(editSession, pos, v, clipboardBlock, option);
            } else {
                placeLater.add(new StructureBlock(v, clipboardBlock));
            }

            // For every 10 place intensive blocks
            if (placeLaterPause > 0 && clipboardBlock.getId() != 0) {
                placeLaterPause--;
            } else {

                // only place these when having a greater xz-cubevalue to avoid placing torches etc in air and break them later
                while (placeLater.peek() != null
                        && placeLater.peek().getPosition().getBlockY() < v.getBlockY()) {
                    StructureBlock plb = placeLater.poll();
                    doBlock(editSession, pos, plb.getPosition(), plb.getBlock(), option);
                    
                   
                    
                    if(plb.getPriority() != PRIORITY_LIQUID) {
                        
                        placeLaterPlaced++;
                        if(BlockType.emitsLight(plb.getBlock().getId())) {
                            placeLaterPlaced++;
                        }
                        
                    }
                    
                    if (placeLaterPlaced >= MAX_PLACE_LATER_TO_PLACE) {
                        placeLaterPause = BLOCK_BETWEEN;
                        placeLaterPlaced = 0;
                    }
                }
            }

        }
        // Empty the queue
        while (placeLater.peek() != null) {
            StructureBlock plb = placeLater.poll();
            doBlock(editSession, pos, plb.getPosition(), plb.getBlock(), option);
        }
    }
    
    private int getPriority(BaseBlock block) {
        if (isWater(block) || isLava(block)) {
            return PRIORITY_LIQUID;
        }

        if (BlockType.shouldPlaceLast(block.getId()) || BlockType.emitsLight(block.getId())) {
            return PRIORITY_LATER;
        }
        
        

        if (BlockType.shouldPlaceFinal(block.getId())) {
            return PRIORITY_FINAL;
        }

        return PRIORITY_FIRST;

    }

    private boolean isLava(BaseBlock b) {
        int bi = b.getType();
        if (bi == BlockID.LAVA || bi == BlockID.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    private boolean isWater(BaseBlock b) {
        int bi = b.getType();
        if (bi == BlockID.WATER || bi == BlockID.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

    @Override
    public abstract BaseBlock getBlock(Vector position);
    
    protected void doBlock(EditSession editSession, Vector position, Vector blockPosition, BaseBlock block, T option) {
        Vector p;
        switch (getDirection()) {
            case EAST:
                p = position.add(blockPosition);
                break;
            case WEST:
                p = position.add((-blockPosition.getBlockX()) + (getWidth() - 1), blockPosition.getBlockY(), (-blockPosition.getBlockZ()) + (getLength() - 1));
                block.rotate90();
                block.rotate90();
                break;
            case NORTH:
                p = position.add(blockPosition.getBlockZ(), blockPosition.getBlockY(), (-blockPosition.getBlockX()) + (getWidth() - 1));
                block.rotate90Reverse();
                break;
            case SOUTH:
                p = position.add((-blockPosition.getBlockZ()) + (getLength() - 1), blockPosition.getBlockY(), blockPosition.getBlockX());
                block.rotate90();
                break;
            default:
                throw new AssertionError("unreachable");
        }
        
        for(BlockPredicate bp : option.getIgnore()) {
            if(bp.evaluate(blockPosition, p, block)) {
                return;
            }
        }
        
        for(BlockMask bm : option.getBlockMasks()) {
            bm.apply(blockPosition, p, block);
        }
        
        
//        System.out.println("set: " + p + " " + block.getId() + " " + block.getData());
        
        editSession.rawSetBlock(p, block);
        
    }
    
}
