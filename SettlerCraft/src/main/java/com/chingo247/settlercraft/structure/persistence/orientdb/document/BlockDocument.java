/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure.persistence.orientdb.document;

import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Date;

/**
 *
 * @author Chingo
 */
public class BlockDocument implements OrientDocumentable{
    
    private final int x;
    private final int y;
    private final int z;
    private final int oldMaterial;
    private final int newMaterial;
    private final byte oldData;
    private final byte newData;
    private final long date;
    private final String world;

    public BlockDocument(String world, int x, int y, int z, int oldMaterial, byte oldData, int newMaterial, byte newData) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldMaterial = oldMaterial;
        this.newMaterial = newMaterial;
        this.oldData = oldData;
        this.newData = newData;
        this.world = world;
        this.date = new Date().getTime();
    }

    @Override
    public ODocument asDocument() {
        ODocument doc = new ODocument("Block");
        doc.field("x", x);
        doc.field("y", y);
        doc.field("z", z);
        doc.field("oldMaterial", oldMaterial);
        doc.field("oldData", oldData);
        doc.field("newMaterial", newMaterial);
        doc.field("newData", newData);
        doc.field("world", world);
        doc.field("date", date);
        return doc;
    }
    
    
    
}
