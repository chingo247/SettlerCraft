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
package com.chingo247.structureapi.plugin.bukkit;

import com.chingo247.settlercraft.menuapi.menu.ACategoryMenu;
import org.apache.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

/**
 *
 * @author Chingo
 */
public class BKMenuAPI  implements Listener {

    private final Logger LOG = Logger.getLogger(BKMenuAPI.class);
    
    @EventHandler
    public void onServerReloadEvent(PluginDisableEvent disableEvent) {
        if (disableEvent.getPlugin().getName().equals("SettlerCraft")) {
            LOG.info("Disable event!");
//            onServerReload();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent pqe) {
//        onPlayerLeave(pqe.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMenuClickEvent(InventoryClickEvent ice) {
        if (ice.getWhoClicked() instanceof Player) {
            Player player = (Player) ice.getWhoClicked();
            int click;
            switch (ice.getClick()) {
                case DOUBLE_CLICK:
                    click = ACategoryMenu.DOUBLE_CLICK;
                    break;
                case LEFT:
                    click = ACategoryMenu.LEFT_CLICK;
                    break;
                case RIGHT:
                    click = ACategoryMenu.RIGHT_CLICK;
                    break;
                case SHIFT_LEFT:
                    click = ACategoryMenu.SHIFT_LEFT;
                    break;
                case SHIFT_RIGHT:
                    click = ACategoryMenu.SHIFT_RIGHT;
                    break;
                default:
                    return;
            }
            
//            if(onPlayerClick(ice.getRawSlot(), player.getUniqueId(), click, BukkitPlatform.wrapItem(ice.getCurrentItem()))) {
//                ice.setCancelled(true); // cancel this event
//            }
        }

       

    }

}
