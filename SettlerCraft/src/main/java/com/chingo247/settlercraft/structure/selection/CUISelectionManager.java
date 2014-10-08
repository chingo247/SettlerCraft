/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.selection;

import com.chingo247.settlercraft.structure.schematic.Schematic;
import com.chingo247.settlercraft.util.WorldEditUtil;
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

        public CUISelection(Player player, Schematic schematic, Vector target, Vector pos2) {
            this.checksum = schematic.getCheckSum();
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

    public void select(Player player, Schematic schematic, Vector pos1, Vector pos2) {
        CUISelection selection = new CUISelection(player, schematic, pos1, pos2);
        LocalPlayer ply = WorldEditUtil.getLocalPlayer(player);

        LocalSession session = WorldEditUtil.getWorldEditPlugin().getWorldEdit().getSession(ply);
        World world = WorldEditUtil.getWorld(player.getWorld().getName());

        selections.put(player.getUniqueId(), selection);
        
        session.getRegionSelector(world).selectPrimary(pos1, null);
        session.getRegionSelector(world).selectSecondary(pos2, null);
        session.dispatchCUISelection(ply);
    }

    public void clear(Player player, boolean talk) {
        LocalPlayer ply = WorldEditUtil.getLocalPlayer(player);
        LocalSession session = WorldEditUtil.getWorldEditPlugin().getWorldEdit().getSession(ply);
        World world = WorldEditUtil.getWorld(player.getWorld().getName());
        if (session.getRegionSelector(world).isDefined()) {
            session.getRegionSelector(world).clear();
            session.dispatchCUISelection(ply);
        }
        selections.remove(player.getUniqueId());
    }
    
    
    public boolean matchesSelection(Player player, Schematic schematic, Vector pos1, Vector pos2) {
        CUISelection selection = selections.get(player.getUniqueId());
        if(selection == null) {
            return false;
        }
        return selection.pos1.equals(pos1) 
                && selection.pos2.equals(pos2) 
                && schematic.getCheckSum() == selection.checksum;
    }
    
    public boolean hasSelection(Player player) {
        return selections.get(player.getUniqueId()) != null;
    }
}
