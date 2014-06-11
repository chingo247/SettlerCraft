package com.sc.event.build;

///*
// * Copyright (C) 2014 Chingo
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.sc.api.structure.event.build;
//
//import com.sc.api.structure.model.Structure;
//import org.bukkit.entity.Player;
//import org.bukkit.event.HandlerList;
//import org.bukkit.inventory.ItemStack;
//
///**
// *
// * @author Chingo
// */
//public class PlayerBuildEvent extends BuildEvent {
//
//    private final Player player;
//
//    /**
//     * Constructor.
//     *
//     * @param structure The structure involved in this event
//     * @param player The player involved in this event
//     * @param stack The itemstack involved in this event
//     */
//    public PlayerBuildEvent(Structure structure, Player player, ItemStack stack) {
//        super(structure, player, stack);
//        this.player = player;
//    }
//
//    /**
//     * Gets the player who is involved in this event.
//     *
//     * @return The player who is involved in this event
//     */
//    public Player getPlayer() {
//        return player;
//    }
//    
//    private static final HandlerList handlers = new HandlerList();
//
//    @Override
//    public HandlerList getHandlers() {
//        return handlers;
//    }
//
//    public static HandlerList getHandlerList() {
//        return handlers;
//    }
//}
