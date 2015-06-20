/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.towny.restriction;

import com.chingo247.settlercraft.structureapi.structure.restriction.StructureRestriction;
import com.chingo247.settlercraft.towny.plugin.SettlerCraftTowny;
import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
        List<WorldCoord> coords = getCoords(Bukkit.getWorld(world.getName()), affectedArea);
        TownyWorld townyWorld = towny.getTownyUniverse().getWorldMap().get(world.getName());
        
        
        Iterator<WorldCoord> coordIt = coords.iterator();
            while (coordIt.hasNext()) {
                Coord nextPos = coordIt.next();
                TownBlock tb = null;
                
                    System.out.println("Is restricted to plots!");
                    try {
                        tb = townyWorld.getTownBlock(nextPos);
                    } catch (NotRegisteredException ex) {
                        if(restrictToPlots) {
                            setMessage("Not on a plot...");
                            return false;
                        } else {
                            System.out.println("Is not restricted to plots!");
                        }
                    }
                   
                
                if(tb != null) {
                    System.out.println("Town block not null!");
                    Town t = null;
                    try {
                       t =  tb.getTown();
                    } catch (NotRegisteredException ex) {}
                    
                    
                    Resident resident = null;
                    try {
                         resident = tb.getResident();
                    } catch (NotRegisteredException ex) {}
                    System.out.println("Resident: " + (resident != null ? resident.getName() : ""));
                    
                    if(resident != null && resident.getName().equals(whoPlaces.getName())) {
                        setMessage("You don't own this plot");
                        return false;
                    }
                    
                    System.out.println("Mayor? " + (t != null ? t.getMayor().getName(): ""));
                    if(t != null && !t.getMayor().getName().equals(whoPlaces.getName())) {
                        setMessage("You are not the mayor of this town, you may only place within the plots");
                        return false;
                    }
                    
                }
            }


       
        return true;
    }
    
    private List<WorldCoord> getCoords(org.bukkit.World w, CuboidRegion region) {
        
        Vector minPoint = region.getMinimumPoint();
        Vector maxPoint = region.getMaximumPoint();
        
        System.out.println("Before between: " + minPoint + " and " + maxPoint);
        
        WorldCoord minCoord = WorldCoord.parseWorldCoord(new Location(w, minPoint.getBlockX(), 0, minPoint.getBlockZ()));
//        WorldCoord maxCoord = WorldCoord.parseWorldCoord(new Location(w, maxPoint.getBlockX(), 0, maxPoint.getBlockZ()));
        
        Vector2D min = SettlerCraftTowny.translate(minCoord);
//        Vector2D max = SettlerCraftTowny.translate(maxCoord);
        
        
        List<WorldCoord> coords = new ArrayList<>();
        for(int x = min.getBlockX(); x < maxPoint.getBlockX(); x += blockSize) {
            for(int z = min.getBlockZ(); z < maxPoint.getBlockZ(); z += blockSize) {
                WorldCoord wc = WorldCoord.parseWorldCoord(new Location(w, x, 0, z));
                Vector2D pos = SettlerCraftTowny.translate(wc);
                System.out.println("Added (" + wc.getX() + ", " + wc.getZ() + ") VECTOR(" + pos.getBlockX() +"," +  pos.getBlockZ() + ")");
                coords.add(wc);
            }
        }
        
        return coords;
    }
    
    

}
