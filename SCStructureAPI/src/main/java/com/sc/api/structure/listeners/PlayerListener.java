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
package com.sc.api.structure.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Chingo
 */
public class PlayerListener implements Listener {
    


    @EventHandler
    public void onPlayerBuildEvent(PlayerInteractEvent pie) {
//        if (pie.getItem() != null
//                && pie.getClickedBlock() != null
//                && pie.getItem().getItemMeta() != null
//                && pie.getItem().getItemMeta().getDisplayName() != null
//                && pie.getItem().getItemMeta().getDisplayName().equals(Recipes.CONSTRUCTION_TOOL)) {
//            // Cancel default action which would destroy blocks
//            pie.setCancelled(true);
//            if (pie.getAction() != Action.LEFT_CLICK_BLOCK) {
//                return;
//            }
//            StructureService service = new StructureService();
//            Structure structure = service.getStructure(pie.getClickedBlock().getLocation());
//            if (structure != null && structure.getStatus() != StructureState.COMPLETE) {
//                PlayerAction.build(pie.getPlayer(),structure, possibleSkillAmount);
//            }
//        }
        
    }
}
