/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.commands;

import com.chingo247.settlercraft.core.exception.CommandException;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;

/**
 *
 * @author Chingo
 */
public class CommandHelper {
    
    private IColors colors;
    
    public CommandHelper(APlatform platform) {
        this.colors = platform.getChatColors();
    }
    
    public void isLong(String argument, String errormessage,String usage) throws CommandException {
        try {
            Long.parseLong(argument);
        } catch (NumberFormatException nfe) {
            throw new CommandException(colors.red() + errormessage, colors.red() + usage);
        }
    }
    
    public void isInt(String argument, String errormessage,String usage) throws CommandException {
        try {
            Integer.parseInt(argument);
        } catch (NumberFormatException nfe) {
            throw new CommandException(colors.red() + errormessage, colors.red() + usage);
        }
    }
    
    public void isTrue(boolean expression, String errormessage) throws CommandException {
        if(!expression) {
            throw new CommandException(colors.red() + errormessage);
        }
    }
    
    public void isFalse(boolean expression, String errormessage) throws CommandException {
        isTrue(!expression, errormessage);
    }
    
    public void argumentsInRange(int min, int max, String[] args, String usage) throws CommandException {
        if (args.length < min) {
            throw new CommandException("Too few arguments!", colors.red() +  usage);
        } else if (args.length > max) {
            throw new CommandException("Too many arguments!",colors.red() +  usage);
        }
    }
    
}
