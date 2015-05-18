package com.chingo247.settlercraft.structureapi.util;

///*
// * Copyright (C) 2014 Chingo247
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.chingo247.settlercraft.util;
//
//import com.sk89q.worldedit.blocks.BaseBlock;
//
///**
// * TODO Read values from a file
// *
// * @author Chingo
// */
//public class SettlerCraftMaterials {
//
////    private final static Set<Material> UNCRAFTABLE = new HashSet<>(Arrays.asList(
////            Material.AIR,
////            Material.LONG_GRASS,
////            Material.COMMAND,
////            Material.BEDROCK
////    ));
////
////    /**
////     * SettlerCraftMaterials recognized as wood
////     */
////    private final static HashMap<Material, Float> WOOD = Maps.newHashMap();
////
////    static {
////        WOOD.put(Material.WOOD, 1.0f);
////        WOOD.put(Material.LOG, 4.0f);
////        WOOD.put(Material.WOOD_DOUBLE_STEP, 1.0f);
////        WOOD.put(Material.WORKBENCH, 4.0f);
////        WOOD.put(Material.CHEST, 8.0f);
////        WOOD.put(Material.WOODEN_DOOR, 6.0f);
////        WOOD.put(Material.STICK, 0.5f);
////        WOOD.put(Material.FENCE, 3.0f);
////        WOOD.put(Material.FENCE_GATE, 4.0f);
////        WOOD.put(Material.LADDER, 3.0f);
////        WOOD.put(Material.SIGN, 2.5f);
////        WOOD.put(Material.SIGN_POST, 2.5f);
////        WOOD.put(Material.WOOD_STAIRS, 1.5f);
////        WOOD.put(Material.BIRCH_WOOD_STAIRS, 1.5f);
////        WOOD.put(Material.SPRUCE_WOOD_STAIRS, 1.5f);
////        WOOD.put(Material.WOOD_STEP, 0.5f);
////    }
////
////    /**
////     * SettlerCraftMaterials recognized as Cobblestone
////     */
////    private final static HashMap<Material, Float> COBBLESTONE = Maps.newHashMap();
////
////    static {
////        COBBLESTONE.put(Material.COBBLESTONE, 1.0f);
////        COBBLESTONE.put(Material.COBBLESTONE_STAIRS, Float.NaN);
////        COBBLESTONE.put(Material.COBBLE_WALL, Float.NaN);
////        COBBLESTONE.put(Material.STEP, 0.5f);                   // DATA == 3
////    }
////
////    /**
////     * SettlerCraftMaterials recognized as Stone Brick AKA Smooth Brick
////     */
////    private final static HashMap<Material, Float> STONE_BRICK = Maps.newHashMap();
////
////    static {
////        STONE_BRICK.put(Material.SMOOTH_BRICK, 1.0f);
////        STONE_BRICK.put(Material.SMOOTH_STAIRS, 1.5f);
////        STONE_BRICK.put(Material.STEP, 0.5f); // DATA == 5
////    }
////
////    /**
////     * SettlerCraftMaterials recognized as Brick
////     */
////    private final static HashMap<Material, Float> BRICK = Maps.newHashMap();
////
////    static {
////        BRICK.put(Material.STEP, 0.5f);
////        BRICK.put(Material.BRICK, 1.0f);
////        BRICK.put(Material.BRICK_STAIRS, 1.5f); // DATA == 4
////    }
////
////    private final static HashMap<Material, Float> NETHER_BRICK = Maps.newHashMap();
////
////    static {
////        NETHER_BRICK.put(Material.STEP, 0.5f); // DATA == 6
////        NETHER_BRICK.put(Material.NETHER_BRICK, 1.0f);
////        NETHER_BRICK.put(Material.NETHER_BRICK_STAIRS, 1.5f);
////        NETHER_BRICK.put(Material.NETHER_FENCE, 3.0f);
////    }
////
////    private final static HashMap<Material, Float> DIRT = Maps.newHashMap();
////
////    static {
////        DIRT.put(Material.DIRT, 1.0f);
////        DIRT.put(Material.GRASS, 1.0f);
////    }
////
////    // QUARTZ
////    private final static HashMap<Material, Float> QUARTZ = Maps.newHashMap();
////
////    static {
////        QUARTZ.put(Material.QUARTZ_BLOCK, 1.0f);
////        QUARTZ.put(Material.QUARTZ_STAIRS, 1.5f);
////        QUARTZ.put(Material.STEP, 0.5f); // DATA == 7
////    }
////
////    // SANDSTONE
////    private final static HashMap<Material, Float> SANDSTONE = Maps.newHashMap();
////
////    static {
////        SANDSTONE.put(Material.SANDSTONE, 1.0f);
////        SANDSTONE.put(Material.SANDSTONE_STAIRS, 1.5f);
////        SANDSTONE.put(Material.STEP, 0.5f); // DATA == 1
////    }
////
////    public static boolean isUncraftable(Material material) {
////        return UNCRAFTABLE.contains(material);
////    }
////
////    public static boolean isBrick(BaseBlock block) {
////        if (Material.getMaterial(block.getId()) == Material.STEP) {
////            return block.getData() == 4;
////        }
////        return BRICK.containsKey(Material.getMaterial(block.getId()));
////    }
////
////    /**
////     * Checks if the block is recognized as brick
////     *
////     * @param block The block
////     * @return true if block is recognized as brick
////     * @deprecated makes use of deprecated methods block.getData and block.getId
////     */
////    public static boolean isBrick(Block block) {
////        return isBrick(new BaseBlock(block.getTypeId(), block.getData()));
////    }
////
////    /**
////     * Checks if block is recognized as Nether Brick
////     *
////     * @param block The block
////     * @return true if block is recognized as brick
////     */
////    public static boolean isNetherBrick(Resource block) {
////        if (block.getMaterial() == Material.STEP) {
////            return block.getData() == 6;
////        }
////        return NETHER_BRICK.containsKey(block.getMaterial());
////    }
////
////    /**
////     * Checks if block is recognized as Nether Brick
////     *
////     * @param block The block
////     * @return true if block is recognized as brick
////     * @deprecated makes use of deprecated methods block.getData and block.getId
////     */
////    public static boolean isNetherBrick(Block block) {
////        return isNetherBrick(new Resource(block.getType().getId(), block.getData()));
////    }
////
////    /**
////     * Checks if block is recognized as dirt
////     *
////     * @param block The block
////     * @return true if block is recognized as dirt
////     */
////    public static boolean isDirt(Resource block) {
////        return DIRT.containsKey(block.getMaterial());
////    }
////
////    /**
////     * Checks if material is recognized as dirt
////     *
////     * @param material The material
////     * @return true if block is recognized as dirt
////     */
////    public static boolean isDirt(Material material) {
////        return DIRT.containsKey(material);
////    }
////
////    /**
////     * Checks if block is recognized as quartz
////     *
////     * @param block The block
////     * @return true if block is recognized as quartz
////     */
////    public static boolean isQuartz(Resource block) {
////        if (block.getMaterial() == Material.STEP) {
////            return block.getData() == 7;
////        }
////        return QUARTZ.containsKey(block.getMaterial());
////    }
////
////    /**
////     * Checks if block is recognized as quartz
////     *
////     * @param block The block
////     * @return true if block is recognized as quartz
////     * @deprecated makes use of deprecated methods block.getData and block.getId
////     */
////    public static boolean isQuartz(Block block) {
////        return isQuartz(new Resource(block.getType().getId(), block.getData()));
////    }
////
////    /**
////     * Checks if block is recognized as sandstone
////     *
////     * @param block The block
////     * @return true if block is recognized as quartz
////     */
////    public static boolean isSandStone(Resource block) {
////        if (block.getMaterial() == Material.STEP) {
////            return block.getData() == 1;
////        }
////        return SANDSTONE.containsKey(block.getMaterial());
////    }
////
////    /**
////     * Checks if block is recognized as sandstone
////     *
////     * @param block The block
////     * @return true if block is recognized as quartz
////     * @deprecated Makes use of deprecated methods getId and getData
////     */
////    public static boolean isSandStone(Block block) {
////        return isSandStone(new Resource(block.getType().getId(), block.getData()));
////    }
////
////    /**
////     * Returns wheter the material is of type wood or not
////     *
////     * @param material The material
////     * @return true if material is recognized as wood
////     */
////    public static boolean isWood(Material material) {
////        return WOOD.containsKey(material);
////    }
////
////    public static boolean isWood(BaseBlock block) {
////        return isWood(Material.getMaterial(block.getId()));
////    }
////
////    public static boolean isWood(Block block) {
////        return isWood(block.getType());
////    }
////
////    /**
////     * Checks if a block is of type cobblestone.
////     *
////     * @param block The block
////     * @return if the material is a cobblestone type
////     */
////    public static boolean isCobbleStone(BaseBlock block) {
////        if (Material.getMaterial(block.getId()) == Material.STEP) {
////            return block.getData() == 3;
////        }
////        return COBBLESTONE.containsKey(Material.getMaterial(block.getId()));
////    }
////
////
////    /**
////     * Checks if a block is of type stone brick (Material.SMOOTH_BRICK).
////     *
////     * @param block The block
////     * @return if the material is a stone brick type
////     */
////    public static boolean isStoneBrick(BaseBlock block) {
////        if (Material.getMaterial(block.getId()) == Material.STEP) {
////            return block.getData() == 5;
////        }
////        return STONE_BRICK.containsKey(Material.getMaterial(block.getId()));
////    }
////
////    /**
////     * Checks if a block is of type stone brick (Material.SMOOTH_BRICK).
////     *
////     * @param block The block
////     * @return if the material is a stone brick type
////     * @deprecated Makes use of deprecated methods getId and getData
////     */
////    public static boolean isStoneBrick(Block block) {
////        return isStoneBrick(new BaseBlock(block.getTypeId(), block.getData()));
////    }
////
//    /**
//     * Returns the wood value for given material
//     *
//     * @param block The block
//     * @return the value of the block in its base resource, returns 1.0 by default if not supported
//     */
//    public static float getValue(BaseBlock block) {
////        Material mat = Material.getMaterial(block.getId());
////        if (isWood(mat)) {
////            return WOOD.get(mat);
////        } else if (isCobbleStone(block)) {
////            return COBBLESTONE.get(mat);
////        } else if (isStoneBrick(block)) {
////            return STONE_BRICK.get(mat);
////        } else if (isBrick(block)) {
////            return BRICK.get(mat);
////        } else if (isQuartz(block)) {
////            return QUARTZ.get(mat);
////        } else if (isSandStone(block)) {
////            return SANDSTONE.get(mat);
////        } else if (isStoneBrick(block)) {
////            return COBBLESTONE.get(mat);
////        } else if (isNetherBrick(block)) {
////            return NETHER_BRICK.get(mat);
////        } else if (isDirt(block)) {
////            return DIRT.get(mat);
////        } else {
//        return 1.0f; // Default value
////        }
//    }
////
////    public static boolean canSimplify(Resource block) {
////        return getSimplifiedMaterial(block) != null;
////    }
////
////    public static Material getSimplifiedMaterial(Resource block) {
////        if (isWood(block)) {
////            return Material.WOOD;
////        } else if (isStoneBrick(block)) {
////            return Material.SMOOTH_BRICK;
////        } else if (isBrick(block)) {
////            return Material.BRICK;
////        } else if (isCobbleStone(block)) {
////            return Material.COBBLESTONE;
////        } else if (isQuartz(block)) {
////            return Material.QUARTZ_BLOCK;
////        } else if (isSandStone(block)) {
////            return Material.SANDSTONE;
////        } else if (isStoneBrick(block)) {
////            return Material.SMOOTH_BRICK;
////        } else if (isNetherBrick(block)) {
////            return Material.NETHER_BRICK;
////        } else if (isDirt(block)) {
////            return Material.DIRT;
////        } else {
////            return null;
////        }
////    }
//
//}