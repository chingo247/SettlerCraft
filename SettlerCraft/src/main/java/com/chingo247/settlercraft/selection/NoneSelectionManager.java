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
package com.chingo247.settlercraft.selection;

import com.chingo247.settlercraft.model.persistence.entities.world.CuboidDimension;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;

/**
 * A Non-selection manager. Which will print the current selection to the player
 * @author Chingo
 */
public class NoneSelectionManager extends ASelectionManager {
    
    private static NoneSelectionManager instance;
    
    private NoneSelectionManager() {}
    
    public static NoneSelectionManager getInstance() {
        if(instance == null) {
            instance = new NoneSelectionManager();
        }
        return instance;
    }

    @Override
    public void select(Player player, Vector start, Vector end) {
        CuboidDimension dimension = new CuboidDimension(start, end);
        player.print("You've selected area: " + dimension.getMinPosition() + ", " + dimension.getMaxPosition());
        putSelection(new Selection(player.getUniqueId(), start, end));
    }

    @Override
    public void deselect(Player player) {
        removeSelection(getSelection(player.getUniqueId()));
    }

    
    
}
