/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.persistence;

/**
 *
 * @author Chingo
 */
public class SettlerCraftService extends AbstractService {

    public SettlerCraftService() {
        super();
    }

    

    public static void main(String[] args) {
        SettlerCraftService service = new SettlerCraftService();
        service.getSessionFactory().openSession();
    }
    
    

}
