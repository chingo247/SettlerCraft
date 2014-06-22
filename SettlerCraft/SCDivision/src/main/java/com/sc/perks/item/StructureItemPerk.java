/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.perks.item;

import com.sc.perks.Perk;
import java.util.HashMap;
import java.util.List;
import org.bukkit.enchantments.Enchantment;

/**
 * MaterialPerks multiply or give bonusses to certain Material drops Each StructureItemPerk has a
 * Type, the type represents the event that caused the drop.
 *
 * @author Chingo
 */
public class StructureItemPerk extends Perk {

    public enum Type {
        FURNACE,
        BLOCK_BREAK,
        CRAFT,
        BREW,
        SLAY
        
    }

    private int requiredLevel = 0;
    private int perkLevel = 0; // The level of this perk
    private int extraIncrease = 1; // Increases when level / extraIncrease % extraIncrease == 0

    private double extraChanceIncrement = 0.00d;

    StructureItemPerk(String name) {
        super(name);
    }

    private HashMap<Integer, List<Enchantment>> enchantments;


}
