/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.entity.division;

import com.settlercraft.core.model.entity.division.guild.Guild;
import com.settlercraft.core.model.entity.settler.SettlementLeader;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;

/**
 *
 * @author Chingo
 */
public class Settlement extends Division{

    protected final Location location;
    protected final Set<Guild> guilds;
    
    public Settlement(String name, SettlementLeader leader, Location location) {
        super(name, leader);
        this.location = location;
        this.guilds = new HashSet<>();
    }

    public Location getLocation() {
        return location;
    }
    
    
    
    
}
