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
package com.chingo247.settlercraft.core.platforms.bukkit;

import com.chingo247.settlercraft.core.platforms.services.IPermissionRegistry;
import com.chingo247.settlercraft.core.platforms.services.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Chingo
 */
public class BKPermissionRegistry implements IPermissionRegistry {

    @Override
    public void registerPermission(Permission permission) {
        org.bukkit.permissions.PermissionDefault defaultValue;
        switch(permission.getDefault()) {
            case FALSE: defaultValue = PermissionDefault.FALSE;
                break;
            case TRUE: defaultValue = PermissionDefault.TRUE;
                break;
            case NOT_OP: defaultValue = PermissionDefault.NOT_OP;
                break;
            case OP: defaultValue = PermissionDefault.OP;
                break;
            default: 
                throw new AssertionError("Unreachable");
        }
        
        
        Bukkit.getPluginManager().addPermission(new org.bukkit.permissions.Permission(permission.getName(), defaultValue));
    }
    
}
