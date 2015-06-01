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
package com.chingo247.menuapi.menu;

import com.chingo247.xplatform.platforms.bukkit.BukkitPlatform;
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
public class BKMenuAPIListener  implements Listener {

    private final Logger LOG = Logger.getLogger(BKMenuAPIListener.class);
    
    @EventHandler
    public void onServerReloadEvent(PluginDisableEvent disableEvent) {
        if (disableEvent.getPlugin().getName().equals("SettlerCraft-MenuAPI")) {
            MenuAPI.getInstance().onServerReload();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent pqe) {
            MenuAPI.getInstance().onPlayerLeave(pqe.getPlayer().getUniqueId());
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
            
            
            if(MenuAPI.getInstance().onPlayerClick(ice.getRawSlot(), player.getUniqueId(), click, BukkitPlatform.wrapItem(ice.getCurrentItem()), BukkitPlatform.wrapItem(ice.getCursor()))) {
                if(ice.getRawSlot() < 54) {
                    ice.setCancelled(true); // cancel this event
                }
            }
        }

       

    }

}
