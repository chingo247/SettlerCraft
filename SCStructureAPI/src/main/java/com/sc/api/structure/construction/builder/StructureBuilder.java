/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.SCStructureAPI;
import com.sc.api.structure.construction.strategies.FoundationStrategy;
import com.sc.api.structure.construction.strategies.FrameStrategy;
import com.sc.api.structure.event.structure.StructureLayerCompleteEvent;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureState;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.requirement.material.MaterialResource;
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
import com.settlercraft.core.util.Maths;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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

    private static final int DEFAULT_TIME_BETWEEN_LAYERS = Ticks.ONE_SECOND * 3;

    public enum BuildDirection {

        UP,
        DOWN
    }

    /**
     * Clears all blocks at the location of the structure.
     *
     * @param structure
     */
    public static void clear(Structure structure) {
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

    public static void foundation(Structure structure) {
        foundation(structure, FoundationStrategy.DEFAULT);
    }

    public static void foundation(Structure structure, FoundationStrategy strategy) {
        FoundationBuilder.construct(structure, strategy);
    }

    /**
     * Build a frame for given structure, will overlap anything at structure's
     * location. Uses the {@link FrameStrategy.FANCY} strategy to build the frame by default
     * @param structure The structure
     */
    public static void frame(Structure structure) {
        frame(structure, FrameStrategy.FANCY);
    }

    /**
     * Build a frame for given structure, will overlap anything at structure's
     * location. Uses given strategy of {@link FrameStrategy}  to build the frame.
     * @param structure The structure
     * @param strategy The strategy to use
     */
    public static void frame(Structure structure, FrameStrategy strategy) {
        frame(structure, strategy, 2, 2);
    }

    /**
     * Build a frame for given structure, will overlap anything at structure's
     * location. Uses given strategy of {@link FrameStrategy}  to build the frame.
     * @param structure The structure
     * @param strategy The strategy to use
     * @param hGap The gap between blocks horizontally
     * @param vGap The gap between blocks vertically
     */
    public static void frame(Structure structure, FrameStrategy strategy, int hGap, int vGap) {
        FrameBuilder.construct(structure, strategy, hGap, vGap);
    }
    
    /**
     * Constructs a frame animated
     * @param structure The target structure
     */
    public static void animatedFrame(Structure structure) {
        animatedFrame(structure, FrameStrategy.FANCY, 2, 2, Material.WOOD, Ticks.ONE_SECOND * 3);
    }
    
    /**
     * Constructs a frame animated with given strategy for target structure
     * @param Structure The structure
     * @param strategy The strategy to use to construct the frame
     */
    public static void animatedFrame(Structure Structure, FrameStrategy strategy) {
        animatedFrame(Structure, strategy, 2, 2, Material.WOOD, Ticks.ONE_SECOND);
    }
    
    /**
     * Constructs a frame animated with given strategy, material and interval
     * @param structure The structure
     * @param strategy The strategy to use to construct the frame
     * @param material The material to use to constuct the frame
     * @param interval The interval at which layers of a structure are constructed, default is every 3 seconds
     * TIP: U may multiply {@link Ticks.ONE_SECOND} to get a the amount of ticks in seconds
     */
    public static void animatedFrame(Structure structure, FrameStrategy strategy, Material material, int interval) {
        animatedFrame(structure, strategy, 2, 2, material, interval);
    }
    
    /**
     * Constructs a frame animated with given strategy, material and interval
     * @param structure The structure
     * @param strategy The strategy to use to construct the frame
     * @param hGap The gap between blocks horizontally
     * @param vGap The gap between blocks vertically
     * @param material The material to use to constuct the frame
     * @param interval The interval at which layers of a structure are constructed, default is every 3 seconds
     * TIP: U may multiply {@link Ticks.ONE_SECOND} to get a the amount of ticks in seconds
     */
    public static void animatedFrame(Structure structure, FrameStrategy strategy, int hGap, int vGap, Material material, int interval) {
        AnimatedFrameBuilder.construct(structure, strategy, hGap, vGap, material, interval);
    }

    /**
     * Instantly completely constructs a the structure, crushes anything in its path.
     * @param structure The structure to construct instantly
     */
    public static void instant(final Structure structure) {
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
                        placeToDirection(structure, plb);
                    } else { // Not only directionals in near future
                        plb.place();
                    }
                }
            }
        }, DEFAULT_TIME_BETWEEN_LAYERS * 2); // A delay for attachables and other special blocks
    }

    /**
     * Finish this structure into given direction. If the direction is up, the
     * structure will be build from current layer to top. Otherwise from top to
     * bottom
     *
     * @param structure The structure
     * @param bd Complete up or perform a complete down
     * @param keepFrame determines if there should be an attempt to keep the
     * frame
     * @param force Will try to complete the structure even though it says that
     * it's in a Complete State, however force will be ignored if the structure
     * is in finishing state, which it would have fallen into if this method was
     * called before
     */
    public static void complete(Structure structure, BuildDirection bd, boolean keepFrame, boolean force) {
        if ((structure.getStatus() != StructureState.FINISHING && structure.getStatus() != StructureState.COMPLETE) || (force && structure.getStatus() == StructureState.COMPLETE)) {
            structure.setStatus(StructureState.FINISHING);
            final SchematicObject schematic = structure.getPlan().getStructureSchematic();
            final SchematicBlockData[][][] arr = schematic.getBlocksAsArray();
            complete(structure, keepFrame, bd, structure.getProgress().getLayer(), arr, new LinkedList<SpecialBlock>());
        }
    }

    private static void complete(final Structure structure, final boolean keepFrame, final BuildDirection bd, int layer, final SchematicBlockData[][][] arr, final List<SpecialBlock> placeLater) {
        final StructureService structureService = new StructureService();
        if ((layer == arr.length && bd == BuildDirection.UP) || (layer == -1 && bd == BuildDirection.DOWN)) {
            // Final Condition 
            placeSpecialBlocks(structure, placeLater, new CallBack() {

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
                    complete(structure, keepFrame, bd, next, arr, placeLater);
                }
            }, DEFAULT_TIME_BETWEEN_LAYERS);

        }
    }

    private static void placeSpecialBlocks(Structure structure, List<SpecialBlock> plbs, CallBack callback) {
        for (SpecialBlock plb : plbs) {
            if (SettlerCraftMaterials.isDirectional(new Resource(plb.material, plb.data))) {
                placeToDirection(structure, plb);
            } else {
                plb.place();
            }
        }
        callback.onComplete();
    }

    /**
     * Constructs the given layer, it will ignore any directionals and will be
     * placed afther the layer is finish
     *
     * @param structure The structure
     * @param layer The layer to construct
     * @param keepFrame if true it will avoid location where a frame block could have been placed
     */
    public static void layer(Structure structure, int layer, boolean keepFrame) {
        if (structure.getStatus() != StructureState.CONSTRUCTING_A_LAYER
                && structure.getStatus() != StructureState.COMPLETE
                && structure.getStatus() != StructureState.FINISHING) {
            StructureService structureService = new StructureService();
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
                complete(structure, BuildDirection.DOWN, false, false);
            } else {
                StructureProgressService structureProgressService = new StructureProgressService();
                structureProgressService.nextLayer(structure.getProgress(), true);
                Bukkit.getPluginManager().callEvent(new StructureLayerCompleteEvent(structure, layer));
                structureService.setStatus(structure, StructureState.READY_TO_BE_BUILD);
            }
        }
    }

    public static void build(Structure structure ,Inventory inventory, int baseValue, BuildCallback callback) {
        if (structure.getStatus() == StructureState.READY_TO_BE_BUILD) {
            List<MaterialResource> resources = structure.getProgress().getResources();
            Iterator<MaterialResource> lit = resources.iterator();
            StructureProgressService structureProgressService = new StructureProgressService();

            while (lit.hasNext()) {
                MaterialResource materialResource = lit.next();

                for (ItemStack stack : inventory) {
                    if (materialResource != null && stack != null && materialResource.getMaterial() == stack.getType()) {
                        int removed = structureProgressService.resourceTransaction(materialResource, Maths.lowest(stack.getAmount(), baseValue, stack.getMaxStackSize()));

                        if (removed > 0) {
                            // Remove items from player inventory
                            ItemStack removedIS = new ItemStack(stack);
                            removedIS.setAmount(removed);
                            inventory.removeItem(removedIS);

                            // Layer Complete?
                            if (structure.getProgress().getResources().isEmpty()) {
                                int completedLayer = structure.getProgress().getLayer();
                                structureProgressService.nextLayer(structure.getProgress(), false);
                                complete(structure, BuildDirection.DOWN, false, true);
                                Bukkit.getPluginManager().callEvent(new StructureLayerCompleteEvent(structure, completedLayer));
                            }
                            callback.onSucces(structure, removedIS);
                        }
                    }
                }
            }
            callback.onResourcesNotRequired(structure);
        } else {
            callback.onNotInBuildState(structure);
        }
    }

    /**
     * TODO FIX THIS SHITCODE
     * Using the directional class to get the right direction see:
     * http://jd.bukkit.org/rb/doxygen/dc/d24/interfaceorg_1_1bukkit_1_1material_1_1Directional.html
     *
     * @param spb
     */
    private static void placeToDirection(Structure structure, SpecialBlock spb) {
        Resource r = new Resource(spb.material, spb.data);
        if (SettlerCraftMaterials.isDirectional(r)) {
            if (SettlerCraftMaterials.isDirectionalAttachable(r)) {

                Directional directional = (Directional) spb.material.getNewData(spb.data);
                BlockFace face = getDirection(structure, directional);
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
    private static BlockFace getDirection(Structure structure, Directional directional) {
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

    
    private interface CallBack {

        void onComplete();
    }

}
