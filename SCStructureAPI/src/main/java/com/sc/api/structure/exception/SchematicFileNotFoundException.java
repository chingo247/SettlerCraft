/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.exception;

import java.io.File;

/**
 *
 * @author Christian
 */
public class SchematicFileNotFoundException extends Throwable {

    public SchematicFileNotFoundException(File filePath) {
        super("no such file: " + filePath.getAbsolutePath());
    }
    
    
}