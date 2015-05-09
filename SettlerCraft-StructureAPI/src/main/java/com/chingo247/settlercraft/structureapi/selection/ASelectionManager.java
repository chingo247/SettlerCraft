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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class ASelectionManager implements ISelectionManager {
    
    private Map<UUID, Selection> selections = Collections.synchronizedMap(new HashMap<UUID, Selection>());

    @Override
    public boolean hasSelection(Player player) {
        return selections.get(player.getUniqueId()) != null;
    }

    @Override
    public boolean matchesCurrentSelection(Player player, Vector start, Vector end) {
        Selection selection = selections.get(player.getUniqueId());
        if(selection == null) {
            return false;
        }
        Selection newSelection = new Selection(player.getUniqueId(), start, end);
        
        return selection.equals(newSelection);
    }
    
    protected Selection getSelection(UUID player) {
        if(player == null) return null;
        return selections.get(player);
    }
    
    protected void putSelection(Selection selection) {
        if(selection == null) return;
        selections.put(selection.getPlayer(), selection);
    }
    
    protected void removeSelection(Selection selection) {
        if(selection == null) return;
        selections.remove(selection.getPlayer());
    }
    
}
