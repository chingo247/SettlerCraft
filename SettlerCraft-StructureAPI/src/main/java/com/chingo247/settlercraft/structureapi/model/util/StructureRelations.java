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
package com.chingo247.settlercraft.structureapi.model.util;

/**
 *
 * @author Chingo
 */
public class StructureRelations {
    
    /**
     * Should point to it's container
     */
    public static final String RELATION_WITHIN = "Within";
    /**
     * Should point to it's parent
     */
    public static final String RELATION_SUBSTRUCTURE = "SubstructureOf";
    /**
     * Should point to it's owner
     */
    public static final String RELATION_OWNED_BY = "OwnedBy";
}
