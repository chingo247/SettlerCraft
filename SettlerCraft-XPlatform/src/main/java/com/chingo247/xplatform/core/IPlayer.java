/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
