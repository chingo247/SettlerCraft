
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structure.placement;

import com.chingo247.settlercraft.structure.construction.options.ConstructionOptions;
import com.chingo247.settlercraft.util.WorldUtil;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.construction.worldedit.StructureBlock;
import com.chingo247.settlercraft.persistence.entities.SchematicEntity;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.structure.plan.schematic.SchematicDataManager;
import com.chingo247.settlercraft.util.CubicIterator;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.chingo247.settlercraft.world.Direction;
import static com.chingo247.settlercraft.world.Direction.EAST;
import static com.chingo247.settlercraft.world.Direction.NORTH;
import static com.chingo247.settlercraft.world.Direction.SOUTH;
import static com.chingo247.settlercraft.world.Direction.WEST;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class SchematicPlacement extends DirectionalPlacement {
    
    private static final int PRIORITY_FIRST = 4;
    private static final int PRIORITY_LIQUID = 3;
    private static final int PRIORITY_LATER = 2;
    private static final int PRIORITY_FINAL = 1;
    private static final int BLOCK_BETWEEN = 100;
    private static final int MAX_PLACE_LATER_TO_PLACE = 10;

    private final Long checksum;
    private final Vector position;
    private final File schematicFile;
    private final SchematicEntity data;
    private Direction direction;
    private int rotation;

    public SchematicPlacement(File schematicFile, Direction direction, Vector position) throws IOException {
        this.checksum = FileUtils.checksumCRC32(schematicFile);
        this.position = position;
        this.direction = direction;
        this.schematicFile = schematicFile;
        this.data = SchematicDataManager.getInstance().getData(checksum);
    }

    public File getSchematicFile() {
        return schematicFile;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Vector getRelativePosition() {
        return position;
    }

    private SchematicEntity getSchematicData() {
        if (data == null) {

        }
        return data;
    }

    @Override
    public CuboidDimension getCuboidDimension() {
        Vector size = getSchematicData().getSize();
        Vector end = getPoint2Right(position, direction, new Vector(
                size.getBlockX(),
                size.getBlockY(),
                size.getBlockZ())
        );
        return new CuboidDimension(position, end);
    }

    private Vector getPoint2Right(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add(size.subtract(1, 1, 1));
            case SOUTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case NORTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

    /**
     * Flips the Schematic, note that this method will fake the flip operation
     * and only sets the direction of this Schematic internally as opposed to
     * other flip operations where huge amount of blocks are swapped. This
     * method is therefore a VERY LIGHT operation.
     *
     * @param direction
     */
    @Override
    public void rotate(Direction direction) {
        
        
        switch (direction) {
            case EAST:
                break;
            case SOUTH:
                rotation += 90;
                break;
            case WEST:
                rotation += 180;
                break;
            case NORTH:
                rotation += 270;
                break;
            default:
                throw new AssertionError("unreachable");
        }
        // If the amount is bigger than 360, then remove turn back 360
        if (rotation >= 360) {
            rotation = - 360;
        }
        this.direction = WorldUtil.getDirection(rotation);
    }

    @Override
    public void move(Vector offset) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void place(EditSession editSession, Vector pos, Options o) {
        System.out.println("SchematicPlacement#place()");
        ConstructionOptions options = new ConstructionOptions();
        
        System.out.println("PlacementDirection: " + direction);
        
        System.out.println("Placing clipboard on: (" + pos.getBlockX() + ", " + pos.getBlockY() + ", " + pos.getBlockZ() + ")");
        try {
            CuboidClipboard clipboard = SchematicFormat.MCEDIT.load(schematicFile);
            SchematicUtil.align(clipboard, direction);
            
            System.out.println("Size: " + clipboard.getSize());

            int x = options.getXCube() <= 0 ? clipboard.getSize().getBlockX() : options.getXCube();
            int y = options.getYCube() <= 0 ? clipboard.getSize().getBlockY() : options.getYCube();
            int z = options.getZCube() <= 0 ? clipboard.getSize().getBlockZ() : options.getZCube();

//            ReplaceBlockClipboardMask mask = new ReplaceBlockClipboardMask(this, 35, 14, 35, 11);

            CubicIterator traversal = new CubicIterator(clipboard.getSize(), x, y, z);
            PriorityQueue<StructureBlock> placeLater = new PriorityQueue<>();

            int placeLaterPlaced = 0;
            int placeLaterPause = 0;

            // Cube traverse this clipboard
            while (traversal.hasNext()) {
                Vector v = traversal.next();

//                if (mask.test(v)) {
//                    clipboard.setBlock(v, new BaseBlock(mask.getMaterial(), mask.getData()));
//                }

                BaseBlock b = clipboard.getBlock(v);
                if (b == null) {
                    continue;
                }

                int priority = getPriority(b);

                if (priority == PRIORITY_FIRST) {
                    doblock(editSession, b, v, pos);
                } else {
                    placeLater.add(new StructureBlock(v, b));
                }

                // For every 10 place intensive blocks, place 100 normal blocks
                if (placeLaterPause > 0) {
                    placeLaterPause--;
                } else {

                    // only place after a greater ZCube-value, this way torches and other attachables will not be placed against air and break
                    while (placeLater.peek() != null && (getCube(placeLater.peek().getPosition().getBlockZ(), options.getZCube()) < (getCube(v.getBlockZ(), options.getZCube())))) {
                        StructureBlock plb = placeLater.poll();
                        doblock(editSession, plb.getBlock(), plb.getPosition(), pos);
                        placeLaterPlaced++;
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

        } catch (IOException | DataException ex) {
            Logger.getLogger(SchematicPlacement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private int getCube(int index, int cube) {
        if (index % cube == 0) {
            return index / cube;
        } else {
            index -= (index % cube);
            return index / cube;
        }
    }
    
    private void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {
        Vector p = blockPos.add(pos);
//        System.out.println("Do block: " + p + ", " + b.getId() + " : " + b.getData());
        session.rawSetBlock(p, b);
    }

    private int getPriority(BaseBlock block) {
        if (isWater(block) || isLava(block)) {
            return PRIORITY_LIQUID;
        }

        if (BlockType.shouldPlaceLast(block.getId())) {
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

}
