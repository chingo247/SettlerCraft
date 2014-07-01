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
public class EntityPerk extends AbstractPerk {
    
    private HashMap<Integer, HashMap<String, List<Bonus>>> itemBonuses; // Entity, Event, List of bonusses to be applied

    public EntityPerk(String name, Group group) {
        super("entity-perks", name, group);
    }

    @Override
    public void levelUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
