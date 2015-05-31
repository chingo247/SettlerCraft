package com.chingo247.settlercraft.structureapi.platforms.bukkit.listener;

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
            placeHandler.handleDeselect(player, null);
            return;
        }

        Location loc = block.getLocation();
        placeHandler.handle(stack, player, SettlerCraft.getInstance().getWorld(player.getWorld().getName()), new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

}
