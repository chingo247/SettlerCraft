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
package com.chingo247.settlercraft.structure.persistence.hibernate;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 * @param <T>
 */
public class AbstractDAOImpl<T> implements AbstractDAO<T> {


    public AbstractDAOImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        this.type = (Class) pt.getActualTypeArguments()[0];
    }

    private final Class<T> type;

    private Class<T> getType() {
        return type;
    }

    @Override
    public T find(long id) {
        Session session = HibernateUtil.getSession();
        T t =  (T) session.get(getType(), id);
        session.close();
        return t;
    }

    public T save(T t) {
        Session session = null;
        Transaction tx = null;

        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            t = (T) session.merge(t);
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                Logger.getLogger(AbstractDAO.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return t;
    }

    public void delete(T t) {
        Session session = HibernateUtil.getSession();
        session.delete(t);
        session.close();
    }

}
