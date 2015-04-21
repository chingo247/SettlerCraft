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
package com.chingo247.settlercraft.core.persistence.util;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.annotations.Index;

/**
 *
 * @author Chingo
 */
@Entity
public class IdGenerator implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    
    @Index(name = "generatorName")
    @Column(unique = true, nullable = false)
    private String name;

    private int idNumber;
    
    /**
     * JPA Constructor
     */
    protected IdGenerator() {}
    
    IdGenerator(String name) {
        this.name = name;
        this.idNumber = 0;
    }

    int incrementAndGet() {
        idNumber++;
        return idNumber;
    }
    
    
    
    
}
