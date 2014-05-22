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
package com.sc.api.structure.model;

import com.sc.api.structure.model.plan.ReservedSide;
import java.io.Serializable;
import java.util.EnumMap;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class ReservedArea implements Serializable {

    private final int r_zPlus;
    private final int r_xPlus;
    private final int r_xMinus;
    private final int r_zMinus;
    private final int r_up;
    private final int r_down;

    /**
     * JPA Constructor.
     */
    protected ReservedArea() {
        r_down = 0;
        r_up = 0;
        r_xMinus = 0;
        r_xPlus = 0;
        r_zMinus = 0;
        r_zPlus = 0;
    }

    public ReservedArea(Structure structure) {
        EnumMap<ReservedSide, Integer> reserved = structure.getPlan().getReserved();
        switch (structure.getCardinal()) {
            
            case NORTH:
                this.r_zMinus = reserved.get(ReservedSide.NORTH);
                this.r_xPlus = reserved.get(ReservedSide.EAST);
                this.r_zPlus = reserved.get(ReservedSide.SOUTH);
                this.r_xMinus = reserved.get(ReservedSide.WEST);
                break;
            case EAST:
                this.r_zPlus = reserved.get(ReservedSide.EAST);
                this.r_xPlus = reserved.get(ReservedSide.NORTH);
                this.r_zMinus = reserved.get(ReservedSide.WEST);
                this.r_xMinus = reserved.get(ReservedSide.SOUTH);
                break;
            case SOUTH:
                this.r_zMinus = reserved.get(ReservedSide.SOUTH);
                this.r_xPlus = reserved.get(ReservedSide.WEST);
                this.r_zPlus = reserved.get(ReservedSide.NORTH);
                this.r_xMinus = reserved.get(ReservedSide.EAST);
                break;
            case WEST:
                this.r_zPlus = reserved.get(ReservedSide.WEST);
                this.r_xPlus = reserved.get(ReservedSide.SOUTH);
                this.r_zMinus = reserved.get(ReservedSide.EAST);
                this.r_xMinus = reserved.get(ReservedSide.NORTH);
                break;
            default:
                throw new AssertionError("Unreachable");
        }
        this.r_up = reserved.get(ReservedSide.UP);
        this.r_down = reserved.get(ReservedSide.DOWN);
    }

    public int getR_zPlus() {
        return r_zPlus;
    }

    public int getR_xPlus() {
        return r_xPlus;
    }

    public int getR_xMinus() {
        return r_xMinus;
    }

    public int getR_zMinus() {
        return r_zMinus;
    }

    public int getR_up() {
        return r_up;
    }

    public int getR_down() {
        return r_down;
    }

    
}
