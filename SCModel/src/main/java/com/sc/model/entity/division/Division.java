/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.entity.division;

import com.settlercraft.model.entity.living.Labourer;
import com.settlercraft.model.entity.living.Representative;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Chingo
 */
public abstract class Division {
    
    protected String name;
    protected final Set<Labourer> labourers;
    protected Representative representative;

    protected Division(String name, Representative representative) {
        this.labourers = new HashSet<>();
        this.name = name;
        this.representative = representative;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }
    
    
}
