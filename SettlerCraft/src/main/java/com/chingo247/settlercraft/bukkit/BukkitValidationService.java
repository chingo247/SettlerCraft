///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.settlercraft.bukkit;
//
//import com.chingo247.settlercraft.structureapi.persistence.hibernate.PlayerMembershipDAO;
//import com.chingo247.settlercraft.structureapi.persistence.hibernate.PlayerOwnershipDAO;
//import com.chingo247.settlercraft.structureapi.persistence.hibernate.ValidationService;
//import com.chingo247.settlercraft.structureapi.structure.old.PlayerMembership;
//import com.chingo247.settlercraft.structureapi.structure.old.PlayerOwnership;
//import com.chingo247.settlercraft.structureapi.structure.old.Structure;
//import com.chingo247.settlercraft.util.WorldGuardUtil;
//import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
//import com.sk89q.worldedit.BlockVector;
//import com.sk89q.worldedit.Vector;
//import com.sk89q.worldguard.LocalPlayer;
//import com.sk89q.worldguard.domains.DefaultDomain;
//import com.sk89q.worldguard.protection.managers.RegionManager;
//import com.sk89q.worldguard.protection.managers.storage.StorageException;
//import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
//import java.io.File;
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.util.HashMap;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import org.apache.commons.io.FileUtils;
//import org.bukkit.Bukkit;
//import org.bukkit.World;
//
///**
// *
// * @author Chingo
// */
//public class BukkitValidationService extends ValidationService {
//    
//    private final PlayerMembershipDAO playerMembershipDAO = new PlayerMembershipDAO();
//    private final PlayerOwnershipDAO playerOwnershipDAO = new PlayerOwnershipDAO();
//
//    public BukkitValidationService(BukkitStructureAPI structureAPI) {
//        super(structureAPI);
//    }
//
//    @Override
//    protected boolean firstTime(String world) throws IOException {
//        World w = Bukkit.getWorld(world);
//        File worldsFolder = new File(structureAPI.getStructureDataFolder(), "/restoreservice");
//        worldsFolder.mkdirs();
//        File scWorldFile = new File(worldsFolder, world);
//        File lockFile = new File(w.getWorldFolder(), "session.lock");
//
//        if (scWorldFile.exists()) {
//            if (FileUtils.contentEquals(scWorldFile, lockFile)) {
//                return false;
//            } else {
//                FileUtils.copyFile(lockFile, scWorldFile);
//                return true;
//            }
//
//        }
//        FileUtils.copyFile(lockFile, scWorldFile);
//
//        return true;
//
//    }
//
//    @Override
//    protected HashMap<String, Timestamp> getWorldData() {
//        HashMap<String, Timestamp> worldData = new HashMap<>();
//        for (World world : Bukkit.getWorlds()) {
//            try {
//                if (!firstTime(world.getName())) {
//                    continue;
//                }
//            } catch (IOException ex) {
//                continue;
//            }
//
//            if (isSettlerCraftWorld(world.getName())) {
//                Timestamp t = new Timestamp(world.getWorldFolder().lastModified());
//                worldData.put(world.getName(), t);
//            }
//        }
//        return worldData;
//    }
//
//    @Override
//    protected void reclaim(Structure structure) {
//        World world = Bukkit.getWorld(structure.getLocation().getWorld());
//        if (world == null) {
//            return;
//        }
//        RegionManager mgr = WorldGuardUtil.getRegionManager(world);
//        if (mgr == null) {
//            return;
//        }
//
//        CuboidDimension dim = structure.getDimension();
//        Vector p1 = dim.getMinPosition();
//        Vector p2 = dim.getMaxPosition();
//        String id = structure.getStructureRegion();
//        
//        System.out.println("StructureRegion: " + id);
//
//        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
//        region.setOwners(new DefaultDomain());
//
//
//        // Set Owners
//        for (PlayerOwnership owner : playerOwnershipDAO.getOwners(structure)) {
//            LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(owner.getPlayerUUID()));
//
//            region.getOwners().addPlayer(lp);
//        }
//
//
//        // Set Owners
//        for (PlayerMembership member : playerMembershipDAO.getMembers(structure)) {
//            LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(member.getUUID()));
//            region.getMembers().addPlayer(lp);
//        }
//
//        mgr.addRegion(region);
//
//        try {
//            mgr.save();
//        } catch (StorageException ex) {
//            Logger.getLogger(ValidationService.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    @Override
//    protected void removeRegion(Structure structure, String w) {
//        World world = Bukkit.getWorld(w);
//        
//        if (world == null) {
//            return;
//        }
//
//        RegionManager rmgr = WorldGuardUtil.getRegionManager(world);
//        if (rmgr == null) {
//            return;
//        }
//
//        rmgr.removeRegion(structure.getStructureRegion());
//        structureAPI.print("Region: " + structure.getStructureRegion() + " has been removed");
//    }
//
//    @Override
//    protected void saveChanges(String world) throws StorageException {
//        World w = Bukkit.getWorld(world);
//        RegionManager rmgr = WorldGuardUtil.getRegionManager(w);
//        rmgr.saveChanges();
//    }
//
//}
