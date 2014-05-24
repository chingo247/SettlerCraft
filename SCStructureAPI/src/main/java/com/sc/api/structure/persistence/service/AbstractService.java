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
package com.sc.api.structure.persistence.service;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import javax.persistence.EntityManager;

/**
 * @author Chingo
 */
public abstract class AbstractService {

    protected EntityManager entityManager;

    protected JPAQuery from(EntityPath<?>... paths) {
        return new JPAQuery(entityManager).from(paths);
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
