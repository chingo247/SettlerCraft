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

    private static final String prefix = "settlercraft.";

    public enum Perms {

        OPEN_PLAN_MENU(new Permission(prefix + "open.planmenu")),
        OPEN_PLAN_SHOP(new Permission(prefix + "open.planshop")),
        CONSTRUCTION_PAUSE(new Permission(prefix + "construction.pause")),
        CONSTRUCTION_CONTINUE(new Permission(prefix + "construction.continue")),
        CONSTRUCTION_CANCEL(new Permission(prefix + "construction.cancel")),
        CONSTRUCTION_REQUEUE(new Permission(prefix + "construction.requeue")),
        STRUCTURE_PLACE(new Permission(prefix + "construction.place")),
        STRUCTURE_DEMOLISH(new Permission(prefix + "structure.demolish")),
        RESTORE_REFUND(new Permission(prefix + "restore.refund")),
        RESTORE_CLEAN(new Permission(prefix + "restore.clean"));

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
