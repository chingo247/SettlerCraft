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
package com.chingo247.settlercraft.structure.construction.restore;

import com.chingo247.settlercraft.structure.entities.world.Dimension;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import de.diddiz.LogBlock.BlockChange;
import de.diddiz.LogBlock.LogBlock;
import de.diddiz.LogBlock.QueryParams;
import de.diddiz.worldedit.RegionContainer;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class RollbackService {
    
    private final LogBlock logblock = LogBlock.getInstance();
//    private final Consumer consumer = logblock.getConsumer();

    public void rollback(World world, Dimension dimension) {
        CuboidSelection selection = new CuboidSelection(world, dimension.getMinPosition(), dimension.getMaxPosition());
        RegionContainer container = new RegionContainer(selection);
        
        QueryParams params = new QueryParams(logblock);
        params.setSelection(container);
        params.needData = true;
        params.needType = true;
        
        try {
            for(BlockChange bc : logblock.getBlockChanges(params)) {
                System.out.println(bc.toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RollbackService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
