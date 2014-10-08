/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Chingo
 */
public class FileUtil {
    
    private FileUtil() {}
    
    public static void write(InputStream inputStream, File to) {
        OutputStream outputStream = null;

        try {

            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(to);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            org.apache.log4j.Logger.getLogger(FileUtil.class).error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    org.apache.log4j.Logger.getLogger(FileUtil.class).error(e.getMessage());
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    org.apache.log4j.Logger.getLogger(FileUtil.class).error(e.getMessage());
                }

            }
        }
    }
    
}
