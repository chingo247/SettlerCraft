/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.structureapi.main.construction;

import org.primesoft.asyncworldedit.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;

/**
 *
 * @author Chingo
 */
public abstract class SCIBlockPlacerListener implements IBlockPlacerListener {

    @Override
    public void jobAdded(JobEntry je) {
        if (je instanceof SCJobEntry) {
            SCJobEntry jobEntry = (SCJobEntry) je;
            jobAdded(jobEntry);
        }
    }

    @Override
    public void jobRemoved(JobEntry je) {
        if (je instanceof SCJobEntry) {
            SCJobEntry jobEntry = (SCJobEntry) je;
            jobRemoved(jobEntry);
        }
    }
    
    public abstract void jobAdded(SCJobEntry jobEntry);
    
    public abstract void jobRemoved(SCJobEntry jobEntry);

}
