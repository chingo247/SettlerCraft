/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.model;

/**
 *
 * @author Chingo
 */
public interface IPredicate<T> {
    
    /**
     * Evaluates the object
     * @param t The object to evaluate
     * @return True if evaluation of the object was true
     */
    boolean evaluate(T t);
    
}
