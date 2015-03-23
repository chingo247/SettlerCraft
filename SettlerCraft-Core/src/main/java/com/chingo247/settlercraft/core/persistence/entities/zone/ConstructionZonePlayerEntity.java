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
package com.chingo247.settlercraft.core.persistence.entities.zone;

import com.sk89q.worldedit.entity.Player;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Index;

/**
 *
 * @author Chingo
 */
@Entity
public class ConstructionZonePlayerEntity implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Index(name = "construction_zone_index")
    @Column(name = "construction_zone")
    @ManyToOne(cascade = CascadeType.ALL)
    private ConstructionZoneEntity constructionZoneEntity;

    @Index(name = "player_index")
    @Column(name = "player_id")
    private UUID playerId;
    
    @Column(name = "player_name")
    private String playerName;
    
    /**
     * JPA Constructor.
     */
    protected ConstructionZonePlayerEntity() {
    }

    /**
     * Constructor.
     * @param player The player
     * @param constructionZoneEntity The constructionZone
     */
    public ConstructionZonePlayerEntity(Player player, ConstructionZoneEntity constructionZoneEntity) {
        this.constructionZoneEntity = constructionZoneEntity;
    }
    
    public Long getId() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }
    
    public ConstructionZoneEntity getConstructionZoneEntity() {
        return constructionZoneEntity;
    }
    
    

   
}
