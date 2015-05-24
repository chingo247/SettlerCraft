/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.services.holograms;

import com.chingo247.settlercraft.structureapi.platforms.services.Service;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public interface HologramsProvider extends Service {
    
    public Hologram createHologram(String plugin, World world, Vector position);
    
}
