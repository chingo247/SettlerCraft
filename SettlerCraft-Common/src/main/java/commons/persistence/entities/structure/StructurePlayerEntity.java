
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

package commons.persistence.entities.structure;

import com.sk89q.worldedit.entity.Player;
import java.io.Serializable;
import java.util.Objects;
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
@Entity(name = "structure_owner")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "structure_id"}))
public class StructurePlayerEntity implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Index(name = "player_id_index")
    @Column(name = "player_id")
    private final UUID player;
    
    @Column(name = "player_name")
    private final String playerName;
    
    @Index(name = "structure_index")
    @ManyToOne(cascade = CascadeType.ALL)
    private StructureEntity structureentity;
    
    @Column(updatable = false, name = "structure_id")
    private long structureId;
    
    @Index(name = "structure_player_role_index")
    private StructurePlayerRole playerRole;
    
    /**
     * JPA Constructor.
     */
    protected StructurePlayerEntity() {
        this.player = null;
        this.playerName = null;
    }
    
    /**
     * Constructor.
     * @param structure The structure
     * @param player Whether the owner is a isPlayer or not
     */
    StructurePlayerEntity(Player player, StructureEntity structure, StructurePlayerRole playerRole) {
        Objects.requireNonNull(structure);
        Objects.requireNonNull(structure.getId());
        this.structureentity = structure;
        this.player = player.getUniqueId();
        this.playerName = player.getName();
        this.playerRole = playerRole;
        this.structureId = structure.getId();
    }

    public Long getId() {
        return id;
    }
    
    public StructurePlayerRole getPlayerRole() {
        return playerRole;
    }

    public String getName() {
        return playerName;
    }

    public UUID getPlayerUUID() {
        return player;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.player != null ? this.player.hashCode() : 0);
        hash = 71 * hash + (this.structureentity != null ? this.structureentity.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructurePlayerEntity other = (StructurePlayerEntity) obj;
        if (this.player != other.player && (this.player == null || !this.player.equals(other.player))) {
            return false;
        }
        if (this.structureentity != other.structureentity && (this.structureentity == null || !this.structureentity.equals(other.structureentity))) {
            return false;
        }
        return true;
    }
    
}
