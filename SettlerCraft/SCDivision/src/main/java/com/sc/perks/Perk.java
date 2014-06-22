/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks;

import java.util.Objects;

/**
 *
 * @author Chingo
 */
public class Perk {
    
    private int increase = 1; // Increases when level / increase % increase == 0
    private int requiredLevel;
    private final String name;
    private Perk requirement;

    public Perk(String name) {
        this.name = name;
        this.requirement = null;
    }

    public void setRequirement(Perk requirement) {
        this.requirement = requirement;
    }

    public Perk getRequirement() {
        return requirement;
    }

    public String getName() {
        return name;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Perk other = (Perk) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
