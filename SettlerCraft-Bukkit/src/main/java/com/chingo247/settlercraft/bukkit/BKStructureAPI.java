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
package com.chingo247.settlercraft.bukkit;

import com.chingo247.settlercraft.bukkit.util.BKWorldEditUtil;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.xcore.platforms.PlatformFactory;
import com.sk89q.worldedit.entity.Player;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class BKStructureAPI extends StructureAPI {
    
    private SettlerCraftPlugin settlerCraft;
    
    BKStructureAPI(SettlerCraftPlugin settlerCraftPlugin, ExecutorService service) {
        super(PlatformFactory.getPlatform("bukkit"), service);
    }

    @Override
    protected File getWorkingDirectory() {
        return settlerCraft.getDataFolder();
    }

    @Override
    protected Player getPlayer(UUID player) {
        return BKWorldEditUtil.wrapPlayer(Bukkit.getPlayer(player));
    }
    
}
