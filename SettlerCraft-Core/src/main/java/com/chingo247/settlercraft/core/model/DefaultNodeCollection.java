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
package com.chingo247.settlercraft.core.model;

import com.chingo247.settlercraft.core.model.interfaces.IdentifiableNode;
import com.chingo247.settlercraft.core.model.interfaces.IPredicate;
import com.chingo247.settlercraft.core.persistence.neo4j.NodeHelper;
import com.google.common.base.Objects;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

/**
 *
 * @author Chingo
 */
public class DefaultNodeCollection<K,T extends IdentifiableNode<K>> implements NodeCollection<K, T>{
    
    private final Node underlyingNode;
    private final RelationshipType relationshipType;
    private final Direction direction;
    private final Class<T> type;

    public DefaultNodeCollection(Node underlyingNode, RelationshipType relationshipType, Direction direction) {
        this.underlyingNode = underlyingNode;
        this.relationshipType = relationshipType;
        this.direction = direction;
        
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        this.type = (Class) pt.getActualTypeArguments()[0];
    }
    

    private Class<T> getType() {
        return type;
    }


    @Override
    public boolean has(T t) {
        return get(t.getUniqueIndentifier()) != null;
    }

    @Override
    public Iterable<T> iterate() {
        return NodeHelper.makeIterable(underlyingNode, underlyingNode.getRelationships(relationshipType, direction), type);
    }

    @Override
    public T get(K k) {
        T result = null;
        if(k != null) {
            for(Iterator<T> it = iterate().iterator(); it.hasNext();) {
                T next = it.next();
                if(k.equals(next.getUniqueIndentifier())) {
                    result = next;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean add(T t) {
        boolean added = false;
        if(!has(t)) {
            underlyingNode.createRelationshipTo(t.getNode(), relationshipType);
            added = true;
        }
        return added;
    }

    @Override
    public boolean remove(final T t) {
        if(t == null || t.getUniqueIndentifier()== null) {
            return false;
        }
        return NodeHelper.remove(underlyingNode, underlyingNode.getRelationships(relationshipType, direction), type, new IPredicate<T>() {

            @Override
            public boolean evaluate(T other) {
                return Objects.equal(other.getUniqueIndentifier(), t.getUniqueIndentifier());
            }
        });
        
    }
    
    
    
    
    
    
}
