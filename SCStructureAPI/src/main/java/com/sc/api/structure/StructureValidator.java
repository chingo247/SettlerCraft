package com.sc.api.structure;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
import com.settlercraft.core.persistence.StructureProgressService;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.WorldUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Chingo
 */
public class StructureValidator {

    public static void validateStructures() {
        Map<String, Date> worlds = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            worlds.put(world.getName(), new Date(world.getWorldFolder().lastModified()));
        }
        StructureService ss = new StructureService();
        for (Entry<String, Date> e : worlds.entrySet()) {
            if(!ss.getAllCreatedBeforeLastSave(e.getKey(), e.getValue()).isEmpty()) {
                System.out.println("[SC]: Removing some structures that were created before last world-save in " + e.getKey());
                ss.removeAllCreatedBeforeSave(e.getKey(), e.getValue());
            }
           
            List<Structure> structures = ss.getAllModifiedAftherLastSave(e.getKey(), e.getValue());
            if (!structures.isEmpty()) {
                System.out.println("We got some structures to check!");
                for (Structure s : structures) {
                    if(!hasValidLayer(s)) {
                        System.out.println(s + "\nHas invalid layer, expected was " + s.getProgress().getLayer());
                        SCStructureAPI.getBuilder().buildToLayer(s, s.getProgress().getLayer(), true);
                        if(s.getProgress().getResources().isEmpty()) {
                            s.getProgress().setResources(s.getPlan().getRequirement().getMaterialRequirement()
                                    .getLayer(s.getProgress().getLayer()).getResources());
                            StructureProgressService sps = new StructureProgressService();
                            sps.merge(s.getProgress());
                        }
                    }
                }
            }
        }
    }

    private static boolean hasValidLayer(Structure s) {
        int savedLayer = s.getProgress().getLayer();
        Iterator<SchematicBlockData> it = s.getPlan().getSchematic().getBlocksFromLayer(savedLayer).iterator();
        SchematicObject schematic = s.getPlan().getSchematic();
        Direction direction = s.getDirection();
        Location target = s.getLocation();

        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Block b;
                SchematicBlockData d = it.next();
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    b = target.clone().add(x * xMod, savedLayer, z * zMod).getBlock();
                } else {
                    b = target.clone().add(z * zMod, savedLayer, x * xMod).getBlock();
                }
                if (b.getType() != Material.FENCE && b.getType() != d.getMaterial()) { // frame
                    return false;
                }
            }
        }
        return true;

    }
}
