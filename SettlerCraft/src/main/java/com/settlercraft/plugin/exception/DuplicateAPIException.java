/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.plugin.exception;

import com.settlercraft.core.SettlerCraftAPI;

/**
 *
 * @author Chingo
 */
public class DuplicateAPIException extends Throwable {

    public DuplicateAPIException(SettlerCraftAPI api) {
        super("already have an API called: " + api.getName());
    }
    
    
    
}
