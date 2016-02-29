/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.util;

import java.util.Arrays;

/**
 *
 * @author Chingo
 */
public class VersionUtil {
    
    private VersionUtil() {}
    
    /**
     * Compares two versions of the same format. Expected format is: #.#.#
     * This method accepts any number of #. (e.g. 2.3.3.3.3.3.3 or 2.4.5.6). However, both versions need to be in the same format
     * 
     * This method will return:
     * -1 if versionA &lt versionB, 1 if versionA &gt versionB, 0 if versions are equal
     * @param versionA
     * @param versionB
     * @return comp result
     */
    public static int compare(String versionA, String versionB) {
        String[] versionsA = versionA.split("\\.");
        String[] versionsB = versionB.split("\\.");
        
        if(versionsA.length != versionsB.length) {
            throw new IllegalArgumentException("Versions should have the same format!");
        }
        
        for(int i = 0; i < versionsA.length; i++) {
            int a = Integer.parseInt(versionsA[i]);
            int b = Integer.parseInt(versionsB[i]);
            
            if(a == b) {
                continue;
            }
            
            if(a > b) {
                return 1;
            } else {
                return -1;
            }
            
        }
        return 0; 
    }
    
    
}
