/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public abstract class SettlerCraftModule {

    /**
     * Unique Identifier for the api
     */
    private final String name;
    
    public SettlerCraftModule(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the API
     * @return The name of the API
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Setup all recipes for this api
     * @param plugin The plugin
     */
    protected abstract void setupRecipes(JavaPlugin plugin);
    /**
     * Setup all listeners for this api
     * @param plugin The plugin
     */
    protected abstract void setupListeners(JavaPlugin plugin);

    /**
     * Completely initializes this api
     * @param plugin The plugin
     */
    public abstract void init(JavaPlugin plugin);



    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SettlerCraftModule)) {
            return false;
        }
        final SettlerCraftModule other = (SettlerCraftModule) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    
}