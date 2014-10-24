/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.entities.container;

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Chingo
 */
@Entity
public class InboxChest implements Serializable {
    
    @Embedded
    private Chest chest;
    
    private String chestProfile;
    
    @Id
    @GeneratedValue
    private Long id;

    /**
     * JPA Constructor.
     */
    protected InboxChest() {}
    
    public InboxChest(Chest chest, String chestProfile) {
        this.chest = chest;
        this.chestProfile = chestProfile;
    }

    public Long getId() {
        return id;
    }

    public Chest getChest() {
        return chest;
    }
    
    

    
    
    
}
