/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.action;

import com.google.common.base.Preconditions;
import com.settlercraft.model.entity.structure.Builder;
import com.settlercraft.model.entity.structure.Structure;
import com.settlercraft.model.entity.structure.StructurePlan;
import com.settlercraft.persistence.StructureService;
import com.settlercraft.util.location.LocationUtil;
import com.settlercraft.util.location.LocationUtil.DIRECTION;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Chingo
 */
public class BuildAction extends SettlerCraftAction {

    public BuildAction() {}

    /**
     * Places an unfinished building at target location. The orientation of the building will be
     * determined by the yaw of the player
     *
     * @param player The player who places the building
     * @param target The target location
     * @param plan The structure plan of the structure
     */
    public void placeStructure(Player player, Location target, StructurePlan plan) {
        Preconditions.checkArgument(player.isOnline());
        placeStructure(player, target, plan, LocationUtil.getDirection(player.getLocation().getYaw()));
    }

    /**
     * Places an unfinished building at target location. The orientation must be given
     *
     * @param player
     * @param target
     * @param plan
     * @param direction
     */
    public void placeStructure(Player player, Location target, StructurePlan plan, DIRECTION direction) {
        StructureService ss = new StructureService();
        Structure structure = new Structure(player, target, direction, plan);
        
        if(ss.overlaps(structure)) {
            player.sendMessage(ChatColor.RED + "[SC]: Structure OVERLAPS");
            return;
        }
        
        Builder.clearBuildSite(structure);
        Builder.createDefaultFoundation(structure);
        Builder.placeStructureChest(structure);
        Builder.placeStructureSign(structure);
        Builder.instantBuildStructure(player.getLocation(), target, plan.getSchematic());
        ss.save(structure);
    }

}
