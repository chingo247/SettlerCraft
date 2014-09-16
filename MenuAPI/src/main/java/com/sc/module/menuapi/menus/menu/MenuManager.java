package com.sc.module.structureapi.menu;

import com.sc.module.menuapi.menus.menu.exception.DuplicateMenuException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

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
/**
 * @author Chingo
 */
public class MenuManager {

    private final Map<String, CategoryMenu> menus = Collections.synchronizedMap(new HashMap<String, CategoryMenu>());
    private final Map<UUID, Player> visiting = Collections.synchronizedMap(new HashMap<UUID, Player>());

    private static MenuManager instance;

    private MenuManager() {
    }

    public void putVisitor(Player player) {
        visiting.put(player.getUniqueId(), player);
    }

    public static MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }

    public void register(CategoryMenu menu) throws DuplicateMenuException {
        if (menus.containsKey(menu.getTitle())) {
            throw new DuplicateMenuException("Title of the menu has to be unique, " + menu.getTitle() + " already exists");
        }
        menus.put(menu.getTitle(), menu);
    }

    public boolean hasMenu(String menuTitle) {
        return menus.containsKey(menuTitle);
    }

    public CategoryMenu getMenu(String menuTitle) {
        return menus.get(menuTitle);
    }

    
}
