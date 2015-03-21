/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.model.util;

/**
 *
 * @author Chingo
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.io.File;


/**
 *
 * @author Chingo
 */
public class SCLogger {
    
    private static SCLogger logger = new SCLogger();
    
    
   
    
    public static SCLogger getLogger() {
        if(logger == null) {
            logger = new SCLogger();
        }
        return logger;
    }
    
    private static final String PREFIX = "[SettlerCraft]: ";
    private static final String ERROR_PREFIX = "[ERROR]:";
    private final LogLevel currenLevel = LogLevel.INFO;
    
    public void print(String message) {
        print(LogLevel.INFO, message);
    }
    
    public void print(LogLevel level, String message) {
        if(level == LogLevel.OFF) return;
        if(level == LogLevel.ERROR) {
            System.err.println(ERROR_PREFIX + message);
        } else if(level.intValue() >= currenLevel.intValue()) {
            System.out.println(PREFIX + message);
        }
    }
    
    public void print(LogLevel level, File file, String type, Long time) {
        print(level, file.getName(), type, time);
    }
    
    public void print(LogLevel level, String data, String type, Long time) {
        if(level == LogLevel.OFF) return;
        if(level.intValue() >= currenLevel.intValue()) {
            if(time != null) {
                System.out.println("[SettlerCraft]["+type+"]:" + data + " time: " + (time) + " ms");
            } else {
                System.out.println("[SettlerCraft]["+type+"]:" + data + " time: " + (time) + " ms");
            } 
        }
    }
    
    public void print(LogLevel level, File file, String type) {
        print(level, file, type, null);
    }
    
}
