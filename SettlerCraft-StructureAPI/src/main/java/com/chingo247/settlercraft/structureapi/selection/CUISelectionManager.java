
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.chingo247.settlercraft.structureapi.selection;

import com.chingo247.settlercraft.structureapi.util.WorldEditUtil;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class CUISelectionManager extends ASelectionManager {

    private final Logger log = Logger.getLogger(CUISelectionManager.class);
    private static CUISelectionManager instance;
    
    private CUISelectionManager() {}
    
    public static CUISelectionManager getInstance() {
        if(instance == null) {
            instance = new CUISelectionManager();
        }
        return instance;
    }

    @Override
    public void select(Player player, Vector start, Vector end) {
        LocalSession session = WorldEdit.getInstance().getSession(player);
        
        Selection selection = new Selection(player.getUniqueId(), start, end);
        World world = WorldEditUtil.getWorld(player.getWorld().getName());
        putSelection(selection);
        
        session.getRegionSelector(world).selectPrimary(start, null);
        session.getRegionSelector(world).selectSecondary(end, null);
        session.dispatchCUISelection(player);
    }

    @Override
    public void deselect(Player player) {
        LocalSession session = WorldEdit.getInstance().getSession(player);
        World world = WorldEditUtil.getWorld(player.getWorld().getName());
        if (session.getRegionSelector(world).isDefined()) {
            session.getRegionSelector(world).clear();
            session.dispatchCUISelection(player);
        }
        removeSelection(getSelection(player.getUniqueId()));
    }

    
}