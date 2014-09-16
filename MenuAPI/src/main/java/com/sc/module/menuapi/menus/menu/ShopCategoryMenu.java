///*
// * Copyright (C) 2014 Chingo
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.sc.module.structureapi.menu;
//
//import com.sc.module.menuapi.menus.MenuSlot;
//import com.google.common.collect.Maps;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.Inventory;
//
///**
// *
// * @author Chingo
// */
//public class ShopCategoryMenu extends CategoryMenuV2 {
//
//    private final Map<String, List<MenuSlot>> items;
//    private final Map<String, Session> visitors;
//    private boolean chooseDefaultCategory = false;
//    private String defaultCategory;
//    private final HashSet<String> accepts = new HashSet<>();;
//
//    /**
//     * Constructor
//     *
//     * @param title The title of this shop
//     * @param wontDeplete If infinite items in this shop will/must never deplete Note: if infinite
//     * all pick actions on this shop's inventory will be cancelled
//     */
//    public ShopCategoryMenu(String title, boolean wontDeplete) {
//        super(title, wontDeplete);
//        this.items = Maps.newHashMap();
//        this.visitors = Collections.synchronizedMap(new HashMap<String, Session>());
//    }
//
//    public boolean accepts(String type) {
//        return accepts.add(type.toLowerCase());
//    }
//
//   
//
//    private class Session {
//
//        private String currentCategory;
//        private Inventory inventory;
//        private int currentPage = 0;
//        private Map<Integer, List<MenuSlot>> pages;
//
//        public Session(int currentPage, Inventory inventory, String currentCategory) {
//            this.inventory = inventory;
//            this.currentCategory = currentCategory;
//            this.pages = Maps.newHashMap();
//        }
//
//        public boolean hasNext() {
//            return pages.get(currentPage + 1) != null && !pages.get(currentPage + 1).isEmpty();
//        }
//
//        public boolean hasPrev() {
//            return currentPage > 0;
//        }
//
//        private int getFreeSlots() {
//            int slots = 0;
//            for (int i = 0; i < getMenuSlots().length; i++) {
//                if (getMenuSlots()[i] == null) {
//                    slots++;
//                }
//            }
//            return slots;
//        }
//
//        public void setPages(Player player, String category) {
//            this.currentPage = 0;
//            this.pages.clear();
//            if (category.equalsIgnoreCase(defaultCategory)) {
//                clearItemSlots();
//                List<MenuSlot> is = getItems();
//                if (is == null) {
//                    return;
//                }
//                Collections.sort(is, ALPHABETICAL_ORDER);
//                final int max = getFreeSlots();
////                System.out.println("max = " + max);
//                int current = 0;
//                for (int i = 0; i < is.size(); i++) {
//                    if (pages.get(current) == null) {
//                        pages.put(current, new ArrayList<MenuSlot>(max));
//                    }
//                    if (pages.get(current).size() == max) {
//                        current++;
//                        pages.put(current, new ArrayList<MenuSlot>(max));
//                    }
//                    pages.get(current).add(is.get(i));
////                    System.out.println("item: " + i);
//                }
//            } else {
//                clearItemSlots();
////                System.out.println("Category: " + getCategoryName(category));
//                List<MenuSlot> is = getItems(getCategoryName(category));
//
//                if (is == null) {
//                    return;
//                }
//                Collections.sort(is, ALPHABETICAL_ORDER);
//                final int max = getFreeSlots();
////                System.out.println("max = " + max);
//                int current = 0;
//                for (int i = 0; i < is.size(); i++) {
//                    if (pages.get(current) == null) {
//                        pages.put(current, new ArrayList<MenuSlot>(max));
//                    }
//                    if (pages.get(current).size() == max) {
//                        current++;
//                        pages.put(current, new ArrayList<MenuSlot>(max));
//                    }
//                    pages.get(current).add(is.get(i));
////                    System.out.println("item: " + i);
//                }
//            }
//        }
//
//    }
//
//}
