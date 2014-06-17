/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.construction.structure;

import com.sc.construction.asyncworldEdit.ConstructionStructureCallback;
import com.sc.construction.asyncworldEdit.SCAsyncCuboidClipboard;
import com.sc.construction.plan.StructureSchematic;
import com.sc.construction.sync.SyncBuilder;
import com.sc.construction.sync.TaskCompleteCallback;
import com.sc.persistence.SchematicService;
import com.sc.plugin.ConfigProvider;
import com.sc.util.SCAsyncWorldEditUtil;
import com.sc.util.SCWorldEditUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import org.apache.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class StructureTask extends BukkitRunnable {

    private static final int ENCLOSURE_PLACE_SPEED = 200;
    private final Logger LOG = Logger.getLogger(StructureTask.class);
    private final Structure structure;
    private final boolean isDemolishing;
    private final Player tasker;
    private BukkitTask enclosureTask;
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
        
        final AsyncEditSession structureSession = SCAsyncWorldEditUtil.createAsyncEditSession(
                SCWorldEditUtil.getLocalPlayer(tasker), -1);
        // Align the clipboard
        StructureManager.getInstance().align(clipboard, structure.getCardinal());
        Comparator<StructureBlock> bm = StructureBlockComparators.getMode(ConfigProvider.getInstance().getBuildMode());
        Comparator<StructureBlock> dm = StructureBlockComparators.getMode(ConfigProvider.getInstance().getDemolisionMode());
        
        final StructureClipboard structureClipboard = new StructureClipboard(clipboard, bm, dm);
        structureClipboard.setDemolishing(isDemolishing);

        // If we aren't demolishing place the enclosure
        if (!isDemolishing) {
            
            // Create the enclosure and place it
            
            final EditSession enclosureSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(SCWorldEditUtil.getWorld(structure.getWorld()), ENCLOSURE_PLACE_SPEED);
            // Set the enclosure, so that it will be replaced with real blocks when finished
            // Note: The Clipboard is always drawn from the min position using the place method
            enclosureTask = SyncBuilder.placeBuffered(enclosureSession, structureClipboard.getEnclosure().getClipboard(), structure.getDimension().getMinPosition(), ENCLOSURE_PLACE_SPEED, new TaskCompleteCallback() {

                @Override
                public void onComplete() {
                    doStructure(tasker.getName(), structureClipboard, structureSession);
                }
            });
        } else { // Otherwise place the structure clipboard, it will handle the removal
            
            doStructure(tasker.getName(), structureClipboard, structureSession);
        }
    }

    private void doStructure(String ply, StructureClipboard clipboard, AsyncEditSession session) {
        // Build the structure onComplete of the enclosure
        
        
        final ConstructionStructureCallback sCallback = new ConstructionStructureCallback(tasker, structure, session);
        final SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(session.getPlayer(), clipboard);
        try {
            // Note: The Clipboard is always drawn from the min position using the place method
            asyncStructureClipboard.place(session, structure.getDimension().getMinPosition(), false, sCallback);
        } catch (MaxChangedBlocksException ex) { // shouldnt happen
            LOG.error(ex);
        }

    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if(enclosureTask != null) {
            enclosureTask.cancel();
        }
        super.cancel();
    }
    
    

}
