/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.entities;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.validator.NotNull;

/**
 *
 * @author Chingo
 */
@Entity
public class SettlerCraftPlayerEntity implements Serializable {
    
    @Id
    @NotNull
    private UUID uuid;
    @NotNull
    private String name;

    
    /**
     * JPA Constructor.
     */
    SettlerCraftPlayerEntity() {
    }

    /**
     * Constructor
     * @param uuid The player UUID
     * @param name The name of the player
     */
    public SettlerCraftPlayerEntity(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * Gets the name of the player
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the UUID of the player
     * @return The UUID of the player
     */
    public UUID getUuid() {
        return uuid;
    }
    
    

    
}
