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
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class TownyRestriction extends StructureRestriction {

    private final int blockSize;
    private boolean restrictToPlots = false;

    private final Towny towny;

    public TownyRestriction() {
        super("Towny", "towny.structure.restriction", null);

        this.towny = (Towny) Bukkit.getPluginManager().getPlugin("Towny");
        this.blockSize = TownySettings.getTownBlockSize();

    }

    @Override
    public boolean evaluate(Player whoPlaces, World world, CuboidRegion affectedArea) {
        List<Coord> coords = getCoords(affectedArea);
        TownyWorld townyWorld = towny.getTownyUniverse().getWorldMap().get(world.getName());
        
        
        Iterator<Coord> coordIt = coords.iterator();
            while (coordIt.hasNext()) {
                Coord nextPos = coordIt.next();
                TownBlock tb = null;

                if(restrictToPlots) {
                    try {
                        tb = townyWorld.getTownBlock(nextPos);
                    } catch (NotRegisteredException ex) {
                        setMessage("Not on a plot...");
                        return false;
                    }
                }
                
                if(tb != null) {
                    Town t = null;
                    try {
                       t =  tb.getTown();
                    } catch (NotRegisteredException ex) {}
                    
                    Resident resident = null;
                    try {
                         resident = tb.getResident();
                    } catch (NotRegisteredException ex) {}
                    
                    
                    if(resident != null && resident.getName().equals(whoPlaces.getName())) {
                        setMessage("You don't own this plot");
                        return false;
                    }
                    
                    if(t != null && !t.getMayor().getName().equals(whoPlaces.getName())) {
                        setMessage("You are not the mayor of this town, you may only place within the plots");
                        return false;
                    }
                    
                }
            }


       
        return true;
    }
    
    private List<Coord> getCoords(CuboidRegion region) {
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Coord minCoord = Coord.parseCoord(min.getBlockX(), min.getBlockZ());
        Coord maxCoord = Coord.parseCoord(max.getBlockX(), max.getBlockZ());
        
        List<Coord> coords = new ArrayList<>();
        for(int x = minCoord.getX(); x < maxCoord.getX(); x += blockSize) {
            for(int z = minCoord.getZ(); x < maxCoord.getZ(); x += blockSize) {
                coords.add(new Coord(x, z));
            }
        }
        return coords;
    }

}
