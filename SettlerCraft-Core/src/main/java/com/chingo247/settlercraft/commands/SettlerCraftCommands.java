///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.settlercraft.commands;
//
//import com.chingo247.settlercraft.SettlerCraft;
//import com.chingo247.settlercraft.exception.CommandException;
//import com.chingo247.settlercraft.menu.CategoryMenu;
//import com.chingo247.xcore.core.IPlayer;
//import com.sk89q.minecraft.util.commands.Command;
//import com.sk89q.minecraft.util.commands.CommandContext;
//import com.sk89q.minecraft.util.commands.CommandPermissions;
//import com.sk89q.worldedit.entity.Player;
//
///**
// *
// * @author Chingo
// */
//public class SettlerCraftCommands {
//    
//    private final SettlerCraft settlerCraft;
//    
//
//    public SettlerCraftCommands(SettlerCraft settlerCraft) {
//        this.settlerCraft = settlerCraft;
//    }
//    
//    @Command(aliases = {"menu"}, desc = "Opens the planmenu")
//    @CommandPermissions("sc.open.menu")
//    public void openMenu(Player player, CommandContext args) throws CommandException {
//        if (!settlerCraft.getConfigProvider().isPlanMenuEnabled()) {
//            throw new CommandException("Planmenu is disabled");
//        }
//
//        
//        CategoryMenu planmenu = settlerCraft.createPlanMenu();
//        if (planmenu == null) {
//            throw new CommandException("Planmenu is initialized yet, please wait...");
//        }
//
//        if (!planmenu.isEnabled()) {
//            throw new CommandException("Planmenu is not ready yet, please wait");
//        }
//        IPlayer ply = settlerCraft.getPlatform().getPlayer(player.getUniqueId());
//        
//        planmenu.openMenu(ply);
//    }
//    
//    
//    
//}
