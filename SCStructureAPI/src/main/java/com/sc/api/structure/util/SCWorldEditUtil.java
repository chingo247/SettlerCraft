/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.util;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class for often used WorldEdit Operations
 *
 * @author Chingo
 */
public class SCWorldEditUtil {

    public static CuboidClipboard load(File SchematicFile) throws FileNotFoundException, DataException, IOException {
        SchematicFormat format = SchematicFormat.getFormat(SchematicFile);

        return format.load(SchematicFile);
    }



}
