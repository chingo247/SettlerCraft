/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks.bonus;

/**
 *
 * @author Chingo
 */
public class BonusPercentage extends Bonus<Double> {
    
    /**
     * The multiplier
     */
    private Double bonus;

    public BonusPercentage(Double chance, Double multiplier) {
        super(chance);
        this.bonus = multiplier;
    }

    @Override
    public Double getBonus() {
        return bonus + bonusEffectivenes;
    }

    @Override
    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }
    
    
}
