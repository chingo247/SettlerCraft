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
package com.chingo247.settlercraft.util.functions;

/**
 *
 * @author Chingo
 */
public class Maths {
    
    public static float sqrtF(int number, int root) {
        return (float) Math.floor(Math.pow(number, ((double)1/root)));
    }
    
    public static double sqrtD(int number, int root) {
        return (double) sqrt(number, root);
    }
    
    public static int sqrt(int number, int root) {
        return (int) Math.floor(Math.pow(number, ((double)1/root)));
    }
    
}
