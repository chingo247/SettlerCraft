/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.plugin.exception;

import com.settlercraft.core.SettlerCraftModule;

/**
 * Thrown when there already was an API with the same name / unique identifier
 * @author Chingo
 */
public class DuplicateAPIException extends Throwable {

    public DuplicateAPIException(SettlerCraftModule api) {
        super("already have an API called: " + api.getName());
    }
    
    
    
}
