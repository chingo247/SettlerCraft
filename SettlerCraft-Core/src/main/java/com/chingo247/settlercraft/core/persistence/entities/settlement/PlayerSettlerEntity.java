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
package com.chingo247.settlercraft.core.persistence.entities.settlement;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Index;

/**
 *
 * @author Chingo
 */
@Entity(name = "player_settler")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "settlement"}))
public class PlayerSettlerEntity implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "player_name")
    private String playerName;
    
    @Index(name = "player_index")
    @Column(name = "player_id")
    private UUID playerId;
    
    @Index(name = "settlement_index")
    @Column(name = "settlement")
    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private SettlementEntity settlementEntity;

    /**
     * JPA Constructor
     */
    protected PlayerSettlerEntity() {
    }

    public PlayerSettlerEntity(UUID playerUUID, String playerName, SettlementEntity settlementEntity) {
        this.playerName = playerName;
        this.playerId = playerUUID;
        this.settlementEntity = settlementEntity;
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

    public SettlementEntity getSettlementEntity() {
        return settlementEntity;
    }

}
