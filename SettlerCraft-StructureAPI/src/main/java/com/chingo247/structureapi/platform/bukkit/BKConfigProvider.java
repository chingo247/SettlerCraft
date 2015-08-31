package com.chingo247.structureapi.platform.bukkit;

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
import com.chingo247.structureapi.platform.IConfigProvider;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class BKConfigProvider implements IConfigProvider {

    
    private boolean menuEnabled = false;
    private boolean shopEnabled = false;
    private boolean useHolograms = false;
    private boolean allowsSubstructures = false;
    private boolean protectStructures = false;
   

    private final File file = new File(BKStructureAPIPlugin.getInstance().getDataFolder(), "config.yml");

    public void load() throws SettlerCraftException {

        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.menuEnabled = config.getBoolean("menus.planmenu");
        this.shopEnabled = config.getBoolean("menus.planshop");
        this.useHolograms = config.getBoolean("structure.holograms.enabled");
        this.allowsSubstructures = config.getBoolean("structure.allow-substructures");
        this.protectStructures = config.getBoolean("structure.protected");
    }

  

    

    @Override
    public boolean isPlanMenuEnabled() {
        return menuEnabled;
    }

    @Override
    public boolean isPlanShopEnabled() {
        return shopEnabled;
    }

    

    @Override
    public boolean useHolograms() {
        return useHolograms;
    }

    

    @Override
    public boolean isSubstructuresAllowed() {
        return allowsSubstructures;
    }

    @Override
    public boolean protectesStructures() {
        return protectStructures;
    }

   

}
