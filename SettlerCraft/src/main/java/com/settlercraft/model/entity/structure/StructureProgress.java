/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.settlercraft.model.plan.requirement.material.LayerRequirement;
import com.settlercraft.model.plan.schematic.ResourceMaterial;
import com.settlercraft.persistence.StructureProgressService;
import java.io.Serializable;
import java.util.HashMap;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.Material;
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

    @Embedded
    private LayerRequirement layerRequirement;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "structure")
    private Structure structure;

    private boolean inProgress;

    /**
     * JPA Constructor.
     */
    protected StructureProgress() {
    }

    public Long getId() {
        return id;
    }

    /**
     * Constructor.
     *
     * @param structure The structure
     */
    public StructureProgress(Structure structure) {
        this.layerRequirement = structure.getPlan().getRequirement().getMaterialRequirement().getLayer(0);
        this.structure = structure;
        this.inProgress = false;
    }

    public LayerRequirement getResources() {
        return this.layerRequirement;
    }

    public boolean setNext() {
        if (!layerRequirement.getResources().isEmpty() || !layerRequirement.getSpecialResources().isEmpty()) {
            throw new AssertionError("Can't move to next stage when layer requirements have not been met");
        }

        if (!hasNext()) {
            return false;
        } else {
            this.layerRequirement = structure.getPlan().getRequirement().getMaterialRequirement().getLayer(layerRequirement.getLayer() + 1);
            return true;
        }
    }

    public boolean hasNext() {
        return layerRequirement.getLayer() < structure.getPlan().getSchematic().layers;
    }

    /**
     * @param stack The stack
     */
    public void commit(ItemStack stack) {
        commit(stack, stack.getAmount());
    }

    /**
     * @param stack The stack
     * @param amount The amount
     * @return true when itemStack was needed and commited
     */
    public boolean commit(ItemStack stack, int amount) {
        if (stack == null || stack.getAmount() == 0 || amount == 0) {
            return false;
        }

        ResourceMaterial resourceMat = new ResourceMaterial(stack.getType(), stack.getData().getData());
        boolean succes = false;
        synchronized (this) {
            if (layerRequirement.getSpecialResources().containsKey(resourceMat)) {
                succes = true;
                HashMap<ResourceMaterial, Integer> srm = layerRequirement.getSpecialResources();

//            int a = Math.min(amount, Math.min(stack.getAmount(), srm.get(resourceMat)));
                srm.put(resourceMat, srm.get(resourceMat) - amount);

                if (srm.get(resourceMat) <= 0) {
                    srm.remove(resourceMat);
                }
                StructureProgressService sps = new StructureProgressService();
                sps.merge(this);
            } else if (layerRequirement.getResources().containsKey(resourceMat.getMaterial())) {
                succes = true;
                HashMap<Material, Integer> srm = layerRequirement.getResources();
//            int a = Math.min(amount, Math.min(stack.getAmount(), srm.get(resourceMat.getMaterial())));

                srm.put(resourceMat.getMaterial(), srm.get(resourceMat.getMaterial()) - amount);

                if (srm.get(resourceMat.getMaterial()) <= 0) {
                    srm.remove(resourceMat.getMaterial());
                }

                StructureProgressService sps = new StructureProgressService();
                sps.merge(this);
            }
        }
        if (succes) {
            System.out.println(layerRequirement);
        }

        return succes;
    }

    public boolean isNeeded(ResourceMaterial rm) {
        return layerRequirement.getResources().containsKey(rm.getMaterial())
                || layerRequirement.getSpecialResources().containsKey(rm);
    }

    public boolean isNeeded(ItemStack stack) {
        return isNeeded(new ResourceMaterial(stack.getType(), stack.getData().getData()));
    }

    public int getNeed(ItemStack stack) {
        return getNeed(new ResourceMaterial(stack.getType(), stack.getData().getData()));
    }

    public int getNeed(ResourceMaterial material) {
        if (!isNeeded(material)) {
            return 0;
        } else if (layerRequirement.getSpecialResources().containsKey(material)) {
            return layerRequirement.getSpecialResources().get(material);
        } else {
            return layerRequirement.getResources().get(material.getMaterial());
        }
    }

    public Structure getStructure() {
        return structure;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public boolean isInProgress() {
        return inProgress;
    }

}
