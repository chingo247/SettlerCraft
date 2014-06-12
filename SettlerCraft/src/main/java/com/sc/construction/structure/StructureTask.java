/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.construction.structure;

import com.sc.construction.async.ConstructionStructureCallback;
import com.sc.construction.async.SCAsyncCuboidClipboard;
import com.sc.construction.generator.Enclosure;
import com.sc.construction.plan.StructureSchematic;
import com.sc.construction.sync.SyncBuilder;
import com.sc.construction.sync.TaskCompleteCallback;
import com.sc.persistence.SchematicService;
import com.sc.util.SCAsyncWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class StructureTask extends BukkitRunnable {

    private static final int ENCLOSURE_PLACE_SPEED = 100;
    private final Logger LOG = Logger.getLogger(StructureTask.class);
    private final Structure structure;
    private final boolean isDemolishing;
    private final Player tasker;
    /**
     * TODO UUID here after AsyncWorldEditUpdate to 2.0 Constructor
     *
     * @param tasker
     * @param structure
     * @param demolishing
     */
    StructureTask(Player tasker, Structure structure, boolean demolishing) {
        this.tasker = tasker;
        this.structure = structure;
        this.isDemolishing = demolishing;
    }

    @Override
    public void run() {
        // Get the clipboard
        SchematicService ss = new SchematicService();
        StructureSchematic schematic = ss.getSchematic(structure.getPlan().getSchematicChecksum());
        File schematicFile = schematic.getSchematic();
        CuboidClipboard clipboard = null;
        try {
            clipboard = SchematicFormat.MCEDIT.load(schematicFile);
        } catch (IOException | DataException ex) {
            LOG.error(ex);
        }

        // Align the clipboard
        StructureManager.getInstance().align(clipboard, structure.getCardinal());
        final StructureClipboard structureClipboard = new StructureClipboard(clipboard);
        structureClipboard.setDemolishing(isDemolishing);

        // If we aren't demolishing place the enclosure
        if (!isDemolishing) {
            // Create the enclosure and place it
            final Enclosure enclosure = new Enclosure(clipboard, 1, BlockID.IRON_BARS); // Iron bars wont cause lag, FENCES WILL CAUSE LAG
            final EditSession enclosureSession = new EditSession(structure.getLocation().getWorld(), -1);
            // Set the enclosure, so that it will be replaced with real blocks when finished
            structureClipboard.setEnclosure(enclosure);
            // Note: The Clipboard is always drawn from the min position using the place method
            SyncBuilder.placeBuffered(enclosureSession, enclosure.getEnclosure(), structure.getDimension().getMin().getPosition(), ENCLOSURE_PLACE_SPEED, new TaskCompleteCallback() {

                @Override
                public void onComplete() {
                    doStructure(tasker.getName(), structureClipboard);
                }
            });
        } else { // Otherwise place the structure clipboard, it will handle the removal
            doStructure(tasker.getName(), structureClipboard);
        }

    }

    private void doStructure(String ply, StructureClipboard clipboard) {
        // Build the structure onComplete of the enclosure
        final AsyncEditSession structureSession = SCAsyncWorldEditUtil.createAsyncEditSession(tasker.getName(), structure.getLocation().getWorld(), -1);
        final ConstructionStructureCallback sCallback = new ConstructionStructureCallback(tasker, structure, structureSession);
        final SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(structureSession.getPlayer(), clipboard);
        try {
            // Note: The Clipboard is always drawn from the min position using the place method
            asyncStructureClipboard.place(structureSession, structure.getDimension().getMin().getPosition(), false, sCallback);
        } catch (MaxChangedBlocksException ex) { // shouldnt happen
            LOG.error(ex);
        }

    }

}
