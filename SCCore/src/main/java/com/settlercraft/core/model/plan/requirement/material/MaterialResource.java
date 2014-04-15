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
 * Structure resource contains a material and a value to describe the amount that is needed of
 * this material.
 * @author Chingo
 */
@Entity
public class MaterialResource implements Serializable {
  @Id
  @GeneratedValue
  protected Long id;
  
  /**
   * The material.
   */
  @NotNull
  protected final Material material;
  
  @NotNull
  @ManyToOne(cascade = CascadeType.ALL)
  protected StructureLayer requirement;
  
  /**
   * The value of this resource
   */
  protected int value;

  /**
   * JPA Constructor.
   */
  protected MaterialResource() {
    this.material = null;
  }

  /**
   * Constructor.
   * @param requirement The StructureLayer this StructureResource belongs to
   * @param material The material of this StructureResource
   * @param value The amount that is needed of this resource
   */
  public MaterialResource(StructureLayer requirement, Material material, int value) {
    this.material = material;
    this.value = value;
    this.requirement = requirement;
  }
  
  /**
   * Gets the id of this MaterialResource
   * @return 
   */
  public Long getId() {
    return id;
  }

  /** 
   * Gets the value
   * @return 
   */
  public int getValue() {
    return value;
  }

  /**
   * Sets the value of this resource
   * @param amount The value
   */
  public void setValue(int amount) {
    this.value = amount;
  }

  /**
   * Removes an amount of this resource, if the amount is higher than the value of this resource
   * the resource will be set to 0
   * @param amount The amount to substract from this resource
   */
  public void removeAmount(int amount) {
      this.value = value - Math.min(amount, value);
  }
  
  /**
   * Gets the material
   * @return The material
   */
  public Material getMaterial() {
    return material;
  }

  /**
   * Get the material resources of the currentLayer in progress
   * @return The material resources of the current layer in progress
   */
  public StructureLayer getStructureLayer() {
    return requirement;
  }

  @Override
   public String toString() {
    return "[Material: " + material + "] : " + value + "]";
  }
  
}
