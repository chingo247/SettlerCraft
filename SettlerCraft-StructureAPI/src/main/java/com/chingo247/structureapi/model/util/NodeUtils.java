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
package com.chingo247.structureapi.model.util;

import com.chingo247.structureapi.model.interfaces.IFactory;
import java.util.Iterator;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class NodeUtils {
    
    public static Double getDouble(Node node, String property, Double defaultValue) {
        Object o = node.getProperty(property);
        if(o != null) {
            return (double) o;
        }
        return defaultValue;
    }
    
    public static long getLong(Node node, String property, Long defaultValue) {
        Object o = node.getProperty(property);
        if(o != null) {
            return (long) o;
        }
        return defaultValue;
    }
    
    public static int getInt(Node node, String property, Integer defaultValue) {
        Object o = node.getProperty(property);
        if(o != null) {
            return (int) o;
        }
        return defaultValue;
    }
    
    public static String getString(Node node, String property, String defaultValue) {
        Object o = node.getProperty(property);
        if(o != null) {
            return (String) o;
        }
        return defaultValue;
    }
    
    public static Boolean getBoolean(Node node, String property, Boolean defaultValue) {
        Object o = node.getProperty(property);
        if(o != null) {
            return (boolean) o;
        }
        return defaultValue;
    }
    
    public static <T> Iterable<T> makeIterable(final Iterable<Node> nodes, final IFactory<T> factory) {
        final Iterator<Node> nodeIt = nodes.iterator();
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    @Override
                    public boolean hasNext() {
                        return nodeIt.hasNext();
                    }

                    @Override
                    public T next() {
                        return factory.make(nodeIt.next());
                    }
                };
            }
        };
    }
    
}
