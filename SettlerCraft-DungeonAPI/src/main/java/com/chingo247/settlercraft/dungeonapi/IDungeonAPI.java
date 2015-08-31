/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.dungeonapi;

import com.chingo247.settlercraft.core.Direction;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public interface IDungeonAPI {
    
    public IDungeon createDungeon(World world, Vector position, int width, int length, Direction direction, DungeonOptions options);
    
}
