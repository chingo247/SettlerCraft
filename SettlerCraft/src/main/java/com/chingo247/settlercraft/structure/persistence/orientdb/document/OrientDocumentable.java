/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.persistence.orientdb.document;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *
 * @author Chingo
 */
public interface OrientDocumentable {
    
    ODocument asDocument();
    
}
