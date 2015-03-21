/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.bukkit;

import com.chingo247.menu.util.EconomyUtil;
import com.chingo247.settlercraft.plugin.EconomyProvider;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;

/**
 *
 * @author Chingo
 */
public class BKEconomyProvider implements EconomyProvider{
    
    private Economy economy;

    public BKEconomyProvider() {
        this.economy = EconomyUtil.getInstance().getEconomy();
    }
    
    

    @Override
    public void give(UUID player, double amount) {
        //
    }

    @Override
    public void withdraw(UUID player, double amount) {
        //
    }

    @Override
    public void transfer(UUID fromPlayer, UUID toPlayer, double amount) {
        //
    }
    
}
