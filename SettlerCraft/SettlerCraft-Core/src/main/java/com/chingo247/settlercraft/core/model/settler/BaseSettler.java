/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.model.settler;

import com.chingo247.settlercraft.core.model.IdentifiableNode;
import java.util.UUID;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class BaseSettler implements IBaseSettler {
    
    private Long id;
    private UUID uuid;
    private String name;
    private Node underlyingNode;
    
    
    public BaseSettler(Node node) {
        this(new BaseSettlerNode(node));
    }
    
    public BaseSettler(BaseSettlerNode settlerNode) {
        this.underlyingNode = settlerNode.getNode();
        this.id = settlerNode.getId();
        this.uuid = settlerNode.getUniqueId();
        this.name = settlerNode.getName();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Node getNode() {
        return underlyingNode;
    }
    
}
