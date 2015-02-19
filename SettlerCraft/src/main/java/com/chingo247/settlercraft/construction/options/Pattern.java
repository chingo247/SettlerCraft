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
package com.chingo247.settlercraft.construction.options;

import com.chingo247.settlercraft.construction.worldedit.StructureBlock;
import java.util.Comparator;

/**
 * A Pattern mode specifies the pattern in which the Structure will be constructed. A Pattern makes
 * it possible for the BlockPlacer to travel trough the schematic in smaller cubes, creating a
 * realistic effect. The thought behind this is that lots of block-changes on a smaller area
 * (square) will have increased performance.
 *
 * @deprecated Not used anymore as clipboard perform cubic iteration on the fly, which increased performance even more...
 * @author Chingo
 */
@Deprecated
public class Pattern {

    private final Comparator<StructureBlock> buildPattern;

    private Pattern(Comparator<StructureBlock> buildPattern) {
        this.buildPattern = buildPattern;
    }

    public Comparator<StructureBlock> getComparator() {
        return buildPattern;
    }

    /**
     * Creates a Pattern, which defines in which pattern the blocks are placed or removed
     *
     * @param square Defines the width x length of each chunk of blocks
     * @param height Defines the height of each layer
     * @return The Pattern
     */
    public static Pattern createBuildPattern(final int square, final int height) {
        return new Pattern(new Comparator<StructureBlock>() {

            @Override
            public int compare(StructureBlock o1, StructureBlock o2) {
                Integer v4 = o2.getPriority().compareTo(o1.getPriority());

                if (v4 == 0) {
                    Integer v1 = getPiece(o1.getPosition().getBlockY(), height).compareTo(getPiece(o2.getPosition().getBlockY(), height)); // Vertical cost less
                    if (v1 == 0) {
                        Integer v2 = getPiece(o1.getPosition().getBlockZ(), square).compareTo(getPiece(o2.getPosition().getBlockZ(), square));
                        if (v2 == 0) {
                            Integer v3 = getPiece(o1.getPosition().getBlockX(), square).compareTo(getPiece(o2.getPosition().getBlockX(), square));
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
        });
    }

    /**
     * Creates a Pattern, which defines in which pattern the blocks are placed or removed The layer
     * height is equal to the height of the schematic
     *
     * @param square Defines the width x length of each cube of blocks
     * @return The Pattern
     */
    public static Pattern createPattern(final int square) {
        return new Pattern(new Comparator<StructureBlock>() {

            @Override
            public int compare(StructureBlock o1, StructureBlock o2) {
                Integer v4 = o2.getPriority().compareTo(o1.getPriority());

                if (v4 == 0) {

                    Integer v2 = getPiece(o1.getPosition().getBlockZ(), square).compareTo(getPiece(o2.getPosition().getBlockZ(), square));
                    if (v2 == 0) {
                        Integer v3 = getPiece(o1.getPosition().getBlockX(), square).compareTo(getPiece(o2.getPosition().getBlockX(), square));
                        if (v3 == 0) {
                            return new Integer(o1.getPosition().getBlockY()).compareTo(o2.getPosition().getBlockY());
                        }
                        return v3;
                    }
                    return v2;

                }
                return v4;
            }
        });
    }

    /**
     * Creates a Pattern, which defines in which pattern the blocks are placed or removed. There is
     * no visible layering, blocks are placed/removed in squares of 16 (The ChunkSize)
     *
     * @return The Pattern
     */
    public static Pattern createPerformancePattern() {
        return createPattern(16);
    }

    /**
     * Creates a Pattern, which defines in which pattern the blocks are placed or removed. There is
     * visible layering, blocks are placed/removed in cubes of 32x32x32 (width x height x length)
     * blocks
     *
     * @return The Pattern
     */
    public static Pattern createCompromisePattern() {
        return createBuildPattern(32, 32);
    }

    /**
     * Creates a Pattern, which defines in which pattern the blocks are placed or removed. There is
     * visible layering, blocks are placed/removed in cubes of 32x16x32 (width x height x length)
     * blocks. Just because this method is called fancy doesn't mean it has decreased performance.
     * There is just more visible layering
     *
     * @return The Pattern
     */
    public static Pattern createFancyPattern() {
        return createBuildPattern(16, 32);
    }

    private static Integer getPiece(int whole, int squarSize) {
        if (whole % squarSize == 0) {
            return whole / squarSize;
        } else {
            whole -= (whole % squarSize);
            return whole / squarSize;
        }

    }

    /**
     * Get pattern for enum Mode, used to get the pattern from the SettlerCraft config.
     *
     * @param mode The mode
     * @return The Pattern, which defines the buildmode
     */
    public static Pattern getPattern(Mode mode) {

        switch (mode) {
            case PERFORMANCE:
                return createPerformancePattern();
            case COMPROMISE:
                return createCompromisePattern();
            case FANCY:
                return createFancyPattern();
            default:
                throw new AssertionError("Unreachable");
        }
    }

    public enum Mode {

        PERFORMANCE,
        COMPROMISE,
        FANCY;

        public static Mode getMode(int mode) {
            switch (mode) {
                case 0:
                    return PERFORMANCE;
                case 1:
                    return COMPROMISE;
                case 2:
                    return FANCY;
                default:
                    throw new AssertionError("Unkown mode: " + mode);
            }
        }

    }

}
