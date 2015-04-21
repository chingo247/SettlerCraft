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
package com.chingo247.settlercraft.core.persistence.util;

import java.util.Map;
import net.minecraft.util.com.google.common.collect.Maps;

/**
 *
 * @author Chingo
 */
public class IdGeneratorFactory {
    
    private final Map<String, IdManager> generators;
    private static IdGeneratorFactory instance;

    private IdGeneratorFactory() {
        this.generators = Maps.newHashMap();
    }
    
    public static IdGeneratorFactory getInstance() {
        if(instance == null) {
            instance = new IdGeneratorFactory();
        }
        return instance;
    }
    
    /**
     * Gets the id Generator for the given name.
     * If the generator does not exist, a new one is created with the given name.
     * @param name The name of the generator
     * @return 
     */
    public IdManager getIdGeneratorForName(String name) {
        synchronized(generators) {
            IdManager idManager = generators.get(name);
            if(idManager == null) {
                idManager = new IdManager(name);
                generators.put(name, idManager);
            }
            return idManager;
        }
    }
    
    
}
