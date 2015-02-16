/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.commons.core;

import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IPlayer {
    
    public String getName();
    
    public UUID getUniqueId();
    
    public IWorld getWorld();
    
    public void sendMessage(String message);
    
    public void sendMessage(String... message);
    
    public float getYaw();
    
    public int getLevel();
    
    public int getExperience();
    
}
