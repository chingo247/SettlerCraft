/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.persistence;


import com.settlercraft.model.structure.Structure;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureManager extends AbstractService {
    
    public void save(Structure o) {
        Session session = getSession();
        session.save(o);
        session.close();
    }
    
    public void delete(Structure o) {
        Session session = getSession();
        session.delete(o);
        session.close();
    }
    

    
}
