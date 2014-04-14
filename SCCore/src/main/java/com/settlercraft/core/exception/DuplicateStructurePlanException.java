/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.exception;

/**
 * Thrown when the name of the structure plan isn't unique
 *
 * @author Chingo
 */
public class DuplicateStructurePlanException extends Throwable {

    public DuplicateStructurePlanException(String message) {
        super(message);
    }

    public DuplicateStructurePlanException(Throwable cause) {
        super(cause);
    }

    public DuplicateStructurePlanException(String message, Throwable cause) {
        super(message, cause);
    }

}
