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
package com.chingo247.settlercraft.structure.event.subscribers;

import com.chingo247.settlercraft.structure.AbstractStructureAPI;
import com.chingo247.settlercraft.structure.event.structure.StructureCreateEvent;
import com.chingo247.settlercraft.structure.event.structure.StructureRemoveEvent;
import com.chingo247.settlercraft.structure.event.structure.StructureStateChangeEvent;
import com.google.common.eventbus.Subscribe;

/**
 *
 * @author Chingo
 */
public class StructureSubscriber {
    
    private final AbstractStructureAPI api;

    public StructureSubscriber(AbstractStructureAPI abstractStructureAPI) {
        this.api = abstractStructureAPI;
    }
    
    @Subscribe
    public void onStructureCreated(StructureCreateEvent sce) {
        System.out.println(this.getClass().getName() + ": create event");
    }
    
    @Subscribe
    public void onStructureStateChange(StructureStateChangeEvent ssce) {
        System.out.println(this.getClass().getName() + ": state change event");
    }
    
    @Subscribe
    public void onStructureRemoved(StructureRemoveEvent sre) {
        System.out.println(this.getClass().getName() + ": remove event");
    } 
    
}
