/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.platforms.bukkit.util;

import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;

/**
 *
 * @author Chingo
 */
public class BKWorldEditUtil {
    
    private BKWorldEditUtil(){}

    public static LocalPlayer wrapPlayer(Player player) {
        return getWorldEditPlugin().wrapPlayer(player);
    }

    public static WorldEditPlugin getWorldEditPlugin() {
        return AsyncWorldEditMain.getWorldEdit(AsyncWorldEditMain.getInstance());
    }

}
