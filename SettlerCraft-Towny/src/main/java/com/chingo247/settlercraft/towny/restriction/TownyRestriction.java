/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.towny.restriction;

import com.chingo247.settlercraft.structureapi.structure.restriction.StructureRestriction;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class TownyRestriction extends StructureRestriction {
    
    private final int blockSize;
    private static final int TOWN_BLOCK_HEIGHT = 128;
    
    private final Towny towny;

    public TownyRestriction() {
        super("Towny", "towny.structure.restriction", null);
        
        this.towny = (Towny) Bukkit.getPluginManager().getPlugin("Towny");
        this.blockSize = TownySettings.getTownBlockSize();
        
    }

    @Override
    public boolean evaluate(Player whoPlaces, World world, CuboidRegion affectedArea) {
        if(exceedsTownBlock(affectedArea)) {
            setMessage("Structure exceeds towny's townblock size");
            return false;
        }
        
        Set<Vector2D> chunks = affectedArea.getChunks();
        if(chunks.size() > 1)  {
            setMessage("Structure overlaps multiple plots");
            return false;
        }
        
        Iterator<Vector2D> posIt = chunks.iterator();
        Vector2D townBlockPos;
        if(posIt.hasNext()) {
           townBlockPos = posIt.next();
        } else {
            setMessage("Unable to retrieve TownBlocks..."); // Possible? 
            return false;
        }
        
        TownyWorld townyWorld = towny.getTownyUniverse().getWorldMap().get(world.getName());
        TownBlock block;
        try {
            block = townyWorld.getTownBlock(townBlockPos.getBlockX(), townBlockPos.getBlockZ());
        } catch (NotRegisteredException ex) {
            setMessage("Not on TownBlock...");
            return false;
        }
        
        Resident resident;
        try {
            resident = block.getResident();
        } catch (NotRegisteredException ex) {
            setMessage("You don't own this town block");
            return false;
        }
        
        if(!resident.getName().equals(whoPlaces.getName())) {
            setMessage("You don't own this town block");
            return false;
        }
        
        return true;
    }
    
    private boolean exceedsTownBlock(CuboidRegion affected) {
        return affected.getLength() > blockSize || affected.getWidth() > blockSize || affected.getHeight() > TOWN_BLOCK_HEIGHT;
    }
    
    
   
    
}
