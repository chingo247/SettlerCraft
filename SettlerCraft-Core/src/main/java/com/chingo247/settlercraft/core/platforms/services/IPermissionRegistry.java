/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.platforms.services;

import com.chingo247.settlercraft.core.platforms.services.permission.Permission;

/**
 *
 * @author Chingo
 */
public interface IPermissionRegistry {
    
    public void registerPermission(Permission permission);
    
}
