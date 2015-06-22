/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.model.interfaces;

import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerNode;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IStructureOwnerRepository {
    
    public StructureOwnerNode findByUUID(UUID uuid);
    
    public StructureOwnerNode findById(Long id);
    
}
