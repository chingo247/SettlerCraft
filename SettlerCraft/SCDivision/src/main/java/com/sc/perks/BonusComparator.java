/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks;

import java.util.Comparator;

/**
 *
 * @author Chingo
 */
public class BonusComparator implements Comparator<Bonus>{
    
    private static final int MULTIPLIER_PRIORITY = 2;
    private static final int EXTRA_PRIORITY = 1;

    @Override
    public int compare(Bonus o1, Bonus o2) {
        return getPriority(o1).compareTo(getPriority(o2));
    }
    
    private Integer getPriority(Bonus bonus) {
        if(bonus instanceof BonusMultiplier) {
            return MULTIPLIER_PRIORITY;
        } else if (bonus instanceof BonusExtra) {
            return EXTRA_PRIORITY;
        } else {
            return 0;
        }
    }
    
    
    
}
