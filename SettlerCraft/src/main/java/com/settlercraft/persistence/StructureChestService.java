/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.persistence;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.settlercraft.model.entity.WorldLocation;
import com.settlercraft.model.entity.structure.QStructureChest;
import com.settlercraft.model.entity.structure.StructureChest;
import com.settlercraft.util.HibernateUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureChestService extends AbstractService<StructureChest>{

    public StructureChest getStructureChest(Long id) {
        QStructureChest chest = QStructureChest.structureChest;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        StructureChest c = query.from(chest).where(chest.id.eq(id)).uniqueResult(chest);
        session.close();
        return c;
    } 
    
    public StructureChest getStructureChest(String world, int x, int y, int z) {
        QStructureChest chest = QStructureChest.structureChest;
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        StructureChest c = query.from(chest).where(chest.wlocation().eq(new WorldLocation(new Location(Bukkit.getWorld(world), x, y, z)))).uniqueResult(chest);
        session.close();
        return c;
    } 
    
}
