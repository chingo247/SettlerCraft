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
package com.chingo247.settlercraft.structureapi.world;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class World implements Serializable {
    
    @Column(updatable = false, length = 100)
    private String name;
    
    @Column(updatable = false)
    private UUID uuid;

    /**
     * JPA Constructor
     */
    protected World() {}

    public World(String name, UUID uuid) {
        Preconditions.checkNotNull(uuid);
        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(name.length() <= 100);
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }
    
}
