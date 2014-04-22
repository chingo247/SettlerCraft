/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.Resource;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
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
public class Builder {

    private final StructureService structureService;
    private final Structure structure;
    private final int TIME_BETWEEN_LAYERS = Ticks.ONE_SECOND * 2;

    public enum FOUNDATION_STRATEGY {

        DEFAULT,
        PROVIDED,
    }

    Builder(Structure structure) {
        this.structure = structure;
        this.structureService = new StructureService();
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

    /**
     * Constructs a foundation with the given strategy.
     *
     * @param strategy The foundation strategy
     */
    public void foundation(FOUNDATION_STRATEGY strategy) {
        switch (strategy) {
            case DEFAULT:
                placeDefaultFoundation();
                break;
            case PROVIDED:
                placeProvidedFoundation();
                break;
            default:
                throw new UnsupportedOperationException("no strategy implemented for: " + strategy);
        }
    }

    /**
     * Places a foundation for the structure. If the structure doesnt have a
     * foundation schematic provided, the default strategy will be executed.
     */
    public void foundation() {
        if (structure.getPlan().getFoundationSchematic() != null) {
            foundation(FOUNDATION_STRATEGY.PROVIDED);
        } else {
            foundation(FOUNDATION_STRATEGY.DEFAULT);
        }
    }

    private void placeDefaultFoundation() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();

        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Location l;
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    l = target.clone().add(x * xMod, 0, z * zMod);
                } else {
                    l = target.clone().add(z * zMod, 0, x * xMod);
                }
                l.getBlock().setType(Material.COBBLESTONE);
            }
        }
    }

    private void placeProvidedFoundation() {
        SchematicObject schematic = structure.getPlan().getFoundationSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        Iterator<SchematicBlockData> it = structure.getPlan().getFoundationSchematic().getBlocksSorted().iterator();

        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                SchematicBlockData sbd = it.next();
                Location l;
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    l = target.clone().add(x * xMod, 0, z * zMod);
                } else {
                    l = target.clone().add(z * zMod, 0, x * xMod);
                }
                l.getBlock().setType(sbd.getMaterial());
                l.getBlock().setData(sbd.getData());
            }
        }
    }

    /**
     * Creates a frame builder for this structure.
     *
     * @return The frame builder
     */
    public FrameBuilder frame() {
        return new FrameBuilder(structure);
    }

    /**
     * Instantly constructs a the structure
     */
    public void instant() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

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
                    b.setType(d.getMaterial());
                    b.setData(d.getData());
                }
            }
        }
    }

    public void complete() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        complete(schematic.layers - 1);
    }

    private void complete(int y) {

        SCStructureAPI.build(structure).layer(y, false);
        if (y > 0) {
            final int next = y - 1;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin(SCStructureAPI.MAIN_PLUGIN_NAME), new Runnable() {

                @Override
                public void run() {
                    complete(next);
                }
            }, TIME_BETWEEN_LAYERS);
        }

    }

    /**
     * Builds the corresponding layer of this structure, whether the
     * precoditions are met or not.
     *
     * @param layer The layer to build
     * @param hasFrame Determines if this should keep the fence at the borders
     */
    public void layer(int layer, boolean hasFrame) {
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

        final List<PlaceLaterBlock> placeLater = new LinkedList<>();

        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Block b;
                SchematicBlockData d = it.next();
                if (hasFrame && d.getMaterial() == Material.AIR
                        && (z == 0 || z == schematic.length - 1 || x == 0 || x == schematic.width - 1)) {
                    continue;
                }
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    b = target.clone().add(x * xMod, layer, z * zMod).getBlock();
                } else {
                    b = target.clone().add(z * zMod, layer, x * xMod).getBlock();
                }
                if (SettlerCraftMaterials.isDirectional(d)) {
                    placeLater.add(new PlaceLaterBlock(d.getMaterial(), d.getData(), b));
                    b.setType(Material.AIR);

                } else {
                    b.setType(d.getMaterial());
                    b.setData(d.getData());
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin(SCStructureAPI.MAIN_PLUGIN_NAME), new Runnable() {
            @Override
            public void run() {
                for (PlaceLaterBlock plb : placeLater) {
                    if (SettlerCraftMaterials.isDirectional(new Resource(plb.material, plb.data))) {
                        placeToDirection(plb, direction);
                    } else {
                        plb.place();
                    }
                }
            }
        }, TIME_BETWEEN_LAYERS * 2);

    }

    /**
     * Using the directional class to get the right direction
     * see: http://jd.bukkit.org/rb/doxygen/dc/d24/interfaceorg_1_1bukkit_1_1material_1_1Directional.html
     * @param plb
     * @param newDirection 
     */
    private void placeToDirection(PlaceLaterBlock plb, Direction newDirection) {
        Resource r = new Resource(plb.material, plb.data);
        if (SettlerCraftMaterials.isDirectional(r)) {
              if(SettlerCraftMaterials.isDirectionalAttachable(r)) {
                  
                  Directional d = (Directional) plb.material.getNewData(plb.data);
                  BlockFace face = getDirection(d,newDirection);
                  Block b = plb.block;
                  Byte data;
                  
                  switch(plb.material) {
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
                      Stairs stairs = new Stairs(plb.material);
                      stairs.setInverted(((Stairs) d).isInverted());
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
                          Button button = new Button(plb.material);
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
                          lever.setPowered(plb.block.isBlockPowered());
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
                      default:throw new UnsupportedOperationException(plb.material + " : " + plb.data + " not supported");
                  }
                  plb.data = data;
                  plb.place();
                  
//                  if(d instanceof Chest) {
//                      Chest chest = new Chest(face);
//                      data = chest.getData();
//                  } else if(d instanceof Torch) {
//                      Torch torch = new Torch();
//                      torch.setFacingDirection(face);
//                      data = torch.getData();
//                  }
              }
        }
        
    }
    
    
    /**
     * TODO ALL BLOCKFACES SHOULD BE SUPPORTED, MOVE THIS TO WORLDUTIL
     * @param directional
     * @param newDirection
     * @return 
     */
    private BlockFace getDirection(Directional directional, Direction newDirection) {
                if (directional.getFacing() != BlockFace.DOWN && directional.getFacing() != BlockFace.UP) {
                    switch (newDirection) {
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
                                    throw new AssertionError("Dont know direction for: " + newDirection);
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
                                    throw new AssertionError("Dont know direction for: " + newDirection);
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
                                    throw new AssertionError("Dont know direction for: " + newDirection);
                            }
                        default: throw new AssertionError("Unreachable");
                    }
    } else {
                    return directional.getFacing();
                }
    }
    
        /**
         * Builds the layers 0 to given layer of this structure.
         *
         * @param layer The last layer to construct
         * @param hasFrame Wheter or not to take in account that there is can be
         * a frame
         */
    public void layers(int layer, boolean hasFrame) {
        for (int i = 0; i < layer + 1; i++) {
            layer(layer, hasFrame);
        }

    }

    private class PlaceLaterBlock {

        private Material material;
        private Byte data;
        private Block block;

        public PlaceLaterBlock(Material material, Byte data, Block block) {
            this.material = material;
            this.data = data;
            this.block = block;
        }

        public void place() {
            block.setData(data);
            block.setType(material);
        }

    }


}
