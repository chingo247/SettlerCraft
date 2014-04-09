/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.google.common.base.Preconditions;
import com.settlercraft.model.entity.WorldLocation;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureChest implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "structure")
  private Structure mainStructure;

  @Embedded
  private WorldLocation wlocation;

  /**
   * Default JPA Constructor
   */
  protected StructureChest() {
  }

  /**
   * Constructor
   *
   * @param chestLocation The location of the chest
   * @param structure The structure this chest belongs to
   */
  StructureChest(Location chestLocation, Structure structure) {
    this.wlocation = new WorldLocation(chestLocation);
    Preconditions.checkArgument(chestLocation.getBlock().getType() == Material.CHEST);
    this.mainStructure = structure;
  }

  public Long getId() {
    return id;
  }

  public Structure getStructure() {
    return mainStructure;
  }

  public void setStructure(Structure structure) {
    this.mainStructure = structure;
  }

  public Chest getChest() {
    return (Chest) getLocation().getBlock().getState();
  }

  public Location getLocation() {
    return new Location(Bukkit.getWorld(wlocation.getWorld()), wlocation.getX(), wlocation.getY(), wlocation.getZ());
  }

  @Override
  public String toString() {
    return id + " : " + mainStructure.getPlan() + ": TYPE=CHEST";
  }

}
