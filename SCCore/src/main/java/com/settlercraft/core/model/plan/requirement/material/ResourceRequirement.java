/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.plan.requirement.material;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.bukkit.Material;

/**
 * Desribes the material and the amount
 * @author Chingo
 */
@Entity
public class ResourceRequirement implements Serializable {
  @Id
  @GeneratedValue
  protected Long id;
  
  @NotNull
  protected final Material material;
  
  @NotNull
  @ManyToOne(cascade = CascadeType.ALL)
  protected LayerRequirement layerRequirement;
  
  protected int amount;

  protected ResourceRequirement() {
    this.material = null;
  }

  public ResourceRequirement(LayerRequirement layerRequirement, Material material, int amount) {
    this.material = material;
    this.amount = amount;
    this.layerRequirement = layerRequirement;
  }
  
  public Long getId() {
    return id;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public Material getMaterial() {
    return material;
  }

  public LayerRequirement getLayerRequirement() {
    return layerRequirement;
  }

  @Override
   public String toString() {
    return "[Material: " + material + "] : " + amount + "]";
  }
  
  
  
  
  
  

  
}
