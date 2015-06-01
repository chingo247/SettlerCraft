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
package com.chingo247.settlercraft.core.platforms.services;

import java.util.UUID;

/**
 * 
 * @author Chingo
 */
public interface IEconomyProvider {
    
    /**
     * Gets the name of the Service
     * @return The name of the Service 
     */
   public String getName();
    
   public void give(UUID player, double amount);
   
   public void withdraw(UUID player, double amount);
   
   public double getBalance(UUID player);
   
   public boolean isEnabled();
   
}
