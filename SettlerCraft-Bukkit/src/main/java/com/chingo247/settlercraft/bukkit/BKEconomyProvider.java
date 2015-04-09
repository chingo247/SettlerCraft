/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.bukkit;

import com.chingo247.settlercraft.plugin.IEconomyProvider;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Chingo
 */
public class BKEconomyProvider implements IEconomyProvider{
    
    private RegisteredServiceProvider<Economy> economProvider;
    private Economy economy;

    public BKEconomyProvider() {
        this.economProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        this.economy = economProvider.getProvider();
    }
    
    @Override
    public boolean isEnabled() {
        return economy != null;
    }

    @Override
    public void give(UUID player, double amount) {
        economy.depositPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public void withdraw(UUID player, double amount) {
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public double getBalance(UUID player) {
        return economy.getBalance(Bukkit.getPlayer(player));
    }
    
    

  
}