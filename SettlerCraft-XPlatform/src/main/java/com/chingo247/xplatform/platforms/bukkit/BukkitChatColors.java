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
package com.chingo247.xplatform.platforms.bukkit;

import com.chingo247.xplatform.core.IColors;
import org.bukkit.ChatColor;

/**
 *
 * @author Chingo
 */
public class BukkitChatColors implements IColors {
    
    private BukkitChatColors(){}
    
    private static BukkitChatColors instance;
    
    public static BukkitChatColors instance() {
        if(instance == null) {
            instance = new BukkitChatColors();
        }
        return instance;
    }

    @Override
    public String blue() {
        return ChatColor.BLUE.toString();
    }

    @Override
    public String red() {
       return ChatColor.RED.toString();
    }

    @Override
    public String gold() {
        return ChatColor.GOLD.toString();
    }

    @Override
    public String purple() {
        return ChatColor.DARK_PURPLE.toString();
    }

    @Override
    public String green() {
        return ChatColor.GREEN.toString();
    }

    @Override
    public String yellow() {
        return ChatColor.YELLOW.toString();
    }
    
    @Override
    public String reset() {
        return ChatColor.RESET.toString();
    }

    @Override
    public String stripColor(String s) {
        return ChatColor.stripColor(s);
    }

    @Override
    public String white() {
        return ChatColor.WHITE.toString();
    }
    
}
