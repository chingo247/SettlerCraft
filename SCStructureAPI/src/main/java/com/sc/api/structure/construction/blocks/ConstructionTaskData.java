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
//import com.sc.api.structure.construction.progress.ConstructionTask;
//import com.sk89q.jnbt.CompoundTag;
//
///**
// *
// * @author Chingo
// */
//public class ConstructionTaskData implements SCBlockData {
//    
//    public static final String STRUCTURE_TAG = "TAG_Compound";
//    private final ConstructionTask task;
//
//    public ConstructionTaskData(ConstructionTask constructionTask) {
//        this.task = constructionTask;
//    }
//
//    @Override
//    public CompoundTag getCompoundTag() {
//        BlockWriter w = new BlockWriter(STRUCTURE_TAG);
//        w.put("structure", task.getId());
//        w.put("name", task.getStructure().getPlan().getDisplayName());
//        w.put("state", task.getState().name());
//        return w.getCompoundTag();
//    }
//
//    
//    
//    
//
//    
//    
//}
