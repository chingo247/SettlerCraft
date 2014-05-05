/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builders;

import com.sc.api.structure.construction.SCStructureAPI;
import com.sc.api.structure.construction.strategies.FoundationStrategy;
import com.sc.api.structure.construction.strategies.FrameStrategy;
import com.sc.api.structure.event.structure.StructureLayerCompleteEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureState;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.Resource;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
import static com.settlercraft.core.model.world.Direction.EAST;
import static com.settlercraft.core.model.world.Direction.NORTH;
import static com.settlercraft.core.model.world.Direction.SOUTH;
import static com.settlercraft.core.model.world.Direction.WEST;
import com.settlercraft.core.persistence.StructureProgressService;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.SettlerCraftMaterials;
import com.settlercraft.core.util.Ticks;
import com.settlercraft.core.util.WorldUtil;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Bed;
import org.bukkit.material.Button;
import org.bukkit.material.Chest;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Diode;
import org.bukkit.material.Directional;
import org.bukkit.material.Dispenser;
import org.bukkit.material.EnderChest;
import org.bukkit.material.Furnace;
import org.bukkit.material.Ladder;
import org.bukkit.material.Lever;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.Pumpkin;
import org.bukkit.material.Sign;
import org.bukkit.material.Skull;
import org.bukkit.material.Stairs;
import org.bukkit.material.Torch;
import org.bukkit.material.TrapDoor;
import org.bukkit.material.TripwireHook;

/**
 *
 * @author Chingo
 */
public class StructureBuilder {

    private final StructureService structureService;
    private final StructureProgressService structureProgressService;
    private final Structure structure;
    private final int TIME_BETWEEN_LAYERS = Ticks.ONE_SECOND * 2;

    public enum BuildDirection {

        UP,
        DOWN
    }

    public StructureBuilder(Structure structure) {
        this.structure = structure;
        this.structureService = new StructureService();
        this.structureProgressService = new StructureProgressService();
    }

    /**
     * Clears all blocks at the location of the structure.
     */
    public void clear() {
        Direction direction = structure.getDirection();
        Location location = structure.getLocation();
        StructurePlan sp = structure.getPlan();
        SchematicObject schematic = sp.getStructureSchematic();

        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                        b = location.clone().add(x * xMod, y, z * zMod).getBlock();
                    } else {
                        b = location.clone().add(z * zMod, y, x * xMod).getBlock();
                    }
                    b.setType(Material.AIR);
                }
            }
        }
    }

    public FoundationBuilder foundation(FoundationStrategy strategy) {
        return new FoundationBuilder(structure, strategy);
    }

    public FoundationBuilder foundation() {
        return new FoundationBuilder(structure, FoundationStrategy.DEFAULT);
    }

    /**
     * Creates a frame builder for this structure.
     *
     * @return The frame builder
     */
    public FrameBuilder frame() {
        return new FrameBuilder(structure);
    }

    public FrameBuilder frame(FrameStrategy strategy) {
        return new FrameBuilder(structure, strategy);
    }

    /**
     * Instantly constructs a the structure
     */
    public void instant() {
        final SchematicObject schematic = structure.getPlan().getStructureSchematic();
        final Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        final Direction direction = structure.getDirection();
        final Location target = structure.getLocation();
        final int[] mods = WorldUtil.getModifiers(direction);
        final int xMod = mods[0];
        final int zMod = mods[1];
        final List<SpecialBlock> placeLater = new LinkedList<>();

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                        b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                    } else {
                        b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                    }
                    SchematicBlockData d = it.next();
                    if (SettlerCraftMaterials.isDirectional(d)) {
                        placeLater.add(new SpecialBlock(d.getMaterial(), d.getData(), b));
                        b.setType(Material.AIR);

                    } else {
                        b.setType(d.getMaterial());
                        b.setData(d.getData());
                    }
                }
            }
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(SCStructureAPI.getStructureAPI(), new Runnable() {
            @Override
            public void run() {
                for (SpecialBlock plb : placeLater) {
                    if (SettlerCraftMaterials.isDirectional(new Resource(plb.material, plb.data))) {
                        placeToDirection(plb);
                    } else { // Not only directionals in near future
                        plb.place();
                    }
                }
            }
        }, TIME_BETWEEN_LAYERS * 2);
    }

    /**
     * Finish this structure into given direction. If the direction is up, the structure will be
     * build from current layer to top. Otherwise from top to bottom
     *
     * @param bd Complete up or perform a complete down
     * @param keepFrame determines if there should be an attempt to keep the frame
     * @param force Will try to complete the structure even though it says that it's in a Complete State, 
     * however force will be ignored if the structure is in finishing state, which it would have fallen into if this method was called before
     */
    public void complete(BuildDirection bd, boolean keepFrame, boolean force) {
        if ((structure.getStatus() != StructureState.FINISHING && structure.getStatus() != StructureState.COMPLETE) || (force && structure.getStatus() == StructureState.COMPLETE)) {
            structure.setStatus(StructureState.FINISHING);
            final SchematicObject schematic = structure.getPlan().getStructureSchematic();
            final SchematicBlockData[][][] arr = schematic.getBlocksAsArray();
            complete(keepFrame,bd, structure.getProgress().getLayer(), arr, new LinkedList<SpecialBlock>());
        }
    }

    private void complete(final boolean keepFrame, final BuildDirection bd, int layer, final SchematicBlockData[][][] arr, final List<SpecialBlock> placeLater) {
        if ((layer == arr.length && bd == BuildDirection.UP) || (layer == -1 && bd == BuildDirection.DOWN)) {
            // Final Condition 
            placeSpecialBlocks(placeLater, new CallBack() {

                @Override
                public void onComplete() {
                    structureService.setStatus(structure, StructureState.COMPLETE);
                }
            });

        } else {
            final Direction direction = structure.getDirection();
            final Location target = structure.getLocation();
            final int[] mods = WorldUtil.getModifiers(direction);
            final int xMod = mods[0];
            final int zMod = mods[1];

            for (int z = arr[layer].length - 1; z >= 0; z--) {
                for (int x = 0; x < arr[layer][z].length; x++) {
                    Block b;

                    if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                        b = target.clone().add(x * xMod, layer, z * zMod).getBlock();
                    } else {
                        b = target.clone().add(z * zMod, layer, x * xMod).getBlock();
                    }
                    if (keepFrame && arr[layer][z][x].getMaterial() == Material.AIR && b.getType() != Material.AIR) {
                        continue; // Keep the frame!
                    }
                    SchematicBlockData d = arr[layer][z][x];
                    if (!SettlerCraftMaterials.isDirectional(d)) {
                        b.setType(d.getMaterial());
                        b.setData(d.getData());
                    } else {
                        placeLater.add(new SpecialBlock(d.getMaterial(), d.getData(), b));
                    }
                }
            }

            final int next = bd == BuildDirection.UP ? (layer + 1) : (layer - 1);
            Bukkit.getScheduler().runTaskLaterAsynchronously(SCStructureAPI.getStructureAPI(), new Runnable() {

                @Override
                public void run() {
                    complete(keepFrame, bd, next, arr, placeLater);
                }
            }, TIME_BETWEEN_LAYERS);

        }
    }

    private void placeSpecialBlocks(List<SpecialBlock> plbs, CallBack callback) {
        for (SpecialBlock plb : plbs) {
            if (SettlerCraftMaterials.isDirectional(new Resource(plb.material, plb.data))) {
                placeToDirection(plb);
            } else {
                plb.place();
            }
        }
        callback.onComplete();
    }

    /**
     * Constructs the given layer, it will ignore any directionals and will be placed afther the
     * layer is finish
     *
     * @param layer The layer to construct
     * @param keepFrame
     */
    public void layer(int layer, boolean keepFrame) {
        if (structure.getStatus() != StructureState.CONSTRUCTING_A_LAYER
                && structure.getStatus() != StructureState.COMPLETE
                && structure.getStatus() != StructureState.FINISHING) {

            structureService.setStatus(structure, StructureState.CONSTRUCTING_A_LAYER);

            final StructurePlan sp = structure.getPlan();
            if (layer > sp.getStructureSchematic().layers || layer < 0) {
                throw new IndexOutOfBoundsException("layer doesnt exist");
            }
            final Direction direction = structure.getDirection();
            final SchematicObject schematic = sp.getStructureSchematic();
            final Iterator<SchematicBlockData> it = schematic.getBlocksFromLayer(layer).iterator();
            final Location target = structure.getLocation();
            final int[] mods = WorldUtil.getModifiers(direction);
            final int xMod = mods[0];
            final int zMod = mods[1];

            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    SchematicBlockData d = it.next();

                    if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                        b = target.clone().add(x * xMod, layer, z * zMod).getBlock();
                    } else {
                        b = target.clone().add(z * zMod, layer, x * xMod).getBlock();
                    }

                    if (keepFrame && d.getMaterial() == Material.AIR && b.getType() != Material.AIR) {
                        continue; // Keep the frame!
                    }
                    if (!SettlerCraftMaterials.isDirectional(d)) {
                        b.setType(d.getMaterial());
                        b.setData(d.getData());
                    }
                }
            }

            if (layer == schematic.layers - 1) {
                complete(BuildDirection.DOWN, false, false);
            } else {
                structureProgressService.nextLayer(structure.getProgress(), true);
                Bukkit.getPluginManager().callEvent(new StructureLayerCompleteEvent(structure, layer));
                structureService.setStatus(structure, StructureState.READY_TO_BE_BUILD);
            }
        }
    }

    /**
     * Using the directional class to get the right direction see:
     * http://jd.bukkit.org/rb/doxygen/dc/d24/interfaceorg_1_1bukkit_1_1material_1_1Directional.html
     *
     * @param spb
     */
    private void placeToDirection(SpecialBlock spb) {
        Resource r = new Resource(spb.material, spb.data);
        if (SettlerCraftMaterials.isDirectional(r)) {
            if (SettlerCraftMaterials.isDirectionalAttachable(r)) {

                Directional directional = (Directional) spb.material.getNewData(spb.data);
                BlockFace face = getDirection(directional);
                Block b = spb.block;
                Byte data;

                switch (spb.material) {
                    case BED_BLOCK:
                        Bed bed = new Bed(face);
                        data = bed.getData();
                        break;
                    case DIODE:
                    case DIODE_BLOCK_OFF:
                    case DIODE_BLOCK_ON:
                        Diode diode = new Diode();
                        diode.setFacingDirection(face);
                        data = diode.getData();
                        break;
                    case TORCH:
                    case REDSTONE_TORCH_OFF:
                    case REDSTONE_TORCH_ON:
                        Torch torch = new Torch();
                        torch.setFacingDirection(face);
                        data = torch.getData();
                        break;
                    case PISTON_BASE:
                        PistonBaseMaterial piston = new PistonBaseMaterial(Material.PISTON_BASE);
                        piston.setFacingDirection(face);
                        data = piston.getData();
                        break;
                    case PISTON_STICKY_BASE:
                        PistonBaseMaterial piston2 = new PistonBaseMaterial(Material.PISTON_STICKY_BASE);
                        piston2.setFacingDirection(face);
                        data = piston2.getData();
                        break;
                    case PUMPKIN:
                        Pumpkin pumpkin = new Pumpkin(face);
                        data = pumpkin.getData();
                        break;
                    case SKULL:
                        Skull skull = new Skull(b.getType(), b.getData());
                        skull.setFacingDirection(face);
                        data = skull.getData();
                        break;
                    case SMOOTH_STAIRS:
                    case NETHER_BRICK_STAIRS:
                    case QUARTZ_STAIRS:
                    case JUNGLE_WOOD_STAIRS:
                    case SPRUCE_WOOD_STAIRS:
                    case BIRCH_WOOD_STAIRS:
                    case COBBLESTONE_STAIRS:
                    case BRICK_STAIRS:
                    case WOOD_STAIRS:
                        Stairs stairs = new Stairs(spb.material);
                        stairs.setInverted(((Stairs) directional).isInverted());
                        stairs.setFacingDirection(face);
                        data = stairs.getData();
                        break;
                    case COCOA:
                        CocoaPlant cocoaPlant = new CocoaPlant();
                        cocoaPlant.setFacingDirection(face);
                        data = cocoaPlant.getData();
                        break;
                    case SIGN_POST:
                        Sign signp = new Sign(Material.SIGN_POST);
                        signp.setFacingDirection(face);
                        data = signp.getData();
                        break;
                    case WALL_SIGN:
                        Sign wsign = new Sign(Material.WALL_SIGN);
                        wsign.setFacingDirection(face);
                        data = wsign.getData();
                        break;
                    case SIGN:
                        Sign sign = new Sign(Material.SIGN);
                        sign.setFacingDirection(face);
                        data = sign.getData();
                        break;
                    case CHEST:
                        Chest chest = new Chest(face);
                        data = chest.getData();
                        break;
                    case ENDER_CHEST:
                        EnderChest echest = new EnderChest(face);
                        data = echest.getData();
                        break;
                    case STONE_BUTTON:
                    case WOOD_BUTTON:
                        Button button = new Button(spb.material);
                        button.setFacingDirection(face);
                        data = button.getData();
                        break;
                    case LADDER:
                        Ladder ladder = new Ladder();
                        ladder.setFacingDirection(face);
                        data = ladder.getData();
                        break;
                    case LEVER:
                        Lever lever = new Lever();
                        lever.setFacingDirection(face);
                        lever.setPowered(spb.block.isBlockPowered());
                        data = lever.getData();
                        break;
                    case TRAP_DOOR:
                        TrapDoor trapDoor = new TrapDoor();
                        trapDoor.setFacingDirection(face);
                        data = trapDoor.getData();
                        break;
                    case TRIPWIRE_HOOK:
                        TripwireHook twh = new TripwireHook();
                        twh.setFacingDirection(face);
                        data = twh.getData();
                        break;
                    case DISPENSER:
                        Dispenser dispenser = new Dispenser(face);
                        data = dispenser.getData();
                        break;
                    case FURNACE:
                        Furnace furnace = new Furnace(face);
                        data = furnace.getData();
                        break;
                    default:
                        throw new UnsupportedOperationException(spb.material + " : " + spb.data + " not supported");
                }
                spb.data = data;
                spb.place();
            }
        }

    }

    /**
     * @param directional The directional (block)
     * @return the blockface
     */
    private BlockFace getDirection(Directional directional) {
        if (directional.getFacing() != BlockFace.DOWN && directional.getFacing() != BlockFace.UP) {
            Direction direction = structure.getDirection();
            switch (direction) {
                case NORTH:
                    return directional.getFacing();
                case EAST:
                    System.out.println(directional.getFacing());
                    switch (directional.getFacing()) {
                        case NORTH:
                            return BlockFace.EAST;
                        case EAST:
                            return BlockFace.SOUTH;
                        case SOUTH:
                            return BlockFace.WEST;
                        case WEST:
                            return BlockFace.NORTH;
                        default:
                            throw new AssertionError("Dont know direction for: " + direction);
                    }
                case WEST:
                    switch (directional.getFacing()) {
                        case NORTH:
                            return BlockFace.WEST;
                        case EAST:
                            return BlockFace.NORTH;
                        case SOUTH:
                            return BlockFace.EAST;
                        case WEST:
                            return BlockFace.SOUTH;
                        default:
                            throw new AssertionError("Dont know direction for: " + direction);
                    }
                case SOUTH:
                    switch (directional.getFacing()) {
                        case NORTH:
                            return BlockFace.SOUTH;
                        case EAST:
                            return BlockFace.WEST;
                        case SOUTH:
                            return BlockFace.NORTH;
                        case WEST:
                            return BlockFace.EAST;
                        default:
                            throw new AssertionError("Dont know direction for: " + direction);
                    }
                default:
                    throw new AssertionError("Unreachable");
            }
        } else {
            return directional.getFacing();
        }
    }

    /**
     * A block that should be placed later than any other blocks because placing them in might cause
     * the block to break (e.g. Torches, Signs, etc)
     */
    private class SpecialBlock {

        private Material material;
        private Byte data;
        private Block block;

        public SpecialBlock(Material material, Byte data, Block block) {
            this.material = material;
            this.data = data;
            this.block = block;
        }

        public void place() {
            block.setData(data);
            block.setType(material);
        }

    }

    private interface CallBack {

        void onComplete();
    }

}
