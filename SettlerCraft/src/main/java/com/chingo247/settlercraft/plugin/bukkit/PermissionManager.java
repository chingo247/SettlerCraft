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
package com.chingo247.settlercraft.plugin.bukkit;

import com.chingo247.settlercraft.commons.core.IPermission;
import com.chingo247.settlercraft.SettlerCraftContext;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IPlayer;
import com.chingo247.xcore.platforms.PlatformFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Chingo
 */
public class PermissionManager implements IPermissionManager {

    private static final String prefix = "settlercraft.";
    private final SettlerCraftContext context = SettlerCraftContext.getContext();
    private APlatform platform = PlatformFactory.createPlatform(context.getPlatform());
    
    private static PermissionManager instance;
    
    public static IPermissionManager getInstance() {
        if(instance == null) {
            instance = new PermissionManager();
        }
        return instance;
    }

    @Override
    public boolean isAllowed(IPlayer player, IPermission permission) {
        if (player == null) {
            return false;
        }
        if (player.isOP()) {
            return true;
        }

        
        
        return player.hasPermission(permission.getName());
    }
    

    public enum Perms {

        OPEN_PLAN_MENU(new Permission(prefix + "user.menu.planmenu", "open the plan menu to select plans for free", PermissionDefault.TRUE)),
        OPEN_PLAN_SHOP(new Permission(prefix + "user.menu.planshop", "open the plan shop to buy plans", PermissionDefault.TRUE)),
        STRUCTURE_PLACE(new Permission(prefix + "user.structure.place", "placing structures", PermissionDefault.TRUE));

        final Permission PERM;
        
        Perms(Permission perm) {
            this.PERM = perm;
            Bukkit.getPluginManager().addPermission(perm);
        }
        
        public Permission getPermission() {
            return PERM;
        }
    }

    public boolean isAllowed(Player player, Perms perms) {
        
    }
    


}
