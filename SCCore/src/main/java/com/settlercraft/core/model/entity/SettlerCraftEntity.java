/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.entity;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Chingo
 */
@MappedSuperclass
public abstract class SettlerCraftEntity {
    
    
    
//    @Version
//    private Timestamp lastModified;
    
    private final Timestamp created;
    
    
    
    public SettlerCraftEntity(){
        this.created = new Timestamp(new Date().getTime());
    }

//    public Date getCreated() {
//        return lastModified;
//    }
    
    public abstract Long getId();
    
    
}
