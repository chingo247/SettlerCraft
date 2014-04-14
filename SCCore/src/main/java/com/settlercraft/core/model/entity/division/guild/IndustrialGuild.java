/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.entity.division.guild;

import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * An industrial guild processes itemstacks into an itemstack of another type
 * e.g. a bakery will process an itemstack of wheat into bread 
 * @author Chingo
 */
public interface IndustrialGuild  {
    
public List<ItemStack> getInput();
public List<ItemStack> getOutput();
    
    


    
    
}
