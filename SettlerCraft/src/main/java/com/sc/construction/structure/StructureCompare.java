/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.construction.structure;

import java.util.Comparator;

/**
 *
 * @author Chingo
 */
public class StructureCompare {
    
    public static final Comparator<StructureBlock> FANCY = new Comparator<StructureBlock>() {
        
        @Override
        public int compare(StructureBlock o1, StructureBlock o2) {
            final int CHUNK_SIZE = 16;
            int squarSize = CHUNK_SIZE * 2;
            Integer v1 = getPiece(o1.getPosition().getBlockY(), squarSize / 2).compareTo(getPiece(o2.getPosition().getBlockY(), squarSize / 2)); // Vertical cost less
            if (v1 == 0) {
                Integer v2 = getPiece(o1.getPosition().getBlockZ(), squarSize).compareTo(getPiece(o2.getPosition().getBlockZ(), squarSize));
                if (v2 == 0) {

                    Integer v3 = getPiece(o1.getPosition().getBlockX(), squarSize).compareTo(getPiece(o2.getPosition().getBlockX(), squarSize));
                    if (v3 == 0) {
                        Integer v4 = o2.getPriority().compareTo(o1.getPriority());
                        if (v4 == 0) {
                            // For the looks
                            return new Integer(o1.getPosition().getBlockY()).compareTo(o2.getPosition().getBlockY()); 
                        }
                        return v4;
                    }
                    return v3;
                }
                return v2;

            }
            return v1;
        }
    };
    
    public static final Comparator<StructureBlock> COMPROMIS =new Comparator<StructureBlock>() {
        @Override
        public int compare(StructureBlock o1, StructureBlock o2) {
            final int CHUNK_SIZE = 16;
            int squarSize = CHUNK_SIZE * 2;
            Integer v1 = getPiece(o1.getPosition().getBlockY(), squarSize).compareTo(getPiece(o2.getPosition().getBlockY(), squarSize)); // Vertical cost less
            if (v1 == 0) {
                Integer v2 = getPiece(o1.getPosition().getBlockZ(), squarSize).compareTo(getPiece(o2.getPosition().getBlockZ(), squarSize));
                if (v2 == 0) {
                    Integer v3 = getPiece(o1.getPosition().getBlockX(), squarSize).compareTo(getPiece(o2.getPosition().getBlockX(), squarSize));
                    if (v3 == 0) {
                        Integer v4 = o2.getPriority().compareTo(o1.getPriority());
                        if (v4 == 0) {
                            // For the looks
                            return new Integer(o1.getPosition().getBlockY()).compareTo(o2.getPosition().getBlockY());
                        }
                        return v4;
                    }
                    return v3;
                }
                return v2;

            }
            return v1;
        }
    };
    
    public static final Comparator<StructureBlock> PERFORMANCE = new Comparator<StructureBlock>() {
        @Override
        public int compare(StructureBlock o1, StructureBlock o2) {
            int squarSize = 16;
            Integer v1 = getPiece(o1.getPosition().getBlockX(), squarSize).compareTo(getPiece(o2.getPosition().getBlockX(), squarSize));
            if (v1 == 0) {
                Integer v2 = getPiece(o1.getPosition().getBlockZ(), squarSize).compareTo(getPiece(o2.getPosition().getBlockZ(), squarSize));
                if (v2 == 0) {
                    Integer v3 = o2.getPriority().compareTo(o1.getPriority());
                        if (v3 == 0) {
                            return new Integer(o1.getPosition().getBlockY()).compareTo(o2.getPosition().getBlockY());
                        }
                        return v3;
                }
                return v2;

            }
            return v1;
        }
    };

    private final Comparator<StructureBlock> comp;

    StructureCompare(Comparator<StructureBlock> comparator) {
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

}
