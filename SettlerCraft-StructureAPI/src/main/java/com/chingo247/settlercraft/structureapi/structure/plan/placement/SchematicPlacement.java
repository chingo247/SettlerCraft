
/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement;

import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.chingo247.settlercraft.core.Direction;
import static com.chingo247.settlercraft.core.Direction.EAST;
import static com.chingo247.settlercraft.core.Direction.NORTH;
import static com.chingo247.settlercraft.core.Direction.SOUTH;
import static com.chingo247.settlercraft.core.Direction.WEST;
import com.chingo247.settlercraft.structureapi.structure.construction.StructureBlock;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.Schematic;
import com.chingo247.settlercraft.core.util.CuboidIterator;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.FastClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.util.PriorityQueue;

/**
 *
 * @author Chingo
 */
public class SchematicPlacement extends AbstractCuboidPlacement<PlaceOptions> implements FilePlacement<PlaceOptions>, BlockPlacement<PlaceOptions> {

    private static final int PRIORITY_FIRST = 4;
    private static final int PRIORITY_LIQUID = 3;
    private static final int PRIORITY_LATER = 2;
    private static final int PRIORITY_FINAL = 1;
    
    private final int BLOCK_BETWEEN;
    private final int MAX_PLACE_LATER_TO_PLACE = 10;

    private final Vector position;
    private final Schematic schematic;
    
    private FastClipboard clipboard;

    public SchematicPlacement(Schematic schematic) {
        this(schematic, Direction.EAST, Vector.ZERO);
    }

    public SchematicPlacement(Schematic schematic, Direction direction, Vector position) {
        super(direction, position);
        this.position = position;
        this.schematic = schematic;
        this.clipboard = schematic.getClipboard();
        this.BLOCK_BETWEEN = Math.round((float) ((schematic.getHeight() * schematic.getLength() * schematic.getWidth()) * 0.001));
    }

    public Schematic getSchematic() {
        return schematic;
    }


    @Override
    public void place(EditSession editSession, Vector pos, PlaceOptions o) {
        
        
        
        System.out.println("Blocks between: " + BLOCK_BETWEEN);
        System.out.println("Pos");
        
        System.out.println("Direction on place: " + getDirection());
        
        Vector size = clipboard.getSize();
        int xCube = o.getxAxisCube() <= 0 ? 16 : o.getxAxisCube();
        int yCube = o.getyAxisCube() <= 0 ? size.getBlockY() : o.getyAxisCube();
        int zCube = o.getzAxisCube() <= 0 ? 16 : o.getzAxisCube();
        
        
        if(xCube > getWidth()) {
            xCube = getWidth();
        }
        
        if(yCube > getHeight()) {
            yCube = getHeight();
        }
        
        if(zCube > getLength()) {
            zCube = getLength();
        }

        CuboidIterator traversal = new CuboidIterator(size, xCube, yCube, zCube);
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
                doblock(editSession, clipboardBlock, v, pos);
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
                    doblock(editSession, plb.getBlock(), plb.getPosition(), pos);
                    
                   
                    
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
            doblock(editSession, plb.getBlock(), plb.getPosition(), pos);
        }

    }

    private void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {

        Vector p;
        
        
        switch (getDirection()) {
            case EAST:
                p = pos.add(blockPos);
                break;
            case WEST:
                p = pos.add((-blockPos.getBlockX()) + (getWidth() - 1), blockPos.getBlockY(), (-blockPos.getBlockZ()) + (getLength() - 1));
                b.rotate90();
                b.rotate90();
                break;
            case NORTH:
                p = pos.add(blockPos.getBlockZ(), blockPos.getBlockY(), (-blockPos.getBlockX()) + (getWidth() - 1));
                b.rotate90Reverse();
                break;
            case SOUTH:
                p = pos.add((-blockPos.getBlockZ()) + (getLength() - 1), blockPos.getBlockY(), blockPos.getBlockX());
                b.rotate90();
                break;
            default:
                throw new AssertionError("unreachable");
        }

        session.rawSetBlock(p, b);
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
    public CuboidRegion getCuboidRegion() {
        return new CuboidRegion(Vector.ZERO, new Vector(schematic.getWidth(), schematic.getHeight(), schematic.getLength()));
    }

    @Override
    public String getTypeName() {
        return PlacementTypes.SCHEMATIC;
    }

    @Override
    public File[] getFiles() {
        return new File[]{schematic.getFile()};
    }

    @Override
    public BaseBlock getBlock(Vector position) {
        if(new CuboidRegion(Vector.ZERO, getSchematic().getSize()).contains(position)) {
            BaseBlock b = clipboard.getBlock(position);
            return b;
        }
        return null;
    }

    @Override
    public int getWidth() {
        return schematic.getWidth();
    }

    @Override
    public int getHeight() {
        return schematic.getHeight();
    }

    @Override
    public int getLength() {
        return schematic.getLength();
    }

    
    

}
