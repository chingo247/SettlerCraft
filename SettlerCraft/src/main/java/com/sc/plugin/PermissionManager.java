/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 *
 * @author Chingo
 */
public class PermissionManager {

    private static final String prefix = "sc.";

    public enum Perms {

        OPEN_PLAN_MENU(new Permission(prefix + "open.planmenu")),
        OPEN_PLAN_SHOP(new Permission(prefix + "open.planshop")),
        STRUCTURE_PLACE(new Permission(prefix + "structure.place"));

        final Permission PERM;
        
        Perms(Permission perm) {
            this.PERM = perm;
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
