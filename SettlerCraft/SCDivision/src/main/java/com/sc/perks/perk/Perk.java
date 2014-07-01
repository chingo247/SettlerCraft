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
public class Perk extends AbstractPerk {
    
    private HashMap<String, List<Bonus>> bonusses;

    public Perk(String name, Group group) {
        super("casual-perks", name, group);
        this.bonusses = new HashMap<>();
    }

    @Override
    public void levelUp() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
