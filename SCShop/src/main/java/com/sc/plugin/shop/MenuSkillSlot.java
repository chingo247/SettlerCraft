/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.shop;

import com.google.common.base.Preconditions;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 * Coming soon... SKILLAPI SUPPORT WILL BE THERE
 * @author Chingo
 */
public final class MenuSkillSlot extends MenuSlot {
    private final int maxLevel;

    public MenuSkillSlot(UUID menuId, ItemStack stack, String skillName, int maxLevel) {
        super(menuId, stack);
        Preconditions.checkArgument(maxLevel > 0);
        this.maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }
    
    
    

}
