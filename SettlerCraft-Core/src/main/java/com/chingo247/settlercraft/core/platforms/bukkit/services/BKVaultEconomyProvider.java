/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.core.platforms.bukkit.services;

import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Chingo
 */
public class BKVaultEconomyProvider implements IEconomyProvider{
    
    private RegisteredServiceProvider<Economy> economProvider;
    private Economy economy;

    public BKVaultEconomyProvider() {
        this.economProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if(economProvider != null) {
            this.economy = economProvider.getProvider();
        } else {
            System.out.println("******************** [ WARNING ] ********************");        
            System.out.println("[SettlerCraft-Core]: Vault economy failed to register");
            System.out.println("*****************************************************");   
        }
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

    @Override
    public String getName() {
        return "Bukkit-Vault";
    }
    
    

  
}
