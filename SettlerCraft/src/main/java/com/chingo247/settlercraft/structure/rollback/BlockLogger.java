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
package com.chingo247.settlercraft.structure.rollback;

import com.chingo247.settlercraft.structure.persistence.HSQLServer;
import com.chingo247.settlercraft.structure.persistence.hibernate.HibernateUtil;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class BlockLogger {
    
    private final Timer timer;
    private Queue<BlockLog> blocksQueue = new LinkedBlockingQueue<>();
    private final Lock lock = new ReentrantLock();
    private final int BATCH = 1_000;
    private final int THRESHOLD = 1000;
    
    
    private final TimerTask task = new TimerTask() {

        @Override
        public void run() {
            if(blocksQueue.isEmpty())  return;
            if(!lock.tryLock()) return;
            
            System.out.println("Logging...");
            Session session = null;
            Transaction tx = null;
            try {
               
                session = HibernateUtil.getSession();
                tx = session.beginTransaction();
                int commited = 0;
                long start = System.currentTimeMillis();
                
                while(blocksQueue.peek() != null && ((System.currentTimeMillis() - start) < THRESHOLD)) {
                    session.persist(blocksQueue.poll());
                    commited++;
                    
                    if(commited % BATCH == 0) {
                        tx.commit();
                        System.out.println("Committing: " + commited);
                        System.out.println("Queue: " + blocksQueue.size());
                        tx = session.beginTransaction();
                        commited = 0;
                    }
                }
                System.out.println("Committing: " + commited);
                System.out.println("Queue: " + blocksQueue.size());
               
                
                tx.commit();
            } catch(Exception e) {
                if(tx != null) {
                    tx.rollback();
                }
                throw e;
            } finally {
                if(session != null) {
                    session.close();
                }
                lock.unlock();
            }
            
        }
    };
    

    public BlockLogger() {
//        BlocksHub b = (BlocksHub) Bukkit.getPluginManager().getPlugin("BlocksHub");
//        this.timer = new Timer("SettlerCraft-BlockLogger");
        
//        try {
//            
//            
//            Field mLogicField = b.getClass().getDeclaredField("m_logic");
//            mLogicField.setAccessible(true);
//            Logic logic = (Logic)mLogicField.get(b);
//          
//            Field mLoggersField = logic.getClass().getDeclaredField("m_loggers");
//            mLoggersField.setAccessible(true);
//            List list = (List) mLoggersField.get(logic);
//            list.add(task);
//        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
//            Logger.getLogger(this.getClass()).error(ex);
//        } 
        this.timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    
    public void logBlock(int x, int y, int z, int oldMaterial, byte oldData, int newMaterial, byte newData) {
        blocksQueue.add(new BlockLog(x, y, z, oldMaterial, oldData, newMaterial, newData));
    }
    
    public static void main(String[] args) {
        HSQLServer.getInstance().start();
        
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QBlockLog qb = QBlockLog.blockLog;
        long total = query.from(qb).count();
        session.close();
        
        System.out.println("Total: " + total);
        
        
        BlockLogger logger = new BlockLogger();
        for(int j = 0; j < 100; j++) {
            for(int i = 0; i < 100_000; i++) {
                logger.logBlock(i, i, i, 10, new Integer(0).byteValue(), i, new Integer(0).byteValue());
            }
        }
        
    }
    
    
    
}
