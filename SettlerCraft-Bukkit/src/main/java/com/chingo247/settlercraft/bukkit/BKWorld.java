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
package com.chingo247.settlercraft.bukkit;

import com.chingo247.settlercraft.SettlerCraftStructure;
import com.chingo247.settlercraft.SettlerCraftWorld;
import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.model.entities.structure.StructureEntity;
import com.chingo247.xcore.core.IWorld;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Chingo
 */
public final class BKWorld extends SettlerCraftWorld {
    
    BKWorld(ExecutorService service, SettlerCraft settlerCraft, IWorld world) {
        super(service,settlerCraft,world);
    }

    @Override
    protected SettlerCraftStructure handleStructure(StructureEntity entity) {
        return new BKStructure(executor, entity, this, getStructurePlan(entity));
    }

    
}
