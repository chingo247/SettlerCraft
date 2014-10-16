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

import com.chingo247.settlercraft.structure.entities.structure.Structure;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class PlanDataAPI {
    
    public static List<StructureDocument> getStructureDocuments(Plugin plugin) {
        List<StructureDocument> docs = new LinkedList<>();
        for(StructureDocument d : PlanDocumentManager.getInstance().getStructureDocuments().values()) {
            if(d.hasContent(plugin)) {
                docs.add(d);
            }
        }
        return docs;
    }
    
    public static List<PlanDocument> getPlanDocuments(Plugin plugin) {
        List<PlanDocument> docs = new LinkedList<>();
        for(PlanDocument d : PlanDocumentManager.getInstance().getPlanDocuments().values()) {
            if(d.hasContent(plugin)) {
                docs.add(d);
            }
        }
        return docs;
    }
    
    public static StructureDocument getStructureDocument(Structure structure) {
        return getStructureDocument(structure.getId());
    }
    
    public static StructureDocument getStructureDocument(long id) {
        return PlanDocumentManager.getInstance().getStructureDocuments().get(id);
    }
    
    
    
}
