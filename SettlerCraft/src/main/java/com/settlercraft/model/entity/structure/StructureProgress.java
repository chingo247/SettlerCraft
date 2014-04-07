/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
@Entity
@Table(name = "Structure_Progress")
public class StructureProgress implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Basic
    @Column(name = "resources")
    private ArrayList<StructureResource> resources;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "structure")
    private Structure structure;

    @Column(name = "layer")
    private int currentLayer;

    public StructureProgress() {
    }

    public StructureProgress(Structure structure) {
        this.currentLayer = 0;
//        this.resources = new ArrayList<>(structure.getPlan().getRequirement().getResources().get(currentLayer));
        this.structure = structure;
    }

    public boolean processChest(StructureChest sc) {
        if (!sc.getStructure().getStructureChest().getId().equals(sc.getId())) {
            throw new IllegalArgumentException("invalid chest, doesnt belong to building");
        }
        Chest chest = sc.getChest();
        Inventory invent = chest.getBlockInventory();
        for (int i = 0; i < resources.size(); i++) {
            for (int j = 0; j < chest.getBlockInventory().getSize(); j++) {
                StructureResource sr = resources.get(i);
                ItemStack is = invent.getItem(j);
                if (is != null && is.getType() == sr.getMaterial()) {
                    int amount = Math.min(sr.getAmount(), is.getAmount());
                    chest.getBlockInventory().setItem(j, new ItemStack(is.getType(), is.getAmount() - amount));
                    sr.setAmount(sr.getAmount() - amount);
                    resources.set(i, sr);
                    print();
                    return true;
                }
            }
        }
        print();
        return false;
    }

    private void print() {
        for (StructureResource sr : resources) {
            System.out.println(sr);
        }
    }

    /**
     * Gets the currentLayer this structure is building
     *
     * @return the currentLayer
     */
    public int getCurrentLayer() {
        return currentLayer;
    }

    /**
     * Sets the currentLayer this structure is building
     *
     * @param currentLayer The currentLayer
     */
    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    public Long getId() {
        return id;
    }

}
