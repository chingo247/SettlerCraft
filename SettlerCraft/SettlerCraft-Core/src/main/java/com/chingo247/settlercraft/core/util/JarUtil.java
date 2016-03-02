/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 *
 * @author Chingo
 */
public class JarUtil {

    private JarUtil() {
    }

    public static void createDefault(File actualFile, File jarFile, String path) {
        if (!actualFile.exists()) {
            InputStream input = null;
            try {
                JarFile file = new JarFile(jarFile);
                ZipEntry copy = file.getEntry(path);
                if (copy == null) {
                    throw new FileNotFoundException();
                }
                input = file.getInputStream(copy);
            } catch (IOException e) {
                Logger.getLogger(JarUtil.class.getName()).log(Level.SEVERE, "Unable to read default configuration: {0}", path);
            }
            if (input != null) {
                FileOutputStream output = null;

                try {
                    new File(actualFile.getParent()).mkdirs();
                    actualFile.createNewFile();
                    output = new FileOutputStream(actualFile);
                    byte[] buf = new byte[8192];
                    int length;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }

//                    Logger.getLogger(JarUtil.class.getName()).log(Level.INFO, "Default file written: {0}", path);
                } catch (IOException e) {
                    Logger.getLogger(JarUtil.class.getName()).log(Level.WARNING, "Failed to write default config file", e);
                } finally {
                    try {
                        input.close();
                    } catch (IOException ignored) {
                    }

                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

}
