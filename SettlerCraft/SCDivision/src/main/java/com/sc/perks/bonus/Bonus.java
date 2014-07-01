/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.perks.bonus;

import net.minecraft.util.com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 * @param <T> The bonus
 */
public abstract class Bonus<T> {

    protected int chanceIncreasesEvery = 1; // Increases when level % increase == 0
    protected int effectivenessIncreasesEvery = 1; // Increases when level % increase == 0
    protected double chance;
    protected double effectivenesIncrement = 0.0d;
    protected double bonusEffectivenes = 0.0d;
    protected double chanceIncrement = 0.0d;

    public Bonus(double chance) {
        Preconditions.checkArgument(chance > 0 && chance <= 1.0d);
        this.chance = chance;
    }

    public void setChance(double chance) {
        Preconditions.checkArgument(chance > 0 && chance <= 1.0d);
        this.chance = chance;
    }

    public Double getChance() {
        return chance;
    }

    public void setChanceIncrement(double chanceIncrement) {
        this.chanceIncrement = chanceIncrement;
    }

    public void setEffectivenesIncrement(double effectivenesIncrement) {
        this.bonusEffectivenes = effectivenesIncrement;
    }

    public void setIncreasesEvery(int increasesEvery) {
        this.chanceIncreasesEvery = increasesEvery;
    }

    public double getChanceIncrement() {
        return chanceIncrement;
    }

    public double getEffectivenesIncrement() {
        return bonusEffectivenes;
    }

    public int getIncreasesEvery() {
        return chanceIncreasesEvery;
    }
    
    public abstract T getBonus();
    public abstract void setBonus(T bonus);
    public void levelUp(int perkLevel) {
        if(perkLevel % chanceIncreasesEvery == 0) {
            
        }
    }

    /**
     * Rolls the 'dice', if the number was lower or equal to the chance, then this bonus was succesful.
     * If the chance was 1.0 (100%) then this method will return true instantly. 
     * @return If succesful
     */
    public boolean succeed() {
        if(chance == 1.0d) {
            return true;
        }
        if(Math.random() <= chance) {
            return true;
        }
        return false;
    }
}
