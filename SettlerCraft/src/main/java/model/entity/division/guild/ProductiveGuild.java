/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entity.division.guild;

import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Productive Guilds only have output e.g. farms (wheats, eggs, etc)
 * @author Chingo
 */
public interface ProductiveGuild {
    
    public List<ItemStack> getOutput();
}
