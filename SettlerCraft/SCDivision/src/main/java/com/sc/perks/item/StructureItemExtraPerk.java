/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.perks.item;

import com.sc.perks.BonusExtra;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public final class StructureItemExtraPerk extends StructureItemPerk {

    
    private HashMap<Integer, BonusExtra> affectedMaterials;
    private double extraIncrement = 0.00d;
    private double extraChanceIncrement = 0.00d;

    public StructureItemExtraPerk(String name) {
        super(name);
    }

    public void putExtra(int material, double chance, int extra) {
        this.affectedMaterials.put(material, new BonusExtra(chance, extra));
    }

    int getExtra(ItemStack stack) {
        BonusExtra m = affectedMaterials.get(stack.getTypeId());
        return m == null ? 0 : m.succeed() ? m.getExtra(): 0;
    }

}
