/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.xplatform.core;

import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IPlayer extends ICommandSender {
    
    /**
     * Gets the name of the player
     * @return The name of the player
     */
    public String getName();
    
    /**
     * The player unique id
     * @return The player id
     */
    public UUID getUniqueId();
    
    /**
     * The current world the player is in
     * @return The world the player is in
     */
    public IWorld getWorld();
    
    /**
     * Sends a message to the player
     * @param message The message to send
     */
    @Override
    public void sendMessage(String message);
    
    /**
     * Sends a message to the player
     * @param message The message to send
     */
    @Override
    public void sendMessage(String... message);
    
    /**
     * Gets the yaw of the player
     * @return The yaw of the player
     */
    public float getYaw();
    
    /**
     * Gets the current level of the player
     * @return The current level of the player
     */
    public int getLevel();
    
    /**
     * Gets the amount of experience of the player
     * @return The experience of the player
     */
    public int getExperience();
    
    /**
     * Checks if the player is OP
     * @return True if player is OP
     */
    public boolean isOP();

    /**
     * Checks if the player has the permission
     * @param permission The permission
     * @return true if the player has the permission
     */
    public boolean hasPermission(String permission);
    /**
     * Updates the player's inventory
     */
    public void updateInventory();
    
    /**
     * Gets the inventory of the player
     * @return The inventory of the player
     */
    public AInventory getInventory();
    
    /**
     * Checks if the player is sneaking
     * @return True if the player is sneaking
     */
    public boolean isSneaking();
    
    /**
     * Closes the inventory, if it was open...
     */
    public void closeInventory();

    /**
     * Opens the inventory
     * @param inventory The inventory
     */
    public  void  openInventory(AInventory inventory);
    
    public ILocation getLocation();
    
    
}
