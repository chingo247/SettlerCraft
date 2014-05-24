///*
// * Copyright (C) 2014 Chingo
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
//
//package com.sc.api.structure.construction.blocks;
//
//import com.sk89q.jnbt.ByteArrayTag;
//import com.sk89q.jnbt.ByteTag;
//import com.sk89q.jnbt.CompoundTag;
//import com.sk89q.jnbt.DoubleTag;
//import com.sk89q.jnbt.FloatTag;
//import com.sk89q.jnbt.IntArrayTag;
//import com.sk89q.jnbt.IntTag;
//import com.sk89q.jnbt.ListTag;
//import com.sk89q.jnbt.LongTag;
//import com.sk89q.jnbt.ShortTag;
//import com.sk89q.jnbt.StringTag;
//import com.sk89q.jnbt.Tag;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// *
// * @author Chingo
// */
//public class BlockWriter  {
//    
//    private final String tagName;
//    private final Map<String, Tag> tags;
//
//    
//    public BlockWriter(final String compoundTagName) {
//        this.tagName = compoundTagName;
////        this.t = new CompoundTag(compoundTagName, new HashMap<String, Tag>());
//        this.tags = new HashMap<>();
//    }
//    
//    public void put(String key, int value) {
//        this.tags.put(key, new IntTag(key, value));
//        
//    }
//    
//    public void put(String key, String value) {
//        this.tags.put(key, new StringTag(key, value));
//        
//    }
//    
//    public void put(String key, byte[] value) {
//        this.tags.put(key, new ByteArrayTag(key, value));
//        
//    }
//    
//    public void put(String key, byte value) {
//        this.tags.put(key, new ByteTag(key, value));
//        
//    }
//    
//    public void put(String key, double value) {
//        this.tags.put(key, new DoubleTag(key, value));
//        
//    }
//    
//    public void put(String key, float value) {
//        this.tags.put(key, new FloatTag(key, value));
//        
//    }
//    
//    public void put(String key, int[] value) {
//        this.tags.put(key, new IntArrayTag(key, value));
//        
//    }
//    
//    public void put(String key, Class<? extends Tag> type, List<? extends Tag> value) {
//        this.tags.put(key, new ListTag(key, type, value));
//        
//    }
//    
//    public void put(String key, long value) {
//        this.tags.put(key, new LongTag(key, value));
//        
//    }
//    
//    public void put(String key, short value) {
//        this.tags.put(key, new ShortTag(key, value));
//        
//    }
//    
//    public CompoundTag getCompoundTag() {
//        return new CompoundTag(tagName, tags);
//    }
//    
//    public void remove(String key) {
//        this.tags.remove(key);
//    }
//
//    
//
//    
//    
//}
