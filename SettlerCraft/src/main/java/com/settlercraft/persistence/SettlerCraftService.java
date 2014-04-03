/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.persistence;


/**
 *
 * @author Chingo
 * @param <T> The object for this service
 */
public interface SettlerCraftService <T extends Object> {

    public T save(T object);
    public void delete(T object);

}
