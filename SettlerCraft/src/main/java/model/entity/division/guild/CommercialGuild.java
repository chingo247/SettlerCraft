/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model.entity.division.guild;

import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 * Commercial guild is a guild that only sell goods, like markets
 * @author Chingo
 */
public interface CommercialGuild {
    
    public List<ItemStack> getGoods();
    /**
     * The goods this guild will accept to sell
     * @return 
     */
    public List<ItemStack> acceptsGoods();
    
}
