/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.exception;

/**
 *
 * @author Chingo
 */
public final class InvalidStructurePlanException extends Throwable {

    public InvalidStructurePlanException(String message) {
        super(message);
    }

    public InvalidStructurePlanException(Throwable cause) {
        super(cause);
    }
    
    public InvalidStructurePlanException(String message, Throwable cause) {
        super(message, cause);
    }
}
