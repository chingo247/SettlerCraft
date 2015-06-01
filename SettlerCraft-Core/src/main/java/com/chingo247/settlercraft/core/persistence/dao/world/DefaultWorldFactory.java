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
package com.chingo247.settlercraft.core.persistence.dao.world;

import com.chingo247.settlercraft.core.World;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class DefaultWorldFactory extends AbstractWorldFactory<World> {
    
    private static DefaultWorldFactory instance;
    
    private DefaultWorldFactory() {}
    
    public static DefaultWorldFactory instance() {
        if(instance == null) {
            instance = new DefaultWorldFactory();
        }
        return instance;
    }
    
    @Override
    public World makeWorld(WorldNode node) {
        String name = node.getName();
        UUID uuid = node.getUUID();
        return new DefaultWorld(name, uuid);
    }
        
    
}
