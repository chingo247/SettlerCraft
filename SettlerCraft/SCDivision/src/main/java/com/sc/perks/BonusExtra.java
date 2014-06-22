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
public class BonusExtra extends Bonus {
    
    // The amount of extra
    private Integer extra;

    public BonusExtra(Double chance, Integer extra) {
        super(chance);
        this.extra = extra;
    }

    public Integer getExtra() {
        return extra;
    }

    public void setExtra(Integer extra) {
        this.extra = extra;
    }
    
    
    
}
