/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.model.settler;

import java.util.UUID;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class Settler  {
    
    private Long id;
    private UUID uuid;
    private String name;
    private Node underlyingNode;
    
    
    public Settler(Node node) {
        this(new SettlerNode(node));
    }
    
    public Settler(SettlerNode settlerNode) {
        this.underlyingNode = settlerNode.getNode();
        this.id = settlerNode.getId();
        this.uuid = settlerNode.getUniqueId();
        this.name = settlerNode.getName();
    }

    public Long getId() {
        return id;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Node getNode() {
        return underlyingNode;
    }
    
}
