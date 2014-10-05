/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.menuapi.menus.menu.slots;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Chingo
 */
public class CategorySlot extends Slot {

    private final String name;
    private final Set<String> aliases = new HashSet<>();
    private final Material icon;
    private final byte data;

    public CategorySlot(String name, Material icon) {
       this(name, icon, new Integer(0).byteValue());
    }
    
    public CategorySlot(String name, Material icon, byte data) {
        this.name = name;
        this.icon = icon;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }

    public void addAliases(String... alias) {
        aliases.addAll(Arrays.asList(alias));
    }

    public boolean hasAlias(String alias) {
        return aliases.contains(alias);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CategorySlot other = (CategorySlot) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public ItemStack getIcon() {
        ItemStack is = new ItemStack(icon);
        is.setData(new MaterialData(icon.getId(), data));
        
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(null);
        is.setItemMeta(meta);
        return is;
    }

}
