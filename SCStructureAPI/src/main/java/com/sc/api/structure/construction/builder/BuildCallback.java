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

package com.sc.api.structure.construction.builder;

import com.sc.api.structure.model.structure.Structure;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public interface BuildCallback {

    /**
     * Code to be executed on succes U can update the inventory here or/and fire
     * an event
     *
     * @param structure The structure
     * @param deposit The itemStack removed from the inventory
     */
    public void onSucces(Structure structure, ItemStack deposit);

    /**
     * Structure was in a state where building is not possible, use
     * structure.getStatus() and define what to do for that state
     *
     * @param structure The structure
     */
    public void onNotInBuildState(Structure structure);

    /**
     * Inventory doesnt have any materials the Structure requires at the moment
     *
     * @param structure The structure
     */
    public void onResourcesNotRequired(Structure structure);
}
