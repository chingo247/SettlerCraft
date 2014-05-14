/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.util;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.regions.CuboidRegionSelector;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This class contains worldedit methods that will be frequently used, feel free
 * to copy, modify, use it for your own purposes
 *
 * @author Chingo
 */
public class WorldEditUtil {

    public static CuboidClipboard load(File SchematicFile) throws FileNotFoundException, DataException, IOException {
        SchematicFormat format = SchematicFormat.getFormat(SchematicFile);
        return format.load(SchematicFile);
    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }
    
    public static EditSession createEditSession(Player player, int maxblocks) {
        return getWorldEditPlugin().createEditSession(player);
    }
    
    public static EditSession getEditSession(LocalWorld world, int maxblocks) {
        return WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, maxblocks);
    }

    public static LocalSession getLocalSession(Player player) {
        return getWorldEditPlugin().getSession(player);
    }

    public static LocalPlayer getLocalPlayer(Player player) {
        return getWorldEditPlugin().wrapPlayer(player);
    }

    public static LocalWorld getLocalWorld(Player player) {
        return getLocalPlayer(player).getWorld();
    }
    
    public static LocalWorld getLocalWorld(String world) {
        List<LocalWorld> worlds = getWorldEditPlugin().getWorldEdit().getServer().getWorlds();
        for(LocalWorld lw : worlds) {
            if(lw.getName().equals(world)) {
                return lw;
            }
        }
        return null;
    }

    public static void selectClipboardArea(Player player, Location pos1, Location pos2) {
            LocalPlayer localPlayer = getLocalPlayer(player);
            LocalWorld world = localPlayer.getWorld();
            LocalSession session = getLocalSession(player);
            
//            Vector pos = getBlockWorldVector(location);
//            Vector pos2 = pos.add(clipboard.getSize().subtract(1,1,1));
            
            session.setRegionSelector(world, new CuboidRegionSelector(world, pos1.getPosition(), pos2.getPosition()));
            session.getRegionSelector(world).learnChanges();
            session.getRegionSelector(world).explainRegionAdjust(localPlayer, session);
            
            
    }

}
