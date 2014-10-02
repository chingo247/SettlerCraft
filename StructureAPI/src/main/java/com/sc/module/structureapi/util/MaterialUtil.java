/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.util;

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
