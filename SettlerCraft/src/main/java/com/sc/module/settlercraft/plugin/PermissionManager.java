/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Chingo
 */
public class PermissionManager {

    private static final String prefix = "sc.";

    public enum Perms {

        OPEN_PLAN_MENU(new Permission(prefix + "user.menu.planmenu", "open the plan menu to select plans for free", PermissionDefault.TRUE)),
        OPEN_PLAN_SHOP(new Permission(prefix + "user.menu.planshop", "open the plan shop to buy plans",PermissionDefault.TRUE)),
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
