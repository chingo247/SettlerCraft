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
package com.chingo247.settlercraft.util.dummy;

import com.thoughtworks.xstream.io.xml.DocumentWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 */
public class DummyDataGenerator {
    
    public static void generateStructureLots(File targetFolder) throws IOException {
        if(!targetFolder.exists()) targetFolder.mkdirs();
        
        if(!targetFolder.isDirectory()) {
            throw new AssertionError("Target must be a directory");
        }
        
        OutputFormat format = OutputFormat.createPrettyPrint();
        FileWriter fw;
        for(int i = 0; i < 1000; i++) {
            fw = new FileWriter(new File(targetFolder, "test-structurelot-" + i + ".xml"));
                XMLWriter f = new XMLWriter(fw, format);
                Document d = DocumentHelper.createDocument();
                Element root = d.addElement("StructurePlan");
                
                root.addElement("Name").setText("Test-" + i);
                Element placeable = root.addElement("Placeable");
                placeable.addElement("Type").setText("StructureLot");
                placeable.addElement("Width").setText(String.valueOf(i));
                placeable.addElement("Height").setText(String.valueOf(i));
                placeable.addElement("Length").setText(String.valueOf(i));
                
                f.write(d);
                
            f.close();
        }
        
    }
    
    public static void main(String[] args) {
        try {
            
            DummyDataGenerator.generateStructureLots(new File("C:\\Users\\Chingo\\Desktop\\SettlerCraft\\StructureLotTest"));
        } catch (IOException ex) {
            Logger.getLogger(DummyDataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
