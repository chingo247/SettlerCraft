/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.bukkit.prism;

import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.world.Dimension;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.appliers.PrismProcessType;
import me.botsko.prism.actions.Handler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.Vector;

/**
 *
 * @author Chingo
 */
public class PrismIntegration {
    
    private final Prism prism;

    public PrismIntegration() {
        this.prism = (Prism) Bukkit.getPluginManager().getPlugin("Prism");
    }
    
    
    
    /**
     * 
     * @param structure
     * @param targetDate
     * @return 
     */
    public QueryResult getBlocks(Structure structure, Date targetDate) {
        Dimension dim = structure.getDimension();
        String world = structure.getWorldName();
        
        QueryParameters params = new QueryParameters();
        
        params.setSinceTime(structure.getLog().getCreatedAt().getTime());
        params.setBeforeTime(targetDate.getTime());
        params.setWorld(world);
        params.setMinLocation(new Vector(dim.getMinX(), dim.getMinY(), dim.getMinZ()));
        params.setMaxLocation(new Vector(dim.getMaxX(), dim.getMaxY(), dim.getMaxZ()));
        params.setProcessType(PrismProcessType.LOOKUP);
        ActionsQuery aq = new ActionsQuery(prism);
        
        QueryResult lookupResult = aq.lookup(params);
        HashMap<String, Handler> map = new HashMap<>();
        for(Handler h : lookupResult.getActionResults()) {
            String key = h.getX() + "" + h.getY() + "" + h.getZ();
            Handler current = map.get(key);
            
            if(current == null ||(current.getId() < h.getId())) {
                map.put(key, h);
            }
        }
        return lookupResult;
    }
    
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        HashMap<String, Object> map = new HashMap<>();
        
        int count = 0;
        for(int i = 1; i < 100_000; i++) {
            map.put(UUID.randomUUID().toString(), new Object());
            count++;
            if((i % (1_00_000 / 4) == 0)) {
                System.out.println( (i/10_000_000) * 100);
            }
        }
        System.out.println("Count: " + count + " in " + (System.currentTimeMillis() - start));
    }
    
}
