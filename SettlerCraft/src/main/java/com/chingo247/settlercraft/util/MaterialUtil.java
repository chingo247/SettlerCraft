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


package com.chingo247.settlercraft.util;

import org.bukkit.Material;
import org.bukkit.material.Attachable;
import org.bukkit.material.Directional;

/**
 *
 * @author Chingo
 */
public class MaterialUtil {
    
    public static boolean isDirectional(Material material, byte b) {
        return (material.getNewData(b) instanceof Directional);
    }
    
    public static boolean isAttachable(Material material, byte b) {
        return (material.getNewData(b) instanceof Attachable);
    }
    
}
