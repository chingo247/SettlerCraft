/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.core.util;

import com.google.common.base.Preconditions;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jpountz.xxhash.StreamingXXHash32;
import net.jpountz.xxhash.StreamingXXHash64;
import net.jpountz.xxhash.XXHashFactory;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Chingo
 */
public class XXHasher {

    private final XXHashFactory factory;

    public XXHasher() {
        this.factory = XXHashFactory.safeInstance();
    }

    /**
     *
     * @param f The file
     * @param buffer The buffer size, recommended size is at least 8192. Also
     * consider usage of a value which is a power of 2
     * @return
     * @throws IOException
     */
    public long hash64(File f, int buffer) throws IOException {
        Preconditions.checkArgument(buffer > 0, "buffer should be greater than 0");
        int seed = 0x9747b24c;

        StreamingXXHash64 hash64 = factory.newStreamingHash64(seed);

        ByteArrayInputStream in = new ByteArrayInputStream(IOUtils.toByteArray(new FileInputStream(f)));

        byte[] buf = new byte[buffer];
        for (;;) {
            int read = in.read(buf);
            if (read == -1) {
                break;
            }
            hash64.update(buf, 0, read);
        }
        return hash64.getValue();
    }

    /**
     *
     * @param f The file
     * @return
     * @throws IOException
     */
    public long hash64(File f) throws IOException {
        return hash64(f, 8192);
    }

    public static void main(String[] args) {
        File f = new File("F:\\GAMES\\MineCraftServers\\Bukkit 1.7.10-SettlerCraft-RC4\\plugins\\SettlerCraft-StructureAPI\\plans\\");

        XXHasher hasher = new XXHasher();
        for (File file : f.listFiles()) {
            long hash;
            try {
                hash = hasher.hash64(file);
                System.out.println(file.getName() + ": \t" + hash);
            } catch (IOException ex) {
                Logger.getLogger(XXHasher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public int hash32String(String string) {
        int seed = 0x9747b28c; 
        StreamingXXHash32 hash32 = factory.newStreamingHash32(seed);
        try {
            byte[] data = string.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            
            byte[] buf = new byte[8192];
            for (;;) {
                int read = in.read(buf);
                if (read == -1) {
                    break;
                }
                hash32.update(buf, 0, read);
            }
           
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return hash32.getValue();
       
    }
    
    public long hash64String(String string) {
        int seed = 0x9747b28c; 
        StreamingXXHash64 hash64 = factory.newStreamingHash64(seed);
        try {
            byte[] data = string.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            
            byte[] buf = new byte[8192];
            for (;;) {
                int read = in.read(buf);
                if (read == -1) {
                    break;
                }
                hash64.update(buf, 0, read);
            }
           
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return hash64.getValue();
       
    }


}
