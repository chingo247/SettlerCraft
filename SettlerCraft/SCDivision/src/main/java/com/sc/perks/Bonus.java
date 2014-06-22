/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.perks;

import net.minecraft.util.com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class Bonus {

    private Double chance;

    public Bonus(Double chance) {
        Preconditions.checkArgument(chance > 0 && chance <= 1.0d);
        this.chance = chance;
    }

    public void setChance(Double chance) {
        Preconditions.checkArgument(chance > 0 && chance <= 1.0d);
        this.chance = chance;
    }

    public Double getChance() {
        return chance;
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
