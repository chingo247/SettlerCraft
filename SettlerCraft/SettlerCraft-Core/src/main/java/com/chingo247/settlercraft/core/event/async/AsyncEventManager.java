///*
// * Copyright (C) 2015 Chingo
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
//package com.chingo247.settlercraft.core.event.async;
//
//import com.chingo247.settlercraft.core.SettlerCraft;
//import com.google.common.eventbus.AsyncEventBus;
//
///**
// *
// * @author Chingo
// */
//public class AsyncEventManager {
//    
//    private static AsyncEventManager instance;
//    
//    private final AsyncEventBus asyncEventBus;
//    
//    private AsyncEventManager() {
//        this.asyncEventBus = new AsyncEventBus("SettlerCraft-AsyncEventBus", SettlerCraft.getInstance().getExecutor());
//    }
//    
//    public static AsyncEventManager getInstance() {
//        if(instance == null) {
//            instance = new AsyncEventManager();
//        }
//        return instance;
//    }
//    
//    public void post(Object event) {
//        asyncEventBus.post(event);
//    }
//    
//    public void register(Object subscriber) {
//        asyncEventBus.register(subscriber);
//    }
//    
//    public void unregister(Object subscriber) {
//        asyncEventBus.unregister(subscriber);
//    }
//
//    public AsyncEventBus getEventBus() {
//        return asyncEventBus;
//    }
//     
//}
