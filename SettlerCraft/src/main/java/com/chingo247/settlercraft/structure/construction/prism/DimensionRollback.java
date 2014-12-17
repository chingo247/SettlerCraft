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
package com.chingo247.settlercraft.structure.construction.prism;

import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.world.Dimension;
import com.chingo247.settlercraft.util.functions.DimensionIterator;
import com.chingo247.settlercraft.util.functions.Maths;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.database.mysql.SelectQueryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.util.BlockVector;

/**
 *
 * @author Chingo
 */
public class DimensionRollback {
    
    
    private static final int MAX_PREPARED_CLIPBOARDS = 3;
    private final Queue<CuboidClipboard> clusters = new ArrayBlockingQueue<>(MAX_PREPARED_CLIPBOARDS);
    private boolean canceled = false;
    private DimensionIterator dimIterator;
    private Prism prism = (Prism)Bukkit.getPluginManager().getPlugin("Prism");
    
    public DimensionRollback(Dimension dimension, Date targetDate, int maxBlocksPerCluster) {
        int size = Maths.sqrt(maxBlocksPerCluster, 3);
        System.out.println(size * size * size);
        this.dimIterator = new DimensionIterator(dimension, size, size, size);
    }
    
    
    
    public void rollback() {
        if(canceled) return;
        
        if(clusters.isEmpty() && dimIterator.hasNext()) {
            
            
        }
        
        
    }
    
    public QueryResult lookup(String world, Dimension d, Date date) {
        
        Vector min = d.getMinPosition();
        Vector max = d.getMaxPosition();
        
        String query = "SELECT id, epoch, action_id, player, world_id, AVG(x), AVG(y), AVG(z), block_id, block_subid, old_block_id, old_block_subid, data, COUNT(*) counted "
                     + "FROM prism_data INNER JOIN prism_players p ON p.player_id = prism_data.player_id LEFT JOIN prism_data_extra ex ON ex.data_id = prism_data.id "
                     + "WHERE world_id = ( SELECT w.world_id FROM prism_worlds w WHERE w.world = 'myWorld') AND (prism_data.x BETWEEN 0 AND 1) AND (prism_data.y BETWEEN 0 AND 1) AND (prism_data.z BETWEEN 0 AND 1) AND prism_data.epoch >= 1418763713 GROUP BY prism_data.action_id, prism_data.player_id, prism_data.block_id, ex.data, DATE(FROM_UNIXTIME(prism_data.epoch)) ORDER BY prism_data.epoch DESC, x ASC, z ASC, y ASC, id DESC LIMIT 10000;";
        
        System.out.println(query);
        
        return null;
    }
    
    public static void main(String[] args) {
        DimensionRollback rollback = new DimensionRollback(new Dimension(Vector.ZERO, new Vector(100, 30, 100)), new Date(), 50000);
        rollback.lookup("myWorld", new Dimension(Vector.ZERO, Vector.ONE), new Date());
        
    }
    
    
    
}
