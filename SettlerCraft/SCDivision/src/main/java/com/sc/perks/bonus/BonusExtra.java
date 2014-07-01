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
public class BonusExtra extends Bonus<Integer> {
    
    // The amount of extraBonus
    private Integer extraBonus;

    public BonusExtra(Double chance, Integer extra) {
        super(chance);
        this.extraBonus = extra;
    }

    public void setExtra(Integer extra) {
        
    }

    @Override
    public Integer getBonus() {
        return (int) Math.ceil(extraBonus + bonusEffectivenes);
    }

    @Override
    public void setBonus(Integer bonus) {
        this.extraBonus = bonus;
    }



    
    
    
    
}
