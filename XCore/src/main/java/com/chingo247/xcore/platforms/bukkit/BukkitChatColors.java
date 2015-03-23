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
package com.chingo247.xcore.platforms.bukkit;

import com.chingo247.xcore.core.IColor;
import com.chingo247.xcore.util.ChatColors;

/**
 *
 * @author Chingo
 */
public class BukkitChatColors implements IColor {

    @Override
    public String getBlue() {
        return ChatColors.BLUE;
    }

    @Override
    public String getRed() {
       return ChatColors.RED;
    }

    @Override
    public String getGold() {
        return ChatColors.GOLD;
    }

    @Override
    public String getPurple() {
        return ChatColors.DARK_PURPLE;
    }

    @Override
    public String getGreen() {
        return ChatColors.GREEN;
    }
    
}
