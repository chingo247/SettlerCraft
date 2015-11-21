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

import com.chingo247.settlercraft.core.model.IPredicate;
import com.google.common.base.Preconditions;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Chingo
 */
public class NodeHelper {

    public static Double getDouble(Node node, String property, Double defaultValue) {
        Object o = node.getProperty(property);
        if (o != null) {
            return (double) o;
        }
        return defaultValue;
    }

    public static long getLong(Node node, String property, Long defaultValue) {
        Object o = node.getProperty(property);
        if (o != null) {
            return (long) o;
        }
        return defaultValue;
    }

    public static int getInt(Node node, String property, Integer defaultValue) {
        Object o = node.getProperty(property);
        if (o != null) {
            return (int) o;
        }
        return defaultValue;
    }

    public static String getString(Node node, String property, String defaultValue) {
        Object o = node.getProperty(property);
        if (o != null) {
            return (String) o;
        }
        return defaultValue;
    }

    public static Boolean getBoolean(Node node, String property, Boolean defaultValue) {
        Object o = node.getProperty(property);
        if (o != null) {
            return (boolean) o;
        }
        return defaultValue;
    }

    public static <T> T getSingle(Node node, RelationshipType type, Direction direction, Class<T> clazz) {

        Relationship r = node.getSingleRelationship(type, direction);
        if (r != null) {
            Node n = r.getOtherNode(node);
            try {
                return clazz.getConstructor(Node.class).newInstance(n);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }
    
    public static <T> Iterable<T> makeIterable(final Iterator<Node> nodeIt, final Class<T> clazz) {
        try {
            final Constructor c = clazz.getConstructor(Node.class);
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
                            try {
                                return (T) c.newInstance(nodeIt.next());
                            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                    };
                }
            };
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> Iterable<T> makeIterable(final Iterable<Node> nodes, final Class<T> clazz) {
        final Iterator<Node> nodeIt = nodes.iterator();
        return makeIterable(nodeIt, clazz);
    }

    public static <T> Iterable<T> makeIterable(final Node node, final Iterable<Relationship> nodes, final Class<T> clazz, IPredicate<T> predicate) {
        final Iterator<Relationship> nodeIt = nodes.iterator();
        try {
            final Constructor c = clazz.getConstructor(Node.class);
            Iterable resultIterable = new Iterable<T>() {

                @Override
                public Iterator<T> iterator() {
                    return new Iterator<T>() {

                        @Override
                        public boolean hasNext() {
                            return nodeIt.hasNext();
                        }

                        @Override
                        public T next() {
                            try {
                                return (T) c.newInstance(node);
                            } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                Logger.getLogger(NodeHelper.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            return null;
                        }

                    };
                }
            };
            if (predicate != null) {
                Iterator<T> resultIterator = resultIterable.iterator();
                while (resultIterator.hasNext()) {
                    T next = resultIterator.next();
                    if (!predicate.evaluate(next)) {
                        resultIterator.remove();
                    }
                }
            }
            return resultIterable;

        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }

    }

    public static <T> Iterable<T> makeIterable(final Node thisNode, final Iterable<Relationship> nodes, final Class<T> clazz) {
        return makeIterable(thisNode, nodes, clazz, null);
    }

    public static <T> boolean remove(final Node node, final Iterable<Relationship> nodes, final Class<T> clazz, IPredicate<T> predicate) {
        Preconditions.checkNotNull(predicate, "Predicate may not be null");
        try {
            Constructor<T> c = clazz.getConstructor(Node.class);
            for (Iterator<Relationship> iterator = nodes.iterator(); iterator.hasNext();) {
                Relationship next = iterator.next();

                Node otherNode = next.getOtherNode(node);
                T other = c.newInstance(otherNode);
                if (predicate.evaluate(other)) {
                    next.delete();
                    return true;
                }
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(NodeHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    

}
