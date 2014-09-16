/*
 * Copyright (C) 2014 Chingo
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
package com.sc.module.menuapi.menus.menu.util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Chingo
 */
public class EconomyUtil {
    
    private Economy economy;

    private EconomyUtil() {
        setupEconomy();
    }

    private static EconomyUtil instance;
    
    public static EconomyUtil getInstance() {
        if(instance == null) {
            instance = new EconomyUtil();
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
