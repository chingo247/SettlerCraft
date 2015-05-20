/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class MemFileUtil {
    
    public static void writeToMemory(File file) throws IOException {
        FileChannel fc = new RandomAccessFile(file, "rw").getChannel();

        long bufferSize = 8 * 1000;
        MappedByteBuffer mem = fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);

        int start = 0;
        long counter = 1;
        long HUNDREDK = 100000;
        long startT = System.currentTimeMillis();
        long noOfMessage = HUNDREDK * 10 * 10;
        for (;;) {
            if (!mem.hasRemaining()) {
                start += mem.position();
                mem = fc.map(FileChannel.MapMode.READ_WRITE, start, bufferSize);
            }
            mem.putLong(counter);
            counter++;
            if (counter > noOfMessage) {
                break;
            }
        }
        long endT = System.currentTimeMillis();
        long tot = endT - startT;
        System.out.println(String.format("No Of Message %s , Time(ms) %s ", noOfMessage, tot));
    }
    
    public static void main(String[] args) {
        try {
            writeToMemory(new File("F://test.txt"));
        } catch (IOException ex) {
            Logger.getLogger(MemFileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
