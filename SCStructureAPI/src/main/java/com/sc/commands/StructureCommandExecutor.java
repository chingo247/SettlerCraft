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
package com.sc.commands;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.ConstructionProcess;
import com.sc.api.structure.QStructure;
import com.sc.api.structure.Structure;
import com.sc.api.structure.StructureManager;
import com.sc.persistence.HibernateUtil;
import com.sc.persistence.service.StructureService;
import com.sc.plugin.SettlerCraft;
import com.sc.util.SCWorldEditUtil;
import com.sc.util.SCWorldGuardUtil;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.databases.RegionDBUtil;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureCommandExecutor implements CommandExecutor {

    private static final int MAX_LINES = 10;
    private static final String CMD = "/stt";

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (args.length == 0) {
            cs.sendMessage(new String[]{
                ChatColor.LIGHT_PURPLE + CMD + " info " + ChatColor.RESET + " - displays info about the structure you are within",
                ChatColor.LIGHT_PURPLE + CMD + " list [playerName][index]" + ChatColor.RESET + " - displays a list of structures the player owns, no arg for own",
                ChatColor.LIGHT_PURPLE + CMD + " pos [structure id]" + ChatColor.RESET + " - displays your relative position from the structure",
                ChatColor.LIGHT_PURPLE + CMD + " flag [structure id][add/remove][flag][value]" + ChatColor.RESET + " - displays flags, add flags, remove flags",
                ChatColor.LIGHT_PURPLE + CMD + " owner [structure id][add/remove][player]" + ChatColor.RESET + " - displays owner, add owner, remove owner"
            });
            return true;
        }
        String arg = args[0];
        Player player = (Player) cs;
        switch (arg) {
            case "info":
                return displayInfo(player, args);
            case "list":
                return displayStructures(player, args);
            case "pos":
                return getPos(player, args);
            case "flag":
                return flag(player, args);
            case "owner":
                return owner(player,args);

            default:
                player.sendMessage(ChatColor.RED + "No actions known for: " + arg);
                return false;
        }
    }

    private boolean displayStructures(Player player, String[] args) {
        List<Structure> structures;
        if (args.length == 1) {
            structures = listStructures(player);
        } else if (args.length >= 2 && args.length < 4) { // 2 || 3
            String playerName = args[1];
            Player ply = Bukkit.getPlayer(playerName);
            if (ply == null) {
                player.sendMessage(ChatColor.RED + "Unknown player: " + playerName);
                return true;
            } else {
                structures = listStructures(ply);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }
        int amountOfStructures = structures.size();
        if (amountOfStructures == 0) {
            player.sendMessage(ChatColor.RED + "No structures found...");
            return true;
        } else {
            int index;
            Player ply;
            if (args.length == 1) {
                index = 1;
            } else if (args.length == 2) {
                try {
                    index = Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    ply = Bukkit.getPlayer(args[1]);
                    if (ply == null) {
                        player.sendMessage(ChatColor.RED + "Second argument should either be an index or player");
                        return true;
                    }
                    index = 1;
                }
            } else {
                ply = Bukkit.getPlayer(args[1]);
                if (ply == null) {
                    player.sendMessage(ChatColor.RED + "Unknown player: " + args[1]);
                    return true;
                }

                try {
                    index = Integer.parseInt(args[2]);
                } catch (NumberFormatException nfe) {
                    ply = Bukkit.getPlayer(args[1]);
                    if (ply == null) {
                        player.sendMessage(ChatColor.RED + "Third argument must be an index");
                        return true;
                    }
                    index = 1;
                }
            }
            String[] message = new String[MAX_LINES];
            int pages = (amountOfStructures / (MAX_LINES - 1)) + 1;
            if (index > pages) {
                player.sendMessage(ChatColor.RED + "Max page is " + pages);
                return true;
            }

            message[0] = "-----------(Page: " + (index) + "/" + ((amountOfStructures / (MAX_LINES - 1)) + 1) + ", Structures: " + amountOfStructures + ")-----------";
            int line = 1;
            int startIndex = (index - 1) * (MAX_LINES - 1);
            for (int i = startIndex; i < startIndex + (MAX_LINES - 1) && i < structures.size(); i++) {
                Structure structure = structures.get(i);
                String l = "#" + ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET
                        + " " + ChatColor.YELLOW + "X: " + ChatColor.RESET + structure.getLocation().getPosition().getBlockX()
                        + " " + ChatColor.YELLOW + "Y: " + ChatColor.RESET + structure.getLocation().getPosition().getBlockY()
                        + " " + ChatColor.YELLOW + "Z: " + ChatColor.RESET + structure.getLocation().getPosition().getBlockZ()
                        + " " + ChatColor.YELLOW + "World: " + ChatColor.RESET + structure.getLocation().getWorld().getName()
                        + " " + ChatColor.RESET + "Value: " + ChatColor.GOLD + SettlerCraft.valueString(structure.getPlan().getPrice());
                message[line] = l;
                line++;
            }
            player.sendMessage(message);
        }
        return true;
    }

    private List<Structure> listStructures(Player player) {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).where(qs.owner.eq(player.getName()).and(qs.progress().progressStatus.ne(ConstructionProcess.State.REMOVED))).list(qs);
        session.close();
        return structures;
    }

    private boolean displayInfo(Player player, String[] args) {
        StructureService service = new StructureService();
        Structure structure;

        if (args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Invalid id");
                return true;
            }
            structure = service.getStructure(id);
        } else if (args.length == 1) {
            structure = service.getStructure(player.getLocation());
            if (structure == null) {
                player.sendMessage(ChatColor.RED + " Currently not within a structure");
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }

        String valueString = SettlerCraft.valueString(structure.getRefundValue());

        player.sendMessage("#" + ChatColor.GOLD + structure.getId() + " "
                + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.RESET
                + " Owned by: " + ChatColor.GREEN + structure.getOwner() + ChatColor.RESET
                + " Value: " + ChatColor.GOLD + valueString);
        return true;
    }

    private boolean getPos(Player player, String[] args) {
        StructureService service = new StructureService();
        Structure structure;
        if (args.length == 1) {
            structure = service.getStructure(player.getLocation());
            if (structure == null) {
                player.sendMessage(new String[]{
                    ChatColor.RED + "Currently not within a structure",
                    ChatColor.RED + "You may also try " + CMD + " pos [id]"
                });
                return true;
            }
        } else if (args.length == 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException nfe) {
                player.sendMessage("No valid id");
                return true;
            }
            structure = service.getStructure(id);
            if (structure == null) {
                player.sendMessage(ChatColor.RED + "No structure found with id: " + id);
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }
        Vector pos = structure.getRelativePosition(new Location(SCWorldEditUtil.getLocalWorld(player), new BlockVector(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())));

        player.sendMessage("#" + ChatColor.GOLD + structure.getId() + " "
                + ChatColor.BLUE + structure.getPlan().getDisplayName()
                + ChatColor.RESET + ": Your position is "
                + ChatColor.YELLOW + "x:" + ChatColor.RESET + pos.getBlockX() + " "
                + ChatColor.YELLOW + "y:" + ChatColor.RESET + pos.getBlockY() + " "
                + ChatColor.YELLOW + "z:" + ChatColor.RESET + pos.getBlockZ()
        );

        return true;
    }

    private boolean flag(Player player, String[] args) {
        /**
         * /stt flag [id] /stt flag [id] add [flag] /stt flag [id] remove [flag]
         */
        StructureService ss = new StructureService();
        Structure structure;
        if (args.length == 1) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        } else if (args.length >= 2) {
            Long id;
            try {
                id = Long.parseLong(args[1]);
            } catch (NumberFormatException nfe) {
                player.sendMessage(ChatColor.RED + "Invalid structure id");
                return true;
            }
            structure = ss.getStructure(id);
            if (structure == null) {
                player.sendMessage(ChatColor.RED + "Structure with #" + ChatColor.GOLD + id + " " + ChatColor.RED + " not found");
                return true;
            }
            if (args.length == 2) {
                return displayFlags(player, structure);
            } else if (args.length == 5 || args.length == 4) {
                return setFlag(player, structure, args);
            }

        }
        // Error clause
        if (args.length < 5) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments");
        }

        player.sendMessage(new String[]{
            ChatColor.RED + "Usage: ",
            ChatColor.RED + "/stt flag [id]",
            ChatColor.RED + "/stt flag [id] add [flag][value]",
            ChatColor.RED + "/stt flag [id] remove [flag]"
        });
        return true;

    }

    private boolean displayFlags(Player player, Structure structure) {
        World world = structure.getWorld();
        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(world);
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            player.sendMessage(ChatColor.RED + "#" + ChatColor.GOLD + structure.getId()
                    + " " + ChatColor.BLUE + structure.getPlan().getDisplayName()
                    + " " + ChatColor.RED + "doesnt have a region!"
            );
            return true;
        } else {
            Map<Flag<?>, Object> flags = region.getFlags();
            String fs = "";
            if(flags.isEmpty()) {
                fs += ChatColor.RED + "(none)";
            } else {
            int count = 0;
            for (Flag f : flags.keySet()) {
                fs += ChatColor.YELLOW + f.getName() + ": " + ChatColor.RESET + flags.get(f);
                count++;
                if (count < flags.size()) {
                    fs += ", ";
                }
            }
            }

            player.sendMessage(new String[]{
                "Id: " + ChatColor.GOLD + structure.getId() + ChatColor.RESET +  "Plan: " + ChatColor.BLUE + structure.getPlan().getDisplayName(),
                "Flags: " + fs
            });
            return true;
        }
    }

    /**
     * Sets flags, contains snippets of worldguards code
     * @param player The player
     * @param structure The structure
     * @param args The arguments...
     * @return 
     */
    private boolean setFlag(Player player, Structure structure, String[] args) {
        // args.length == 4
        World world = structure.getWorld();
        
        if(world == null) {
            player.sendMessage(ChatColor.RED + "Structure doesnt have a world anymore...");
            return true;
        }
        
        String flagName = args[3];
        if(args.length == 4 && args[2].equals("add")) {
            player.sendMessage(ChatColor.RED + "Too few arguments!");
            return true;
        }
        
        RegionPermissionModel permModel = SCWorldGuardUtil.getRegionPermissionModel(player);
        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(world);
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            player.sendMessage(ChatColor.RED + "#" 
                    + ChatColor.GOLD + structure.getId() + " "
                    + ChatColor.BLUE + structure.getPlan().getDisplayName() + " "
                    + ChatColor.RED + "doesn't have a region!"
            );
            return true;
        }
        
        if(!permModel.maySetFlag(region)) {
            player.sendMessage(ChatColor.RED + "You dont have the permission to do that!");
            return true;
        }
        
        Flag<?> foundFlag = DefaultFlag.fuzzyMatchFlag(flagName);
        
        // We didn't find the flag, so let's print a list of flags that the user
        // can use, and do nothing afterwards
        if (foundFlag == null) {
            StringBuilder list = new StringBuilder();

            // Need to build a list
            for (Flag<?> flag : DefaultFlag.getFlags()) {
                // Can the user set this flag?
                if (!permModel.maySetFlag(region, flag)) {
                    continue;
                }

                if (list.length() > 0) {
                    list.append(", ");
                }
                
                list.append(flag.getName());
            }

            player.sendMessage(ChatColor.RED + "Unknown flag specified: " + flagName);
            player.sendMessage(ChatColor.RED + "Available flags: " + list);
            
            return true;
        }
        
        if (!permModel.maySetFlag(region, foundFlag)) {
            player.sendMessage(ChatColor.RED + "You dont have the permission to set this flag!");
            return true;
        }
        
        
        if(args[2].equals("add")) {
            String flagValue = args[4];
            // Set the flag if [value] was given even if [-g group] was given as well
            try {
                setFlag(region, foundFlag, player, flagValue);
            } catch (InvalidFlagFormat e) {
                player.sendMessage(e.getMessage());
            }

            player.sendMessage(ChatColor.YELLOW
                    + "Region flag " + foundFlag.getName() + " set on #" + ChatColor.GOLD + 
                    structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.YELLOW + "' to '" + flagValue + "'.");
            return true;
        } else if (args[2].equals("remove")) {
            // Clear the flag only if neither [value] nor [-g group] was given
            region.setFlag(foundFlag, null);

            // Also clear the associated group flag if one exists
            RegionGroupFlag groupFlag = foundFlag.getRegionGroupFlag();
            if (groupFlag != null) {
                region.setFlag(groupFlag, null);
            }

            player.sendMessage(ChatColor.YELLOW
                    + "Region flag " + foundFlag.getName() + " removed from #" +
                    ChatColor.GOLD + structure.getId() + " " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.YELLOW + "'. (Any groups were also removed.)");
        } else {
            player.sendMessage(new String[]{
                ChatColor.RED + "Usage: ",
                ChatColor.RED + "/stt flag [id] remove [flag]",
                ChatColor.RED + "/stt flag [id] add [flag][value]"
            });
        }
        
        return true;
    }
    
     /**
     * WorldGuard Utility method to set a flag.
     * 
     * @param region the region
     * @param flag the flag
     * @param sender the sender
     * @param value the value
     * @throws InvalidFlagFormat thrown if the value is invalid
     */
    private static <V> void setFlag(ProtectedRegion region,
            Flag<V> flag, CommandSender sender, String value)
                    throws InvalidFlagFormat {
        region.setFlag(flag, flag.parseInput(WorldGuardPlugin.inst(), sender, value));
    }

    private boolean owner(Player player, String[] args) {
        /**
         * /stt owner [id]
         * /stt owner [id] add [playerName]
         * /stt owner [id] remove [playerName]
         */
        if(args.length < 2 || args.length == 3) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        }
        
        Long id;
        try {
            id = Long.parseLong(args[1]);
        } catch(NumberFormatException nfe) {
            player.sendMessage(ChatColor.RED + "Invalid structure id");
            return true;
        }
        
        StructureService service = new StructureService();
        Structure structure = service.getStructure(id);
        
        if(structure == null) {
            player.sendMessage(ChatColor.RED + "Structure not found...");
            return true;
        }
        
        if(args.length == 2) {
            return displayOwners(player, structure);
        } else if(args.length == 4) {
            if(args[2].equals("add")) {
            return addOwner(player, structure, args);
            } else if(args[2].equals("remove")) {
                return removeOwner(player, structure, args);
            } else {
                player.sendMessage(ChatColor.RED + "Invalid argument '" + args[2] + "'");
                return true;
            }
        } else {
            player.sendMessage(ChatColor.RED + "Too many arguments!");
            return true;
        }
    }

    private boolean displayOwners(Player player, Structure structure) {
        World world = structure.getWorld();
        if(world == null) {
            player.sendMessage(ChatColor.RED + "Structure doesnt have a world anymore...");
            return true;
        }
        
        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(world);
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            player.sendMessage(ChatColor.RED + "#" 
                    + ChatColor.GOLD + structure.getId() + " "
                    + ChatColor.BLUE + structure.getPlan().getDisplayName() + " "
                    + ChatColor.RED + "doesn't have a region!"
            );
            return true;
        }
        
        Set<String> players = StructureManager.getInstance().getOwners(structure);
        String owners = "";
        if(players.isEmpty()) {
            owners = ChatColor.RED + "(none)";
        } else {
            int count = 0;
            for(String ply : players) {
                owners += ply;
                count++;
                if(count < players.size()) {
                    owners += ",";
                }
            }
        }
        
        
        player.sendMessage(new String[]{
                "Id: " + ChatColor.GOLD + structure.getId() + ChatColor.RESET +  "Plan: " + ChatColor.BLUE + structure.getPlan().getDisplayName(),
                "Owners: " + owners
        });
        return true;
    }

    private boolean addOwner(Player player, Structure structure, String[] args) {
        WorldGuardPlugin plugin = SCWorldGuardUtil.getWorldGuard();
        World world = structure.getWorld();
        if(world == null) {
            player.sendMessage(ChatColor.RED + "Structure doesnt have a world anymore...");
            return true;
        }
        
        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(world);
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            player.sendMessage(ChatColor.RED + "#" 
                    + ChatColor.GOLD + structure.getId() + " "
                    + ChatColor.BLUE + structure.getPlan().getDisplayName() + " "
                    + ChatColor.RED + "doesn't have a region!"
            );
            return true;
        }
        
        
        String ply = args[3];
        Boolean flag = region.getFlag(DefaultFlag.BUYABLE);
        Player p = Bukkit.getPlayer(ply);
        if(p == null) {
            player.sendMessage(ChatColor.RED + "Player " + ply + " doesn't exist");
            return true;
        }
        String id = structure.getStructureRegion();
        LocalPlayer localPlayer = SCWorldGuardUtil.getLocalPlayer(player);
        DefaultDomain owners = region.getOwners();
        if (localPlayer != null) {
            if (flag != null && flag && owners != null && owners.size() == 0) {
                try {
                    if (!plugin.hasPermission(player, "worldguard.region.unlimited")) {
                        int maxRegionCount = plugin.getGlobalStateManager().get(world).getMaxRegionCount(player);
                        if (maxRegionCount >= 0 && rmgr.getRegionCountOfPlayer(localPlayer)
                                >= maxRegionCount) {
                            player.sendMessage("You already own the maximum allowed amount of regions.");
                        }
                    }
                    plugin.checkPermission(player, "worldguard.region.addowner.unclaimed." + id.toLowerCase());
                } catch (CommandPermissionsException ex) {
                    player.sendMessage(ChatColor.RED + "You dont have the permission");
                    return true;
                }
            } else {
                try {
                if (region.isOwner(localPlayer)) {
                    plugin.checkPermission(player, "worldguard.region.addowner.own." + id.toLowerCase());
                } else if (region.isMember(localPlayer)) {
                    plugin.checkPermission(player, "worldguard.region.addowner.member." + id.toLowerCase());
                } else {
                    plugin.checkPermission(player, "worldguard.region.addowner." + id.toLowerCase());
                }
                } catch(CommandPermissionsException ex) {
                    player.sendMessage(ChatColor.RED + "You dont have the permission");
                    return true;
                }
                
            }
        }
        
        RegionDBUtil.addToDomain(region.getOwners(), new String[]{ply}, 0);

        player.sendMessage(ChatColor.YELLOW
                +  "Region #" + ChatColor.GOLD + structure.getId() + " "+ ChatColor.BLUE + structure.getPlan() + ChatColor.YELLOW  + " updated.");

        try {
            rmgr.save();
        } catch (ProtectionDatabaseException e) {
            player.sendMessage("Failed to write regions: "+ e.getMessage());
        }
        
        return true;
        
    }

    private boolean removeOwner(Player player, Structure structure, String[] args) {
        WorldGuardPlugin plugin = SCWorldGuardUtil.getWorldGuard();
        World world = structure.getWorld();
        if(world == null) {
            player.sendMessage(ChatColor.RED + "Structure doesnt have a world anymore...");
            return true;
        }
        
        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(world);
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            player.sendMessage(ChatColor.RED + "#" 
                    + ChatColor.GOLD + structure.getId() + " "
                    + ChatColor.BLUE + structure.getPlan().getDisplayName() + " "
                    + ChatColor.RED + "doesn't have a region!"
            );
            return true;
        }
        
        
        String ply = args[3];
        Player p = Bukkit.getPlayer(ply);
        if(p == null) {
            player.sendMessage(ChatColor.RED + "Player " + ply + " doesn't exist");
            return true;
        }
        String id = structure.getStructureRegion();
        LocalPlayer localPlayer = SCWorldGuardUtil.getLocalPlayer(player);
        
        if (localPlayer != null) {
            try {
            if (region.isOwner(localPlayer)) {
                plugin.checkPermission(player, "worldguard.region.removeowner.own." + id.toLowerCase());
            } else if (region.isMember(localPlayer)) {
                plugin.checkPermission(player, "worldguard.region.removeowner.member." + id.toLowerCase());
            } else {
                plugin.checkPermission(player, "worldguard.region.removeowner." + id.toLowerCase());
            }
            } catch (CommandPermissionsException ex) {
                player.sendMessage(ChatColor.RED + "You don't have the permission");
            }
        }


        player.sendMessage(ChatColor.YELLOW
                + "Region #" + ChatColor.GOLD + structure.getId() + " "+ ChatColor.BLUE + structure.getPlan() + ChatColor.YELLOW  + " updated.");
        RegionDBUtil.removeFromDomain(region.getOwners(), new String[]{ply}, 0);
        try {
            rmgr.save();
        } catch (ProtectionDatabaseException e) {
            player.sendMessage("Failed to write regions: "
                    + e.getMessage());
        }
        return true;
    }
}
