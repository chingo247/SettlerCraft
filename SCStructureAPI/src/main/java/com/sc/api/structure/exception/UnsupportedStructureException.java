/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.exception;

/**
 * Thrown when a structure was read with a material that is not supported by this plugin.
 * @author Chingo
 */
public final class UnsupportedStructureException extends Throwable {

    public UnsupportedStructureException(String message) {
        super(message);
    }

    public UnsupportedStructureException(Throwable cause) {
        super(cause);
    }
    
    public UnsupportedStructureException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
    
    
}
