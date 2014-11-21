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
package com.chingo247.settlercraft.bukkit;

import com.chingo247.settlercraft.structure.persistence.hibernate.PlayerMembershipDAO;
import com.chingo247.settlercraft.structure.persistence.hibernate.PlayerOwnershipDAO;
import com.chingo247.settlercraft.structure.persistence.hibernate.ValidationService;
import com.chingo247.settlercraft.structure.PlayerMembership;
import com.chingo247.settlercraft.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.util.WorldGuardUtil;
import com.chingo247.settlercraft.structure.world.Dimension;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 *
 * @author Chingo
 */
public class BukkitValidationService extends ValidationService {
    
    private final PlayerMembershipDAO playerMembershipDAO = new PlayerMembershipDAO();
    private final PlayerOwnershipDAO playerOwnershipDAO = new PlayerOwnershipDAO();

    public BukkitValidationService(BukkitStructureAPI structureAPI) {
        super(structureAPI);
    }

    @Override
    protected boolean firstTime(String world) throws IOException {
        World w = Bukkit.getWorld(world);
        File worldsFolder = new File(structureAPI.getStructureDataFolder(), "/restoreservice");
        worldsFolder.mkdirs();
        File scWorldFile = new File(worldsFolder, world);
        File lockFile = new File(w.getWorldFolder(), "session.lock");

        if (scWorldFile.exists()) {
            if (FileUtils.contentEquals(scWorldFile, lockFile)) {
                return false;
            } else {
                FileUtils.copyFile(lockFile, scWorldFile);
                return true;
            }

        }
        FileUtils.copyFile(lockFile, scWorldFile);

        return true;

    }

    @Override
    protected HashMap<String, Timestamp> getWorldData() {
        HashMap<String, Timestamp> worldData = new HashMap<>();
        for (World world : Bukkit.getWorlds()) {
            try {
                if (!firstTime(world.getName())) {
                    continue;
                }
            } catch (IOException ex) {
                continue;
            }

            if (isSettlerCraftWorld(world.getName())) {
                Timestamp t = new Timestamp(world.getWorldFolder().lastModified());
                worldData.put(world.getName(), t);
            }
        }
        return worldData;
    }

    @Override
    protected void reclaim(Structure structure) {
        World world = Bukkit.getWorld(structure.getLocation().getWorld());
        if (world == null) {
            return;
        }
        RegionManager mgr = WorldGuardUtil.getRegionManager(world);
        if (mgr == null) {
            return;
        }

        Dimension dim = structure.getDimension();
        Vector p1 = dim.getMinPosition();
        Vector p2 = dim.getMaxPosition();
        String id = structure.getStructureRegion();

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        region.setOwners(new DefaultDomain());


        // Set Owners
        for (PlayerOwnership owner : playerOwnershipDAO.getOwners(structure)) {
            LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(owner.getPlayerUUID()));

            region.getOwners().addPlayer(lp);
        }


        // Set Owners
        for (PlayerMembership member : playerMembershipDAO.getMembers(structure)) {
            LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(member.getUUID()));
            region.getMembers().addPlayer(lp);
        }

        mgr.addRegion(region);

        try {
            mgr.save();
        } catch (StorageException ex) {
            Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void removeRegion(Structure structure, String w) {
        World world = Bukkit.getWorld(w);
        
        if (world == null) {
            return;
        }

        RegionManager rmgr = WorldGuardUtil.getRegionManager(world);
        if (rmgr == null) {
            return;
        }

        rmgr.removeRegion(structure.getStructureRegion());
        structureAPI.print("Region: " + structure.getStructureRegion() + " has been removed");
    }

    @Override
    protected void saveChanges(String world) throws StorageException {
        World w = Bukkit.getWorld(world);
        RegionManager rmgr = WorldGuardUtil.getRegionManager(w);
        rmgr.saveChanges();
    }

}
