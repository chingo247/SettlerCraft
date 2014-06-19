/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cc.plugin.scdivision.behaviors;

import com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class ItemMultiplier {
    
    private float chance = 1.0f;
    private float multiplyValue = 0;

    public void setChance(float chance) {
        Preconditions.checkArgument(chance >= 0f && chance <= 1.0f);
        this.chance = chance;
    }
    
    
    
}
