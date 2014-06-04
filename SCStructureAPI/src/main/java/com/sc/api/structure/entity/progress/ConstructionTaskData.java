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
package com.sc.api.structure.entity.progress;

import com.sc.api.structure.entity.world.WorldDimension;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.validation.constraints.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * @author Chingo
 *
 */
@Embeddable
public class ConstructionTaskData implements Serializable {

    private UUID worldId;
    @NotNull
    private Timestamp createdAt;
    private double refundValue;
    @Embedded
    private WorldDimension region;
    private String regionId;
    
    private boolean refundable = true;

    protected ConstructionTaskData() {
    }

    ConstructionTaskData(double refundValue, WorldDimension region, String regionId) {
        this.refundValue = refundValue;
        this.region = region;
        this.regionId = regionId;
        this.createdAt = new Timestamp(new Date().getTime());
        this.worldId = Bukkit.getWorld(region.getMax().getWorld().getName()).getUID();
    }
    
    public World getWorld() {
        return Bukkit.getWorld(worldId);
    }

    public UUID getWorldId() {
        return worldId;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public double getRefundValue() {
        return refundValue;
    }

    public WorldDimension getRegion() {
        return region;
    }

    public String getRegionId() {
        return regionId;
    }

    public boolean isRefunded() {
        return refundable;
    }

    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }
    
    

}
