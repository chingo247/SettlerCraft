/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.platforms.services;

import com.sk89q.worldedit.entity.Player;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IPlayerProvider {
    
    public Player getPlayer(UUID playerId);
    
}