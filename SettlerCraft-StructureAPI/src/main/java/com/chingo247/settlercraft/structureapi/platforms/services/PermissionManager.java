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
package com.chingo247.settlercraft.structureapi.platforms.services;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.platforms.services.IPermissionRegistry;
import com.chingo247.settlercraft.core.platforms.services.permission.Permission;
import com.chingo247.settlercraft.core.platforms.services.permission.PermissionDefault;
import com.chingo247.xplatform.core.IPlayer;
import com.sk89q.worldedit.entity.Player;
import java.util.UUID;
import com.google.common.base.Preconditions;

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
        ROTATE_SCHEMATIC(new Permission(PREFIX + "admin.editor.rotate.schematic", PermissionDefault.FALSE, "Allows rotation of schematics")),
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
        if(player.isOP() && permission.permission.getDefault() != PermissionDefault.FALSE) return true;
        return player.hasPermission(permission.permission.getName());
    }
    
    
}
