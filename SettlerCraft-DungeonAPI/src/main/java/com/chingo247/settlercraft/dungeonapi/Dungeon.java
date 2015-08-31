/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.dungeonapi;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.structure.plan.IStructurePlan;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class Dungeon extends Structure implements IDungeon {

    public Dungeon(Node dungeonNode) {
        super(dungeonNode);
    }

   
    
}
