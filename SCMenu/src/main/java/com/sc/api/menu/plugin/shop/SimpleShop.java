package com.sc.api.menu.plugin.shop;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.sc.plugin.shop;
//
//import com.google.common.base.Preconditions;
//import com.google.common.collect.Maps;
//import java.util.Map;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.Inventory;
//import org.bukkit.inventory.ItemStack;
//
///**
// *
// * @author Chingo
// */
//public abstract class SimpleShop extends Shop {
//
//    protected Inventory templateInventory;
//    private final Map<String, Inventory> customers;
//
//    /**
//     * Constructor.
//     *
//     * @param title The title of this shop, must be unique
//     */
//    public SimpleShop(String title) {
//        this(title, false);
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param title The title of this shop, must be unique
//     * @param infinite If infinite items in this shop will/must never deplete Note: if infinite all
//     * pick actions on this shop's inventory will be cancelled
//     */
//    public SimpleShop(String title, boolean infinite) {
//        super(title, infinite);
//        this.templateInventory = Bukkit.createInventory(null, SHOPSIZE, title);
//        this.customers = Maps.newConcurrentMap();
//    }
//
//    @Override
//    public void setSlot(int slot, boolean reserve) {
//        if (reserve) {
//            reserved.add(slot);
//        } else {
//            reserved.remove(slot);
//        }
//    }
//
//    @Override
//    public void setColumn(int column, final boolean reserve) {
//        Preconditions.checkArgument(column >= 0 && column <= 8);
//        for (int i = column; i < 54; i += 9) {
//            setSlot(i, reserve);
//        }
//    }
//
//    @Override
//    public void setRow(int row, boolean reserve) {
//        Preconditions.checkArgument(row >= 0 && row <= 6);
//        int rowSize = 9;
//        for (int i = row + rowSize; i < row + rowSize; i++) {
//            setSlot(i, reserve);
//        }
//    }
//    
//
//    @Override
//    public boolean addItem(ItemStack item, double price) {
//        ShopSlot sp = new ShopSlot(getId(), item, price);
//        if (!isFull()) {
//            if (isInfinite()) {
//                item.setAmount(1); // Won't deplete!
//            }
//            for (int i = 0; i < SHOPSIZE; i++) {
//                if (!reserved.contains(i)) {
//                    if (templateInventory.getItem(i) == null) {
//                        templateInventory.setItem(i, sp);
//                        return true;
//                    }
//                } else {
//                    return false;
//                }
//            }
//        }
//        return false;
//    }
//
//
//    @Override
//    public boolean isFull() {
//        return (templateInventory.getContents().length == SHOPSIZE
//                || templateInventory.getContents().length + reserved.size() == SHOPSIZE);
//    }
//
//    public void setTemplateInventory(Inventory inventory) {
//        inventory = Bukkit.createInventory(null, SHOPSIZE, getTitle());
//        for (int i = 0; i < SHOPSIZE; i++) {
//            inventory.setItem(i, templateInventory.getItem(i));
//        }
//    }
//    
//    @Override
//    public Inventory getTemplateInventory() {
//        Inventory inv = Bukkit.createInventory(null, SHOPSIZE, getTitle());
//        for (int i = 0; i < SHOPSIZE; i++) {
//            inv.setItem(i, templateInventory.getItem(i));
//        }
//        return inv;
//    }
//    
//
//    @Override
//    public void visit(Player player) {
//        Inventory inv = getTemplateInventory();
//        customers.put(player.getName(), inv);
//        player.openInventory(inv); // Create a copy and show it to the player
//    }
//
//    @Override
//    public void leave(Player player) {
//        customers.remove(player.getName());
//    }
//
//    @Override
//    public void setTemplateInventory(Player player) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public boolean pay(Player player, ShopSlot item) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//
//    
//}
