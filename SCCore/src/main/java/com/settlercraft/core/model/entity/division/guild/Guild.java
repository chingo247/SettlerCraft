/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.entity.division.guild;

import com.settlercraft.core.model.entity.division.Division;
import com.settlercraft.core.model.entity.living.GuildMaster;


/**
 *
 * @author Chingo
 */
public class Guild extends Division {


    public Guild(String title, String name, GuildMaster guildMaster) {
        super(name, guildMaster);
    }
    
    
}
