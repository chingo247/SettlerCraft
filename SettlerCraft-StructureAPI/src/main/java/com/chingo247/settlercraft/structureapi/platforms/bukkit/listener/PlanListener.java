package com.chingo247.settlercraft.structureapi.platforms.bukkit.listener;

/*
 * Copyright (C) 2015 Chingo
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
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlatform;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.util.BKWorldEditUtil;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.event.handlers.StructurePlaceHandler;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Chingo
 */
public class PlanListener implements Listener {

    private final StructurePlaceHandler placeHandler;

    public PlanListener(IEconomyProvider provider) {
        this.placeHandler = new StructurePlaceHandler(provider);
    }

    @EventHandler
    public void onPlayerUsePlan(final PlayerInteractEvent pie) {
        if (pie.getItem() == null) {
            return;
        }
        

        AItemStack stack = BukkitPlatform.wrapItem(pie.getItem());
        if (StructurePlaceHandler.isStructurePlan(stack)) {
            pie.setCancelled(true);
        }

        WorldEditPlugin plugin = BKWorldEditUtil.getWorldEditPlugin();
        Player player = plugin.wrapPlayer(pie.getPlayer());
        Block block = pie.getClickedBlock();

        if (block == null || pie.getAction().equals(Action.RIGHT_CLICK_AIR) || pie.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            placeHandler.handleDeselect(player);
            return;
        }

        Location loc = block.getLocation();
        placeHandler.handle(stack, player, SettlerCraft.getInstance().getWorld(player.getWorld().getName()), new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

}
