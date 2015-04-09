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
package com.chingo247.settlercraft.persistence.graph.relations;

import com.chingo247.settlercraft.persistence.graph.documents.SettlerDocument;
import com.chingo247.settlercraft.persistence.graph.documents.StructureDocument;

/**
 *
 * @author Chingo
 */
public class StructureOwnerRelation {
    
    private SettlerDocument fromSettler;
    private StructureDocument toStructure;
    private OwnerType type;

    public StructureOwnerRelation(SettlerDocument fromSettler, StructureDocument toStructure, OwnerType type) {
        this.fromSettler = fromSettler;
        this.toStructure = toStructure;
        this.type = type;
    }

    public SettlerDocument getFromSettler() {
        return fromSettler;
    }

    public StructureDocument getToStructure() {
        return toStructure;
    }

    public OwnerType getType() {
        return type;
    }

    public void setFromSettler(SettlerDocument fromSettler) {
        this.fromSettler = fromSettler;
    }

    public void setToStructure(StructureDocument toStructure) {
        this.toStructure = toStructure;
    }

    public void setType(OwnerType type) {
        this.type = type;
    }
    
}
