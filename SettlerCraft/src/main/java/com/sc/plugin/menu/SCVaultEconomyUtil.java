/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.menu;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Chingo
 */
public class SCVaultEconomyUtil {
    
    private Economy economy;

    private SCVaultEconomyUtil() {
        setupEconomy();
    }

    private static SCVaultEconomyUtil instance;
    
    public static SCVaultEconomyUtil getInstance() {
        if(instance == null) {
            instance = new SCVaultEconomyUtil();
        }
        return instance;
    }
    
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public Economy getEconomy() {
        return economy;
    }
    
    public boolean hasEconomy(){
        return economy != null;
    }

    
    
    public EconomyResponse pay(Player player, double amount) {
        EconomyResponse er = economy.withdrawPlayer(player.getName(), amount);
        if(er.transactionSuccess()) {
            player.sendMessage("[Economy]: your new balance is " + economy.getBalance(player.getName()));
        }
        return er;
    }
    
}
