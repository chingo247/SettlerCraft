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
package com.chingo247.settlercraft.structureapi.event;

import com.google.common.eventbus.EventBus;

/**
 *
 * @author Chingo
 */
public class EventManager {
    
    private static EventManager instance;
    private final EventBus eventBus = new EventBus();

    private EventManager() {}
    
    public static EventManager getInstance() {
        if(instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
    
    
    
    
    
}