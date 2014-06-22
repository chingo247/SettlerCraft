/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.perks.item;

import com.sc.perks.BonusMultiplier;
import java.util.Collection;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public final class StructureItemMultiplierPerk extends StructureItemPerk {

    private HashMap<Integer, BonusMultiplier> multipliers;
    private double multiplierIncrement = 0.00d;
    private double multiplierChanceIncrement = 0.00d;

    public StructureItemMultiplierPerk(String name) {
        super(name);
    }

    public void putMultiplier(int material, double chance, double multiplier) {
        this.multipliers.put(material, new BonusMultiplier(chance, multiplier));
    }

    double getMultiplier(ItemStack stack) {
        BonusMultiplier m = multipliers.get(stack.getTypeId());
        return m == null ? 0 : m.succeed() ? m.getMultiplier() : 0;
    }

    public static void apply(Collection<StructureItemPerk> perks, ItemStack stack) {
        double multipliers = 1.0d;
        for (StructureItemPerk perk : perks) {
            if (perk instanceof StructureItemMultiplierPerk) {
                multipliers += ((StructureItemMultiplierPerk) perk).getMultiplier(stack);
            }
        }

        int amount = stack.getAmount();

        amount *= (int) multipliers;
        stack.setAmount(amount);
    }
}
