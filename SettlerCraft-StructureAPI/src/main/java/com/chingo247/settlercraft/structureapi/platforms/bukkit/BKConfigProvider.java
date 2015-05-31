package com.chingo247.settlercraft.structureapi.platforms.bukkit;

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
   

    private final File file = new File(BKStructureAPIPlugin.getInstance().getDataFolder(), "config.yml");

    public void load() throws SettlerCraftException {

        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.menuEnabled = config.getBoolean("menus.planmenu");
        this.shopEnabled = config.getBoolean("menus.planshop");
        this.useHolograms = config.getBoolean("structure.holograms.enabled");
        this.allowsSubstructures = config.getBoolean("allow-substructures");
       
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

   

}
