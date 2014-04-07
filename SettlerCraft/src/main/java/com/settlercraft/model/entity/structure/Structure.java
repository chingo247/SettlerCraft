/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.main.SettlerCraft;
import com.settlercraft.model.entity.StructureDimension;
import com.settlercraft.model.entity.WorldLocation;
import com.settlercraft.util.location.LocationUtil;
import com.settlercraft.util.location.LocationUtil.DIRECTION;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
@Entity
@Table(name = "sc_structure")
public class Structure implements Serializable {

  @Id
  @GeneratedValue
  private Long id;
  @NotNull
  private String owner;
  @NotNull
  @NotEmpty
  private String plan;

  @NotNull
  private int xMod;

  @NotNull
  private int zMod;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "chest_id")
  private StructureChest structureChest;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "sign_id")
  private StructureSign structureSign;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "progress")
  private StructureProgress progress;

  @Embedded
  private WorldLocation worldLocation;

  @Embedded
  private StructureDimension dimension;

  /**
   * JPA Constructor
   */
  protected Structure() {
  }

  /**
   * Constructor.
   *
   * @param owner The owner of this structure
   * @param target The start location of this structure
   * @param direction The direction of this structure
   * @param plan
   */
  public Structure(Player owner, Location target, DIRECTION direction, StructurePlan plan) {
    Preconditions.checkNotNull(plan);
    Preconditions.checkNotNull(target);
    this.owner = owner.getName();
    this.plan = plan.getConfig().getName();
    int[] modifiers = LocationUtil.getModifiers(direction);
    this.xMod = modifiers[0];
    this.zMod = modifiers[1];
    this.worldLocation = new WorldLocation(target);
    this.dimension = new StructureDimension(this);
    this.progress = new StructureProgress(this);
  }

  /**
   * Gets the id of this structure
   *
   * @return The id
   */
  public Long getId() {
    return id;
  }

  public StructureProgress getProgress() {
    return progress;
  }

  /**
   * Gets the name of the owner of this structure Owner may be a Player or NPC
   *
   * @return The owner of this structure
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Retrieves the plan of this structure
   *
   * @return The structure plan
   */
  public StructurePlan getPlan() {
    return SettlerCraft.getStructurePlanRegister().getPlan(plan);
  }

  /**
   * Sets the owner of this structure Use a Structure to handle this as a transaction!
   *
   * @param owner The new owner of this structure
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * Gets the structureChest that handles the build progress of this structure
   *
   * @return The structureChest
   */
  public StructureChest getStructureChest() {
    return structureChest;
  }

  /**
   * Gets the structureSign of this structure
   *
   * @return The structureSign
   */
  public StructureSign getStructureSign() {
    return structureSign;
  }

  /**
   * Gets the xMod of this building to determine the direction
   *
   * @return The xMod
   */
  public int getxMod() {
    return xMod;
  }

  /**
   * Gets the zMod of this building to determine the direction
   *
   * @return The zMod of this building
   */
  public int getzMod() {
    return zMod;
  }

  /**
   * Gets the direction (NORTH|EAST|SOUTH|WEST) of this building
   *
   * @return The direction
   */
  public DIRECTION getDirection() {
    return LocationUtil.getDirection(xMod, zMod);
  }

  /**
   * Sets the structureChest of this structure
   *
   * @param structureChest The structureChest
   */
  public void setStructureChest(StructureChest structureChest) {
    this.structureChest = structureChest;
  }

  /**
   * Sets the structureSign of this structure
   *
   * @param structureSign The structureSign
   */
  public void setStructureSign(StructureSign structureSign) {
    this.structureSign = structureSign;
  }

  /**
   * Gets the dimension of this building
   *
   * @return The dimension of this building.
   */
  public StructureDimension getDimension() {
    return dimension;
  }

  /**
   * Gets the actual location of the start of this building
   *
   * @return The building location
   */
  public Location getStructureLocation() {
    return new Location(Bukkit.getWorld(worldLocation.getWorld()), worldLocation.getX(), worldLocation.getY(), worldLocation.getZ());
  }

  /**
   * Gets the start location of this stucture, this includes any reserved sides
   *
   * @return The startlocation of this structure
   */
  public Location getStartLocation() {
    return new Location(Bukkit.getWorld(worldLocation.getWorld()), dimension.getStartX(), dimension.getStartY(), dimension.getStartZ());
  }

  @Override
  public String toString() {
    return "id:" + getId() + " owner:" + getOwner() + " plan:" + getPlan() + " x:" + dimension.getStartX() + " y:" + dimension.getStartY() + " z:" + dimension.getStartZ();
  }

}
