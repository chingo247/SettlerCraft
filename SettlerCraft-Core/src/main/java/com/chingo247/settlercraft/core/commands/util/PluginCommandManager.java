/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.core.commands.util;

import com.chingo247.settlercraft.core.concurrent.KeyPool;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.collect.Maps;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class PluginCommandManager extends CommandsManager<ICommandSender> {

    private static final UUID CONSOLE_UUID = UUID.randomUUID();
    private Map<String, CommandExtras> extra;
    private KeyPool<UUID> commandPool;
    private APlatform platform;
    private IColors colors;

    public PluginCommandManager(ExecutorService es, APlatform platform) {
        this.commandPool = new KeyPool<>(es);
        this.extra = Maps.newHashMap();
        this.platform = platform;
        this.colors = platform.getChatColors();
    }

    private UUID getUUID(ICommandSender sender) {
        if (sender instanceof IPlayer) {
            return ((IPlayer) sender).getUniqueId();
        }
        return CONSOLE_UUID;
    }

    private boolean isConsole(ICommandSender sender) {
        return !(isPlayer(sender));
    }

    private boolean isPlayer(ICommandSender sender) {
        return (sender instanceof IPlayer);
    }

    private boolean isOP(ICommandSender sender) {
        return isConsole(sender) || ((IPlayer) sender).isOP();
    }

    @Override
    public List<Command> registerMethods(Class<?> cls, Method parent) {
        List<Command> registered = super.registerMethods(cls, parent); //To change body of generated methods, choose Tools | Templates.
        Map<String, Method> map;

        // Make a new hash map to cache the commands for this class
        // as looking up methods via reflection is fairly slow
        if (commands.containsKey(parent)) {
            map = commands.get(parent);
        } else {
            map = new HashMap<>();
            commands.put(parent, map);
        }

        for (Command c : registered) {
            for (String alias : c.aliases()) {
                Method m = map.get(alias);
                if (m.isAnnotationPresent(CommandExtras.class)) {
                    CommandExtras commandExtra = m.getAnnotation(CommandExtras.class);
                    extra.put(alias.toLowerCase(), commandExtra);
                }
            }
        }

        return registered;
    }

    @Override
    public void executeMethod(final Method parent, final String[] args, final ICommandSender sender, final Object[] methodArgs, final int level) throws CommandException {
        String cmdName = args[level];

        CommandExtras commandExtra = extra.get(cmdName.toLowerCase());
        if (commandExtra != null) {
            // Check restricted sender
            if (commandExtra.senderType() != CommandSenderType.ANY) {
                switch (commandExtra.senderType()) {
                    case CONSOLE:
                        if (!isConsole(sender)) {
                            throw new CommandException("/" + cmdName + " - Can only be executed from console");
                        }
                        break;
                    case OP:
                        if (!isOP(sender)) {
                            throw new CommandException("/" + cmdName + " - This command can exclusivly be executed by OP's");
                        }
                        break;
                    case PLAYER:
                        if (!isPlayer(sender)) {
                            throw new CommandException("/" + cmdName + " - You need to be a player to execute this command");
                        }
                        break;
                    default:
                        throw new AssertionError("Unreachable");
                }

            }

            if (!commandExtra.async()) {
                super.executeMethod(parent, args, sender, methodArgs, level); //To change body of generated methods, choose Tools | Templates.
            } else {
                commandPool.execute(getUUID(sender), new Runnable() {

                    @Override
                    public void run() {
                        try {
                            PluginCommandManager.super.executeMethod(parent, args, sender, methodArgs, level);
                        } catch (CommandPermissionsException e) {
                            sender.sendMessage(colors.red() + "You don't have permission.");
                        } catch (MissingNestedCommandException e) {
                            sender.sendMessage(colors.red() + e.getUsage());
                        } catch (CommandUsageException e) {
                            sender.sendMessage(colors.red() + e.getMessage());
                            sender.sendMessage(colors.red() + e.getUsage());
                        } catch (WrappedCommandException e) {
                            if (e.getCause() instanceof NumberFormatException) {
                                sender.sendMessage(colors.red() + "Number expected, string received instead.");
                            } else {
                                sender.sendMessage(colors.red() + "An error has occurred. See console.");
                                e.printStackTrace();
                            }
                        } catch (com.sk89q.minecraft.util.commands.CommandException e) {
                            sender.sendMessage(colors.red() + e.getMessage());
                        } catch (Exception ex) {
                            sender.sendMessage(colors.red() + "An error has occurred. See console (if possible).");
                            Logger.getLogger(PluginCommandManager.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }

                });
            }

        } else { // Execute normally
            super.executeMethod(parent, args, sender, methodArgs, level); //To change body of generated methods, choose Tools | Templates.
        }

    }

    @Override
    public boolean hasPermission(ICommandSender sender, String permission) {
        return (!(sender instanceof IPlayer)) || sender.hasPermission(permission);
    }
}
