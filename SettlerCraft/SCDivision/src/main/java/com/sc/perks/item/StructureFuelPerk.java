/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks.item;

import com.sc.perks.Perk;

/**
 *
 * @author Chingo
 */
public class StructureFuelPerk extends Perk {
    
    private int time;
    private int timeIncrease = 0;
    private double timeIncreaseChance = 0.0d;
    private double unusedChance = 0.0d; 
    
    
    public StructureFuelPerk(String name, int increase, double chance) {
        super(name);
        this.timeIncreaseChance = chance;
    }
    
    

   
    
    
    
    
    
}
