/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.core.persistence.neo4j;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintType;
import org.neo4j.graphdb.schema.IndexDefinition;

/**
 *
 * @author Chingo
 */
public class Neo4jHelper {

    public static boolean hasUniqueConstraint(GraphDatabaseService graph, Label label, String property) {
        Iterable<ConstraintDefinition> iterable = graph.schema().getConstraints(label);
        for (ConstraintDefinition i : iterable) {
            if (i.getConstraintType() == ConstraintType.UNIQUENESS) {
                for (String p : i.getPropertyKeys()) {
                    if (p.equals(property)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean hasIndex(GraphDatabaseService graph, Label label, String property) {
        Iterable<IndexDefinition> iterable = graph.schema().getIndexes(label);
        for (IndexDefinition i : iterable) {
            for (String p : i.getPropertyKeys()) {
                if (p.equals(property)) {
                    return true;
                }
            }
        }
        return false;
    }

}
