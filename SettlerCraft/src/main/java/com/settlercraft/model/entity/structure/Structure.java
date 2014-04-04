/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.StructurePlanRegister;
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

  @NotNull
  private int currentLayer;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "chest_id")
  private StructureChest structureChest;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "sign_id")
  private StructureSign structureSign;

  @Embedded
  private WorldLocation wlocation;

  public Structure() {
  }

  public Structure(Player owner, Location target, DIRECTION direction, String plan) {
    Preconditions.checkNotNull(StructurePlanRegister.getPlan(plan));
    Preconditions.checkNotNull(target);
    this.owner = owner.getName();
    this.plan = plan;
    this.currentLayer = 0;
    this.wlocation = new WorldLocation(target);
    int[] modifiers = LocationUtil.getModifiers(direction);
    this.xMod = modifiers[0];
    this.zMod = modifiers[1];
  }

  public Long getId() {
    return id;
  }

  public String getOwner() {
    return owner;
  }

  public String getPlan() {
    return plan;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public StructureChest getStructureChest() {
    return structureChest;
  }

  public StructureSign getStructureSign() {
    return structureSign;
  }

  public void setStructureChest(StructureChest structureChest) {
    this.structureChest = structureChest;
  }

  public void setStructureSign(StructureSign structureSign) {
    this.structureSign = structureSign;
  }

  public int getCurrentLayer() {
    return currentLayer;
  }

  public void setCurrentLayer(int currentLayer) {
    this.currentLayer = currentLayer;
  }

  public int getxMod() {
    return xMod;
  }

  public int getzMod() {
    return zMod;
  }

  public DIRECTION getDirection() {
    return LocationUtil.getDirection(xMod, zMod);
  }

  public Location getLocation() {
    return new Location(Bukkit.getWorld(wlocation.getWorld()), wlocation.getX(), wlocation.getY(), wlocation.getZ());
  }

  @Override
  public String toString() {
    return "id:" + getId() + " owner:" + getOwner() + " plan:" + getPlan() + " x:" + wlocation.getX() + " y:" + wlocation.getY() + " z:" + wlocation.getZ();
  }

}
