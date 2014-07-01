/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks.perk;

import com.sc.perks.bonus.Bonus;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class ItemPerk extends AbstractPerk {
    
    private HashMap<Integer, HashMap<String, List<Bonus>>> itemBonuses; // ItemId, Event, List of bonusses to be applied

    public ItemPerk(String name, Group group) {
        super("item-perks", name, group);
        this.itemBonuses = new HashMap<>();
    }

    @Override
    public void levelUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
