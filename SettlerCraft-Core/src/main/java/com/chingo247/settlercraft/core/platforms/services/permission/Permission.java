/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.platforms.services.permission;

/**
 *
 * @author Chingo
 */
public class Permission {
    
    private PermissionDefault defaultValue;
    private String name;
    private String description;

    public Permission(String name, PermissionDefault defaultValue, String description) {
        this.defaultValue = defaultValue;
        this.name = name;
        this.description = description;
    }
    
    public PermissionDefault getDefault() {
        return defaultValue;
    }
    
    public String getName() {
        return name;
    }
    
}
