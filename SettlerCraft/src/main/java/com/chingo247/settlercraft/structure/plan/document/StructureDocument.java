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
package com.chingo247.settlercraft.structure.plan.document;

import com.chingo247.settlercraft.structure.Structure;
import java.io.File;
import org.dom4j.DocumentException;

/**
 *
 * @author Chingo
 */
public class StructureDocument extends AbstractDocument<StructureDocumentPluginElement> {
    
    private final Structure structure;
    private final StructureDocumentManager documentManager;

    public StructureDocument(StructureDocumentManager documentManager, Structure structure, File docFile) throws DocumentException {
        super(docFile);
        this.structure = structure;
        this.documentManager = documentManager;
    }

    public Structure getStructure() {
        return structure;
    }

    @Override
    public void save() {
        documentManager.save(this);
    }

    @Override
    protected void save(StructureDocumentPluginElement element) {
        documentManager.save(element);
    }
    
    
    
    
    
    
}
