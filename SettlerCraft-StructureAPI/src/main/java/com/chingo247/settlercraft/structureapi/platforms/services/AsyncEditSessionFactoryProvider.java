/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.services;

import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

/**
 *
 * @author Chingo
 */
public interface AsyncEditSessionFactoryProvider {
    
    public AsyncEditSessionFactory getFactory();
    
}
