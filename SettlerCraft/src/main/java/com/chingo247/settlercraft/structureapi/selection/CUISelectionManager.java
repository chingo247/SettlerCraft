
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.chingo247.settlercraft.structureapi.selection;

import com.chingo247.settlercraft.bukkit.WorldEditUtil;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class CUISelectionManager {

    private final Logger log = Logger.getLogger(CUISelectionManager.class);
    private final Map<UUID, CUISelection> selections = Collections.synchronizedMap(new HashMap<UUID, CUISelection>());
    private static CUISelectionManager instance;

    private class CUISelection {

        private final long checksum;
        private final Vector pos1;
        private final Vector pos2;
        private final Player player;

        public CUISelection(Player player, SchematicData schematic, Vector target, Vector pos2) {
            this.checksum = schematic.getChecksum();
            this.pos1 = target;
            this.pos2 = pos2;
            this.player = player;
        }


        
        
        

    }
    
    public static CUISelectionManager getInstance() {
        if(instance == null) {
            instance = new CUISelectionManager();
        }
        return instance;
    }

    public void select(Player player, SchematicData schematic, Vector pos1, Vector pos2) {
        CUISelection selection = new CUISelection(player, schematic, pos1, pos2);
        LocalPlayer ply = WorldEditUtil.wrapPlayer(player);

        LocalSession session = WorldEditUtil.getWorldEditPlugin().getWorldEdit().getSession(ply);
        World world = WorldEditUtil.getWorld(player.getWorld().getName());

        selections.put(player.getUniqueId(), selection);
        
        session.getRegionSelector(world).selectPrimary(pos1, null);
        session.getRegionSelector(world).selectSecondary(pos2, null);
        session.dispatchCUISelection(ply);
    }

    public void clear(Player player, boolean talk) {
        LocalPlayer ply = WorldEditUtil.wrapPlayer(player);
        LocalSession session = WorldEditUtil.getWorldEditPlugin().getWorldEdit().getSession(ply);
        World world = WorldEditUtil.getWorld(player.getWorld().getName());
        if (session.getRegionSelector(world).isDefined()) {
            session.getRegionSelector(world).clear();
            session.dispatchCUISelection(ply);
        }
        selections.remove(player.getUniqueId());
    }
    
    
    public boolean matchesSelection(Player player, SchematicData schematic, Vector pos1, Vector pos2) {
        CUISelection selection = selections.get(player.getUniqueId());
        if(selection == null) {
            return false;
        }
        return selection.pos1.equals(pos1) 
                && selection.pos2.equals(pos2) 
                && schematic.getChecksum() == selection.checksum;
    }
    
    public boolean hasSelection(Player player) {
        return selections.get(player.getUniqueId()) != null;
    }
}
