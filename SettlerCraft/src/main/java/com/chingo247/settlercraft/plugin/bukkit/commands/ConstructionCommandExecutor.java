package com.chingo247.settlercraft.plugin.bukkit.commands;

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
//
//package com.chingo247.settlercraft.bukkit.commands;
//
//
//import com.chingo247.settlercraft.structureapi.persistence.hibernate.StructureDAO;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.chingo247.settlercraft.structureapi.construction.options.BuildOptions;
//import com.chingo247.settlercraft.structureapi.construction.options.DemolitionOptions;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.entity.Player;
//
///**
// *
// * @author Chingo
// */
//public class ConstructionCommandExecutor implements CommandExecutor {
//
//    private static final String CMD = "/cst";
//    private final ChatColor CCC = ChatColor.DARK_PURPLE;
////    private final BukkitStructureAPI structureAPI;
//    private final StructureDAO structureDAO = new StructureDAO();
//
//    public ConstructionCommandExecutor(BukkitStructureAPI structureAPI) {
//        this.structureAPI = structureAPI;
//    }
//    
//    
//
//
//    @Override
//    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
//        if (!(cs instanceof Player)) {
//            cs.sendMessage("You are not a player!"); // Command is issued from server console
//            return true;
//        }
//
//        if (args.length == 0) {
//            cs.sendMessage(ChatColor.RED + "Too few arguments");
//            cs.sendMessage(new String[]{
//                CCC + CMD + " cancel [id]",
//                CCC + CMD + " build [id]",
//                CCC + CMD + " demolish [id]"});
//            return true;
//        }
//        
//        
//        String arg = args[0];
//        Player player = (Player) cs;
//        switch (arg) {
//            case "cancel":
//                return cancelTask(player, args);
//            case "build":
//                return build(player, args);
//            case "demolish":
//                return demolish(player, args);
//            case "rollback":
//                return rollback(player, args);
//            default:
//                player.sendMessage(ChatColor.RED + "No actions known for: " + arg);
//                cs.sendMessage(new String[]{
//                    CCC + CMD + " cancel [id]",
//                    CCC + CMD + " build [id]",
//                    CCC + CMD + " demolish [id]"
//                });
//                return true;
//        }
//
//    }
//
//    private boolean cancelTask(Player player, String[] args) {
//        if (args.length > 2) {
//            player.sendMessage(ChatColor.RED + "Too many arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " cancel [id]"
//            });
//            return true;
//        } else if (args.length < 2) {
//            player.sendMessage(ChatColor.RED + "Too few arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " cancel [id]"
//            });
//            return true;
//        }
//        Long id;
//        try {
//            id = Long.parseLong(args[1]);
//        } catch (NumberFormatException nfe) {
//            player.sendMessage(ChatColor.RED + "No valid id");
//            return true;
//        }
//
//        
//        Structure structure = structureDAO.find(id);
//
//        if (structure == null) {
//            player.sendMessage(ChatColor.RED + "Unable to find structure #" + ChatColor.GOLD + id);
//            return true;
//        }
//
//        if (structureAPI.stop(player, structure)) {
//            player.sendMessage("#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName()  + ChatColor.RESET + " has been canceled");
//        } else {
//            player.sendMessage(ChatColor.RED + "Failed to cancel #" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName());
//        }
//
//        return true;
//    }
//
//    private boolean build(Player player, String[] args) {
//        if (args.length > 2) {
//            player.sendMessage(ChatColor.RED + "Too many arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " build [id]"
//            });
//            return true;
//        } else if (args.length < 2) {
//            player.sendMessage(ChatColor.RED + "Too few arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " build [id]"
//            });
//            return true;
//        }
//        Long id;
//        try {
//            id = Long.parseLong(args[1]);
//        } catch (NumberFormatException nfe) {
//            player.sendMessage(ChatColor.RED + "No valid id");
//            return true;
//        }
//
//        Structure structure = structureDAO.find(id);
//
//        if (structure == null) {
//            player.sendMessage(ChatColor.RED + "Unable to find structure # " + ChatColor.GOLD + id);
//            return true;
//        }
//        if (structureAPI.build(player, structure, new BuildOptions(false), false)) {
//            player.sendMessage(ChatColor.RESET + "#" + ChatColor.GOLD + id + " " + ChatColor.BLUE + structure.getName() + ChatColor.RESET + " will be build");
//        }
//
//        return true;
//    }
//
//    private boolean demolish(Player player, String[] args) {
//        if (args.length > 2) {
//            player.sendMessage(ChatColor.RED + "Too many arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " demolish [id]"
//            });
//            return true;
//        } else if (args.length < 2) {
//            player.sendMessage(ChatColor.RED + "Too few arguments");
//            player.sendMessage(new String[]{
//                "Usage: ",
//                CCC + CMD + " demolish [id]"
//            });
//            return true;
//        }
//        Long id;
//        try {
//            id = Long.parseLong(args[1]);
//        } catch (NumberFormatException nfe) {
//            player.sendMessage(ChatColor.RED + "No valid id");
//            return true;
//        }
//        
//        Structure structure = structureDAO.find(id);
//        
//        if (structure == null) {
//            player.sendMessage(ChatColor.RED + "Unable to find structure #" + id);
//            return true;
//        }
//        if (structureAPI.demolish(player, structure, new DemolitionOptions(), false)) {
//            player.sendMessage(ChatColor.RESET + "#" + ChatColor.GOLD + id + ChatColor.BLUE + structure.getName() + " will be demolished");
//        }
//        return true;
//    }
//
////    private boolean rollback(Player player, String[] args) {
////       if (args.length > 2) {
////            player.sendMessage(ChatColor.RED + "Too many arguments");
////            player.sendMessage(new String[]{
////                "Usage: ",
////                CCC + CMD + " rollback [id]"
////            });
////            return true;
////        } else if (args.length < 2) {
////            player.sendMessage(ChatColor.RED + "Too few arguments");
////            player.sendMessage(new String[]{
////                "Usage: ",
////                CCC + CMD + " rollback [id]"
////            });
////            return true;
////        }
////        Long id;
////        try {
////            id = Long.parseLong(args[1]);
////        } catch (NumberFormatException nfe) {
////            player.sendMessage(ChatColor.RED + "No valid id");
////            return true;
////        }
////
////        StructureService ss = new StructureService();
////        Structure structure = ss.getStructure(id);
////
////        if (structure == null) {
////            player.sendMessage(ChatColor.RED + "Unable to find structure # " + ChatColor.GOLD + id);
////            return true;
////        }
////        structureAPI.rollback(structure);
////        
////
////        return true;
////    }
//
//    private boolean rollback(Player player, String[] args) {
//        long structureID;
//        try {
//            structureID = Long.parseLong(args[1]);
//        } catch (NumberFormatException e) {
//            player.sendMessage("Invalid id...");
//            return true;
//        }
//        
//        Structure structure = structureDAO.find(structureID);
//        
//        structureAPI.rollback(player, structure, structure.getLog().getCreatedAt());
//        
//        return true;
//        
//        
//    }
//    
//}
