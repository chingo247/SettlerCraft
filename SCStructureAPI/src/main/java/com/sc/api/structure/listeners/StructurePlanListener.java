/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listeners;

import com.sc.api.structure.construction.FrameStrategy;
import com.sc.api.structure.construction.SCStructureAPI;
import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.util.WorldUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class StructurePlanListener implements Listener {

    private final JavaPlugin settlerCraft;

    public StructurePlanListener(JavaPlugin settlerCraft) {
        this.settlerCraft = settlerCraft;
    }

    /**
     * Places a structure on player's target location
     *
     * @param pie The playerInteractEvent
     */
    @EventHandler
    public void onPlayerBuild(PlayerInteractEvent pie) {
        if (pie.getItem() == null || pie.getItem().getType() != Material.PAPER) {
            return;
        }
        StructurePlan plan = StructurePlanManager.getInstance().getPlan(pie.getItem().getItemMeta().getDisplayName());
        if (plan != null
                && pie.getClickedBlock() != null
                && pie.getClickedBlock().getType() != Material.AIR) {
            Structure structure = new Structure(
                    pie.getPlayer(), 
                    pie.getClickedBlock().getLocation(), 
                    WorldUtil.getDirection(pie.getPlayer()),
                    plan
            );
            SCStructureAPI.build(structure).frame().construct(FrameStrategy.FANCY);
        }
    }

}
