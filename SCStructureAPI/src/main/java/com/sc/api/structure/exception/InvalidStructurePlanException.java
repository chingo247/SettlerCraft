/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.exception;

import java.io.File;

/**
 * Thrown when the config file of a plan has invalid or missing nodes.
 * @author Chingo
 */
public final class InvalidStructurePlanException extends Throwable {

    public InvalidStructurePlanException(String description) {
        super(description);
    }

    
    
}