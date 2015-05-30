/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
