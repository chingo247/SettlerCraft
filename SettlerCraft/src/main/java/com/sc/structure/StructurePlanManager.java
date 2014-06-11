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
package com.sc.structure;

import com.cc.plugin.api.menu.MenuManager;
import com.cc.plugin.api.menu.MenuSlot;
import com.cc.plugin.api.menu.ShopCategoryMenu;
import com.sc.entity.plan.StructurePlan;
import com.sc.entity.plan.StructureSchematic;
import com.sc.persistence.SchematicService;
import com.sc.structure.exception.StructurePlanException;
import com.sc.util.SettlerCraftUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {

    //TODO Load plans on demand 
    public static final String PLAN_MENU = "Plan Menu";
    public static final String PLANSHOP = "Buy & Build";

    private final Logger LOGGER = Logger.getLogger(StructurePlanManager.class);
    private final Map<String, StructurePlan> plans;
    private final Map<Long, CuboidClipboard> clipboards;
    private static StructurePlanManager instance;
    private final SchematicService ss = new SchematicService();
    private boolean loaded = false;
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private StructurePlanManager() {
        this.plans = Collections.synchronizedMap(new HashMap<String, StructurePlan>());
        this.clipboards = Collections.synchronizedMap(new HashMap<Long, CuboidClipboard>());
    }

    public static StructurePlanManager getInstance() {
        if (instance == null) {
            instance = new StructurePlanManager();
        }
        return instance;
    }
    
    public Vector getSize(Long checksum) {
        CuboidClipboard clipboard = clipboards.get(checksum);
        if(checksum != null) {
            return clipboard.getSize();
        } else {
            return null;
        }
    }

    private void setupMenus() {
        setupPlanMenu();
        setupPlanShop();
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[SettlerCraft]: " + ChatColor.RESET + " plans loaded");
        System.out.println("[PlanManager]: Loaded!");
    }

    private void setupPlanMenu() {
        ShopCategoryMenu planMenu = new ShopCategoryMenu(PLAN_MENU, true, true);

        // Add Plan Categories
        planMenu.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planMenu.addCategory(1, new ItemStack(Material.WORKBENCH), "General");
        planMenu.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planMenu.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planMenu.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planMenu.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planMenu.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Fortifications", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
        planMenu.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planMenu.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planMenu.addActionSlot(9, new ItemStack(Material.COAL_BLOCK), "Previous");
        planMenu.addActionSlot(17, new ItemStack(Material.COAL_BLOCK), "Next");
        planMenu.setLocked(10, 11, 12, 13, 14, 15, 16);
        planMenu.setDefaultCategory("All");
        planMenu.accepts("Plan");
        planMenu.setChooseDefaultCategory(true);

        Iterator<StructurePlan> pit = StructurePlanManager.getInstance().getPlans().iterator();

        while (pit.hasNext()) {
            StructurePlan plan = pit.next();
            ItemStack is = new ItemStack(Material.PAPER);
            Vector v = StructurePlanManager.getInstance().getSize(plan.getSchematicChecksum());
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            int size = v.getBlockX() * v.getBlockY() * v.getBlockZ();
            String sizeString = SettlerCraftUtil.valueString(size);
            slot.setData("Size", v.getBlockX() + "x" + v.getBlockY()+ "x" + v.getBlockZ(), ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            slot.setData("Type", "Plan", ChatColor.GOLD);
            slot.setData("Id", plan.getId(), ChatColor.GOLD);
            planMenu.addItem(slot, plan.getCategory()); //Dont fill in these slots
        }

        MenuManager.getInstance().register(planMenu);
    }

    private void setupPlanShop() {
        ShopCategoryMenu planShop = new ShopCategoryMenu(PLANSHOP, true, true);

        // Add Plan Categories
        planShop.addCategory(0, new ItemStack(Material.NETHER_STAR), "All");
        planShop.addCategory(1, new ItemStack(Material.WORKBENCH), "General");
        planShop.addCategory(2, new ItemStack(Material.ANVIL), "Industry", "Industrial", "Industries");
        planShop.addCategory(3, new ItemStack(Material.BED), "Residency", "Residence", "Residencial", "Houses", "House");
        planShop.addCategory(4, new ItemStack(Material.GOLD_INGOT), "Economy", "Economical", "Shops", "Shop", "Market", "Markets");
        planShop.addCategory(5, new ItemStack(Material.QUARTZ), "Temples", "Temple", "Church", "Sacred", "Holy");
        planShop.addCategory(6, new ItemStack(Material.SMOOTH_BRICK), "Fortifications", "Fort", "Fortification", "Wall", "Fortress", "Fortresses", "Keep", "Castle", "Castles", "Military");
        planShop.addCategory(7, new ItemStack(Material.IRON_SWORD), "Dungeons&Arenas", "Arena", "Arenas", "Dungeon", "Dungeons");
        planShop.addCategory(8, new ItemStack(Material.BUCKET), "Misc");
        planShop.addActionSlot(9, new ItemStack(Material.BED_BLOCK), "Previous");
        planShop.addActionSlot(17, new ItemStack(Material.BED_BLOCK), "Next");
        planShop.setLocked(10, 11, 12, 13, 14, 15, 16); // //Dont fill in these slots
        planShop.setDefaultCategory("All");
        planShop.accepts("Plan");
        planShop.setChooseDefaultCategory(true);

        Iterator<StructurePlan> pit = StructurePlanManager.getInstance().getPlans().iterator();
        while (pit.hasNext()) {
            StructurePlan plan = pit.next();
            ItemStack is = new ItemStack(Material.PAPER);
            MenuSlot slot = new MenuSlot(is, plan.getDisplayName(), MenuSlot.MenuSlotType.ITEM);
            Vector v = StructurePlanManager.getInstance().getSize(plan.getSchematicChecksum());
            int size = v.getBlockX() * v.getBlockY() * v.getBlockZ();
            String sizeString = SettlerCraftUtil.valueString(size);
            slot.setData("Size", v.getBlockX() + "x" + v.getBlockY()+ "x" + v.getBlockZ(), ChatColor.GOLD);
            slot.setData("Type", "Plan", ChatColor.GOLD);
            slot.setData("Blocks", sizeString, ChatColor.GOLD);
            slot.setData("Id", plan.getId(), ChatColor.GOLD);
            planShop.addItem(slot, plan.getCategory(), plan.getPrice());
        }

        MenuManager.getInstance().register(planShop);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public List<StructurePlan> getPlans() {
        return new ArrayList<>(plans.values());
    }

    public StructureClipboard getClipBoard(Long checksum) {
        CuboidClipboard cc = clipboards.get(checksum);
        if (cc != null) {
            return new StructureClipboard(cc);
        } else {
            SchematicService service = new SchematicService();
            StructureSchematic schematic = service.getSchematic(checksum);
            if (schematic != null) {
                try {
                    cc = SchematicFormat.MCEDIT.load(schematic.getSchematic());
                    clipboards.put(checksum, cc);
                    return new StructureClipboard(cc);
                } catch (IOException | DataException ex) {
                    LOGGER.error(ex);
                }
            }
        }
        return null;
    }

    public StructurePlan getPlan(String planId) {
        return plans.get(planId);
    }

    public boolean contains(StructurePlan plan) {
        return plans.containsKey(plan.getId());
    }

    public void load(File structureFolder) {
        if (!isLoaded()) {
            String[] extensions = {"yml", "schematic"};
            Iterator<File> it = FileUtils.iterateFiles(structureFolder, extensions, true);

            List<StructureSchematic> schematics = new LinkedList<>();

            while (it.hasNext()) {
                final File f = it.next();
                String extension = FilenameUtils.getExtension(f.getName());
                if (extension.equals("yml")) {
                    
                    try {
                        StructurePlan plan = readPlan(f);
                        addPlans(plan);
                    } catch (FileNotFoundException | StructurePlanException ex) {
                        LOGGER.error(ex);
                    } 
                } else if (extension.equals("schematic")) {
                    try {
                        schematics.add(new StructureSchematic(f));
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            addSchematics(schematics);
        }
    }

    private void addPlans(StructurePlan plan) throws StructurePlanException {
        if (plans.containsKey(plan.getId().trim())) {
            throw new StructurePlanException("Plan id: " + plan.getId() + " already in use!");
        } else {
            plans.put(plan.getId().trim(), plan);
        }
    }

    private void addSchematics(List<StructureSchematic> schematics)  {
        List<StructureSchematic> absent = new LinkedList<>();
        for (StructureSchematic schematic : schematics) {
            if (!ss.exists(schematic)) {
                absent.add(schematic);
                schematic = ss.save(schematic);
            }
        }
        if(absent.isEmpty()) {
            setupMenus();
            return;
        }

        final int total = absent.size();
        final AtomicInteger count = new AtomicInteger(0);
        for (final StructureSchematic s : absent) {
            executor.execute(new SchematicLoadThread(s, new SchematicCallback() {

                @Override
                public void onComplete(StructureSchematic schematic, CuboidClipboard cc) {
                    clipboards.put(schematic.getCheckSum(), cc);
                    ss.save(schematic);
                    if (count.incrementAndGet() == total) {
                        setupMenus();
                        loaded = true;
                    }
                }
            }));
        }

    }

    private StructurePlan readPlan(File structureYAML) throws FileNotFoundException, StructurePlanException {
        StructurePlan spv = null;
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(structureYAML);

            File schematicStructureFile = FileUtils.getFile(structureYAML.getParent(), config.getString("schematic"));
            if (!schematicStructureFile.exists()) {
                throw new FileNotFoundException("No such file: " + structureYAML.getParent() + "\"" + config.getString("schematic"));
            }

            SchematicFormat format = SchematicFormat.getFormat(schematicStructureFile);
            if (!format.isOfFormat(schematicStructureFile)) {
                System.err.print("[SCStructureAPI]: Unsupported format for " + format.getName() + " in: " + schematicStructureFile.getName());
                return null;
            }

            String id;
            if (config.contains("id")) {
                id = String.valueOf(config.get("id"));
            } else {
                throw new StructurePlanException("Missing 'id' node in " + structureYAML.getAbsolutePath());
            }
            String displayName;
            if (config.contains("displayname")) {
                displayName = String.valueOf(config.get("displayname"));
            } else {
                throw new StructurePlanException("Missing 'displayname' node");
            }

            spv = new StructurePlan(id, displayName, schematicStructureFile);

            if (config.contains("sign")) {
                String[] sign = config.getString("sign").split("\\s");
                if (sign.length != 3) {
                    throw new StructurePlanException("Invalid coordinates for sign in " + structureYAML.getAbsolutePath());
                } else {
                    try {
                        int x = Integer.parseInt(sign[0]);
                        int y = Integer.parseInt(sign[1]);
                        int z = Integer.parseInt(sign[2]);
                        spv.setSignLocation(x, y, z);
                    } catch (NumberFormatException nfe) {
                        throw new StructurePlanException("Invalid coordinates for sign in " + structureYAML.getAbsolutePath());
                    }
                }
            }

            if (config.contains("hide-sign-onComplete")) {
                spv.setHideSignOnComplete(config.getBoolean("hide-sign-onComplete"));
            }

//            if (config.contains("exits")) {
//                for (Object o : config.getList("exits")) {
//                    if (o instanceof List) {
//                        List l = (List) o;
//                        for (int i = 0; i < 3; i++) { // X Y Z
//                            System.out.println(l.get(i));
//                        }
//                    } else {
//                        throw new StructurePlanException("Invalid exits list");
//                    }
//                }
//            }
            if (config.contains("price")) {
                spv.setPrice(config.getDouble("price"));
            }

            if (config.contains("description")) {
                spv.setDescription(config.getString("description"));
            }

            if (config.contains("faction")) {
                spv.setFaction(config.getString("faction"));
            }

            if (config.contains("category")) {
                spv.setCategory(config.getString("category"));
            }

            if (config.contains("start-y")) {
                spv.setStartY(config.getInt("start-y"));
            }

            return spv;
        } catch (IOException | DataException ex) {
            LOGGER.error(ex);
        }
        return spv;
    }

    private class SchematicLoadThread implements Runnable {

        private final StructureSchematic schematic;
        private final SchematicCallback callback;

        public SchematicLoadThread(StructureSchematic schematic, SchematicCallback callback) {
            this.schematic = schematic;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic.getSchematic());
                callback.onComplete(schematic, cc);
            } catch (IOException | DataException ex) {
                java.util.logging.Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private interface SchematicCallback {

        void onComplete(StructureSchematic schematic, CuboidClipboard cc);
    }

}
