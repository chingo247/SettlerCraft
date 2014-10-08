/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.util;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import java.util.List;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;

/**
 *
 * @author Chingo
 */
public class WorldEditUtil {

    private WorldEditUtil() {
    }

    public static World getWorld(String world) {
        List<? extends World> worlds = WorldEdit.getInstance().getServer().getWorlds();
        for (World w : worlds) {
            if (w.getName().equals(world)) {

                return w;
            }
        }
        return null;
    }

    public static LocalPlayer getLocalPlayer(Player player) {
        return getWorldEditPlugin().wrapPlayer(player);
    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return AsyncWorldEditMain.getWorldEdit(AsyncWorldEditMain.getInstance());
    }

}
