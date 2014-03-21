/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.entity.division.guild;

import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Commercial guild is a guild that sells good, the goods could either be brought to this guild or
 * produced by the guild itself
 * @author Chingo
 */
public interface CommercialGuild {
    
    /**
     * The goods this guild will sell
     * @return List of goods this guild will sell
     */
    public List<ItemStack> getOutput();
    /**
     * The goods this guild will accept
     * @return List of goods this guild will accept
     */
    public List<ItemStack> getInput();
    
}
