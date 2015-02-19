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
package com.chingo247.settlercraft.persistence.entities.zone;

import com.chingo247.settlercraft.structure.regions.CuboidDimension;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.Index;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionZoneEntity implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    @Index(name = "world_index")
    private UUID world;
    private CuboidDimension dimension;
    @Column(name = "permits_all")
    private boolean permitsAll;
    
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Column(name = "allowed_players")
    private List<ConstructionZonePlayerEntity> allowedPlayers;

    /**
     * JPA Constructor
     */
    protected ConstructionZoneEntity() {
    }

    /**
     * Constructor
     * @param world The worldUUID
     * @param dimension The dimension
     */
    public ConstructionZoneEntity(UUID world, CuboidDimension dimension) {
        this.world = world;
        this.dimension = dimension;
        this.permitsAll = false;
        this.allowedPlayers = new ArrayList<>();
    }

    public UUID getWorld() {
        return world;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }

    public CuboidDimension getDimension() {
        return dimension;
    }

    public void setDimension(CuboidDimension dimension) {
        this.dimension = dimension;
    }

    public boolean isPermitsAll() {
        return permitsAll;
    }

    public void setPermitsAll(boolean permitsAll) {
        this.permitsAll = permitsAll;
    }

    public List<ConstructionZonePlayerEntity> getAllowedPlayers() {
        return allowedPlayers;
    }

}
