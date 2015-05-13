/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.persistence.dao;

import com.chingo247.settlercraft.core.persistence.util.IdGenerator;
import com.chingo247.settlercraft.core.persistence.util.IdGeneratorFactory;

/**
 *
 * @author Chingo
 */
class StructureIdGenerator {
    
    public static synchronized Long nextId() {
        IdGenerator idGenerator = IdGeneratorFactory.getInstance().getIdGeneratorForName("Structure");
        return idGenerator.nextId();
    }
    
}
