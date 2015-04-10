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
package com.chingo247.proxyplatform.core;

import java.util.List;

/**
 *
 * @author Chingo
 */
public abstract class AItemStack {

    public abstract String getName();
    
    public abstract void setName(String name);
    
    public abstract void setLore(List<String> lore);

    public abstract List<String> getLore();

    public abstract int getAmount();
    
    public abstract void setAmount(int amount);
    
    public abstract void setMaterial(int material);

    public abstract int getMaterial();

    public abstract int getData();
    
    public boolean matches(AItemStack other) {
        return getMaterial() == other.getMaterial() 
                    && getData() == other.getData()
                    && other.getLore().equals(getLore());
    }

    public abstract AItemStack clone();
}
