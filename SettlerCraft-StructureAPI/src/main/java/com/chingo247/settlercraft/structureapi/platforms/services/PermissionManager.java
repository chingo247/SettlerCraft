/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.services;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.platforms.services.IPermissionRegistry;
import com.chingo247.settlercraft.core.platforms.services.permission.Permission;
import com.chingo247.settlercraft.core.platforms.services.permission.PermissionDefault;
import com.chingo247.xplatform.core.IPlayer;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import net.minecraft.util.com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class PermissionManager {
    
    private static final String PREFIX = "settlercraft.";
    private static PermissionManager instance;

    private PermissionManager() {
    }
    
    public static PermissionManager getInstance() {
        if(instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }
    

    /**
     * Will register permissions at the IPermissionRegistry
     * @param registry The registry to register the permissions to
     */
    public void registerPermissions(IPermissionRegistry registry) {
        Preconditions.checkNotNull(registry, "IPermissionRegistry may not be null");
        for(Perms p : Perms.values()) {
            registry.registerPermission(p.permission);
        }
    }
    
    public enum Perms {
        OPEN_PLAN_MENU(new Permission(PREFIX + "settler.open.planmenu", PermissionDefault.OP, "Allows the player to use the plan menu (contains plans for FREE)")),
        OPEN_SHOP_MENU(new Permission(PREFIX + "settler.open.shopmenu", PermissionDefault.TRUE, "Allows the player to use the plan shop")),
        PLACE_STRUCTURE(new Permission(PREFIX + "settler.place.structure", PermissionDefault.TRUE, "Allows the player to place structures"));
        private Permission permission;

        private Perms(Permission permission) {
            this.permission = permission;
        }
    }
    
    public boolean isAllowed(Player player, Perms permission) {
        return isAllowed(player.getUniqueId(), permission);
    }
    
    public boolean isAllowed(UUID player, Perms permission) {
        return isAllowed(SettlerCraft.getInstance().getPlatform().getPlayer(player), permission);
    }

    public boolean isAllowed(IPlayer player, Perms permission) {
        if(player == null) return false;
        if(player.isOP()) return true;
        return player.hasPermission(permission.permission.getName());
    }
    
    
}
