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

import com.chingo247.xplatform.core.ICommandSender;
import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;

/**
 * @author Chingo
 */
public class BukkitConsoleSender implements ICommandSender {
    
    private final CommandSender commandSender;

    public BukkitConsoleSender(CommandSender sender) {
        Preconditions.checkNotNull(sender, "sender may not be null!");
        this.commandSender = sender;
    }

    @Override
    public void sendMessage(String... message) {
        commandSender.sendMessage(message);
    }

    public boolean hasPermission(String permission) {
        return commandSender.hasPermission(permission);
    }
    
}
