package com.chingo247.structureapi.plugin.bukkit.listener;

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
import com.chingo247.proxyplatform.core.AItemStack;
import com.chingo247.proxyplatform.platforms.bukkit.BukkitPlatform;
import com.chingo247.settlercraft.core.services.IEconomyProvider;
import com.chingo247.structureapi.plugin.bukkit.util.BKWorldEditUtil;
import com.chingo247.structureapi.SettlerCraft;
import com.chingo247.structureapi.structure.handlers.StructurePlaceHandler;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.entity.Player;
import java.util.concurrent.ExecutorService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Chingo
 */
public class PlanListener implements Listener {
    
    private StructurePlaceHandler placeHandler;


    public PlanListener(SettlerCraft settlerCraft, ExecutorService service, IEconomyProvider provider) {
        this.placeHandler = new StructurePlaceHandler(service, provider,settlerCraft);
    }

    @EventHandler
    public void onPlayerUsePlan(final PlayerInteractEvent pie) {
        if(pie.getItem() == null) return;
        if(pie.getClickedBlock() == null) return;
        
        AItemStack stack = BukkitPlatform.wrapItem(pie.getItem());
        if(StructurePlaceHandler.isStructurePlan(stack)) {
            System.out.println("Is Structureplan!");
            pie.setCancelled(true);
        }
        
        WorldEditPlugin plugin = BKWorldEditUtil.getWorldEditPlugin();
        Player player = plugin.wrapPlayer(pie.getPlayer());
        Block block = pie.getClickedBlock();
        if(block !=  null) { 
            Location loc = block.getLocation();
            placeHandler.handle(stack, player, SettlerCraft.getInstance().getWorld(player.getWorld().getName()), new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        }
    }



  
}
