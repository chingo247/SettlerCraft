/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Chingo
 */
public class PermissionManager {

    private static final String prefix = "settlercraft.";

    public enum Perms {

        OPEN_PLAN_MENU(new Permission(prefix + "user.menu.planmenu", "open the plan menu to select plans for free", PermissionDefault.TRUE)),
        OPEN_PLAN_SHOP(new Permission(prefix + "user.menu.planshop", "open the plan shop to buy plans", PermissionDefault.TRUE)),
        STRUCTURE_PLACE(new Permission(prefix + "user.structure.place", "placing structures", PermissionDefault.TRUE));

        final Permission PERM;
        
        Perms(Permission perm) {
            this.PERM = perm;
            Bukkit.getPluginManager().addPermission(perm);
        }
        
        public Permission getPermission() {
            return PERM;
        }
    }

    public static boolean isAllowed(Player player, Perms perms) {
        if (player == null) {
            return false;
        }
        if (player.isOp()) {
            return true;
        }

        return player.hasPermission(perms.PERM);
    }
    


}
