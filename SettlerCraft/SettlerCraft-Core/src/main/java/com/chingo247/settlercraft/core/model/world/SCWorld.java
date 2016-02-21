/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.model.world;

import java.util.UUID;
import org.neo4j.graphdb.Node;

/**
 * Has all of the attributes of {@link SCWorldNode}, but none of the operations in this class require
 * transactions (except for the constructor...)
 * @author Chingo
 */
public class SCWorld implements ISCWorld {
    
    private String worldName;
    private UUID worldUUID;
    private Node worldNode;
    
    public SCWorld(Node worldNode) {
        this(new SCWorldNode(worldNode));
    }
    
    public SCWorld(SCWorldNode worldNode) {
        this.worldName = worldNode.getName();
        this.worldUUID = worldNode.getUUID();
        this.worldNode = worldNode.getNode();
    }

    @Override
    public Node getNode() {
        return worldNode;
    }

    @Override
    public UUID getUUID() {
        return worldUUID;
    }

    @Override
    public String getName() {
        return worldName;
    }
    
}
