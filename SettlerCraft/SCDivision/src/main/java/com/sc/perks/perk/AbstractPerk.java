/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks.perk;

import java.util.Objects;

/**
 *
 * @author Chingo
 */
public abstract class AbstractPerk {
    
    
    
    public enum Group {
        OWNER,
        MEMBER,
        ALL
    }
    
    private int maxLevel = -1;
    private int requiredLevel = 0;
    private final String name;
    private AbstractPerk requirement;
    private final Group group;
    private final String rootNode;
   

    public AbstractPerk(String roodNode, String name, Group group) {
        this.name = name;
        this.requirement = null;
        this.group = group;
        this.rootNode = roodNode;
    }

    public void setRequirement(AbstractPerk requirement) {
        this.requirement = requirement;
    }

    public AbstractPerk getRequirement() {
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
        final AbstractPerk other = (AbstractPerk) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
    public abstract void levelUp();
    
    
}
