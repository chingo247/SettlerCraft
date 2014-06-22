/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks;

/**
 *
 * @author Chingo
 */
public class BonusMultiplier extends Bonus {
    
    private Double multiplier;

    public BonusMultiplier(Double chance, Double multiplier) {
        super(chance);
        this.multiplier = multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }


    public Double getMultiplier() {
        return multiplier;
    }
    
    
    
    
}
