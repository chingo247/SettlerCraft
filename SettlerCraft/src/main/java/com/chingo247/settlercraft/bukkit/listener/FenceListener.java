///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.settlercraft.bukkit.listener;
//
//import com.chingo247.settlercraft.structureapi.persistence.hibernate.StructureDAO;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
//import com.sk89q.worldedit.Vector;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.BlockBreakEvent;
//
///**
// * @author Chingo
// */
//public class FenceListener implements Listener {
//
//    private final StructureDAO structureDAO = new StructureDAO();
//
//    @EventHandler
//    public void onFenceBreak(BlockBreakEvent bbe) {
//        if (bbe.getBlock().getType() == Material.IRON_FENCE) {
//            Location l = bbe.getBlock().getLocation();
//            Structure structure = structureDAO.getStructure(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
//            if (structure != null && structure.getState() != Structure.State.COMPLETE) {
//                Vector v = structure.getRelativePosition(new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
//                CuboidDimension dim = structure.getDimension();
//
//                if (v.getBlockY() == 1 && (l.getBlockX() == dim.getMinX() || l.getBlockX() == dim.getMaxX() || l.getBlockZ() == dim.getMinZ() || l.getBlockZ() == dim.getMaxZ())) {
//                    if (bbe.getPlayer() != null) {
//                        bbe.getPlayer().sendMessage(ChatColor.RED + "Can't destroy fence....");
//                    }
//                    bbe.setCancelled(true);
//                }
//            }
//        }
//
//    }
//
//}
