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
package com.chingo247.settlercraft.util;

import com.chingo247.settlercraft.StructureStorage;
import com.chingo247.settlercraft.structure.Structure;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public abstract class PartitionStorage<K, V> {

    private int factor;
    private int partitionSize;
    private int minPartitionSize;
    private final Map<Integer, PartitionStorage<K, V>> subpartitions;
    private final Map<K, V> storage;

    public PartitionStorage(int partitionSize, int minPartitionSize, int factor) {
        Preconditions.checkArgument(partitionSize > minPartitionSize);
        Preconditions.checkArgument(factor < partitionSize);

        this.minPartitionSize = minPartitionSize;
        this.partitionSize = partitionSize;
        if (partitionSize > minPartitionSize) {
            subpartitions = new HashMap<>();
            storage = null;
        } else {
            storage = new HashMap<>();
            subpartitions = null;
        }
    }

    public void store(K k, V v) {
        int pX = getPartitionValue(computeX(v));
        if (pX == minPartitionSize) {
            synchronized (storage) {
                storage.put(k, v);
            }
        } else {
            PartitionStorage ps = null;
            synchronized (subpartitions) {
                ps = subpartitions.get(pX);
                if (ps == null) {
                    subpartitions.put(pX, new PartitionStorage<K, V>(partitionSize / factor, minPartitionSize, factor) {

                        @Override
                        protected int computeX(V t) {
                            return PartitionStorage.this.computeX(t);
                        }
                    });
                }
            }
        }
    }

    private int getPartitionValue(int value) {
        int v = value % (partitionSize / factor);
        value -= v;
        value /= (partitionSize / factor);
        return value;
    }

    public K find(K key) {

    }

    protected abstract int computeX(V t);

}
