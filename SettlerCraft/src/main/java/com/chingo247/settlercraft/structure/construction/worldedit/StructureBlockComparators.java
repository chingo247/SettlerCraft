/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.structure.worldedit;

import java.util.Comparator;

/**
 *
 * @author Chingo
 */
public class StructureBlockComparators {

    public static final Comparator<StructureBlock> FANCY = new Comparator<StructureBlock>() {

        @Override
        public int compare(StructureBlock o1, StructureBlock o2) {
            final int CHUNK_SIZE = 16;
            int squarSize = CHUNK_SIZE * 2;
            Integer v4 = o2.getPriority().compareTo(o1.getPriority());
            if (v4 == 0) {
                Integer v1 = getPiece(o1.getPosition().getBlockY(), squarSize / 2).compareTo(getPiece(o2.getPosition().getBlockY(), squarSize / 2)); // Vertical cost less
                if (v1 == 0) {
                    Integer v2 = getPiece(o1.getPosition().getBlockZ(), squarSize).compareTo(getPiece(o2.getPosition().getBlockZ(), squarSize));
                    if (v2 == 0) {
                        return getPiece(o1.getPosition().getBlockX(), squarSize).compareTo(getPiece(o2.getPosition().getBlockX(), squarSize));
                    }
                    return v2;

                }
                return v1;
            }
            return v4;
        }
    };

    public static final Comparator<StructureBlock> COMPROMIS = new Comparator<StructureBlock>() {
        @Override
        public int compare(StructureBlock o1, StructureBlock o2) {
            final int CHUNK_SIZE = 16;
            int squarSize = CHUNK_SIZE * 2;
            Integer v4 = o2.getPriority().compareTo(o1.getPriority());

            if (v4 == 0) {
                Integer v1 = getPiece(o1.getPosition().getBlockY(), squarSize).compareTo(getPiece(o2.getPosition().getBlockY(), squarSize)); // Vertical cost less
                if (v1 == 0) {
                    Integer v2 = getPiece(o1.getPosition().getBlockZ(), squarSize).compareTo(getPiece(o2.getPosition().getBlockZ(), squarSize));
                    if (v2 == 0) {
                        Integer v3 = getPiece(o1.getPosition().getBlockX(), squarSize).compareTo(getPiece(o2.getPosition().getBlockX(), squarSize));
                        if (v3 == 0) {
                            return new Integer(o1.getPosition().getBlockY()).compareTo(o2.getPosition().getBlockY());
                        }
                        return v3;
                    }
                    return v2;

                }
                return v1;
            }
            return v4;
        }
    };

    public static final Comparator<StructureBlock> PERFORMANCE = new Comparator<StructureBlock>() {
        @Override
        public int compare(StructureBlock o1, StructureBlock o2) {
            int squarSize = 16;
            Integer v3 = o2.getPriority().compareTo(o1.getPriority());
            if (v3 == 0) {
                Integer v1 = getPiece(o1.getPosition().getBlockX(), squarSize).compareTo(getPiece(o2.getPosition().getBlockX(), squarSize));
                if (v1 == 0) {
                    Integer v2 = getPiece(o1.getPosition().getBlockZ(), squarSize).compareTo(getPiece(o2.getPosition().getBlockZ(), squarSize));
                    if (v2 == 0) {
                        return new Integer(o1.getPosition().getBlockY()).compareTo(o2.getPosition().getBlockY());
                    }
                    return v2;

                }
                return v1;
            }
            return v3;
        }
    };

    private final Comparator<StructureBlock> comp;

    StructureBlockComparators(Comparator<StructureBlock> comparator) {
        this.comp = comparator;
    }

    private static Integer getPiece(int whole, int squarSize) {
        if (whole % squarSize == 0) {
            return whole / squarSize;
        } else {
            whole -= (whole % squarSize);
            return whole / squarSize;
        }

    }

    public static Comparator<StructureBlock> getMode(int mode) {
        switch (mode) {
            case 0:
                return StructureBlockComparators.PERFORMANCE;
            case 1:
                return StructureBlockComparators.COMPROMIS;
            case 2:
                return StructureBlockComparators.FANCY;
            default:
                throw new AssertionError("Unreachable");
        }
    }

}
