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
package com.chingo247.settlercraft.core.persistence.hibernate.dao;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Chingo
 * @param <T>
 * @param <K>
 */
public interface AbstractDAO<T,K extends Serializable> {
    
    public T find(K id);
    public T save(T t);
    public void delete(T t);
    public void insert(T t);
    public void bulkInsert(List<T> ts);
    public List<T> bulkUpsert(List<T> ts);
    
}
