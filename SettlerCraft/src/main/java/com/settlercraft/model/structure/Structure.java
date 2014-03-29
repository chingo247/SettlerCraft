/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.main.StructurePlanRegister;
import static com.settlercraft.util.LocationUtil.DIRECTION;
import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private long id;
    @NotNull
    private String owner;

    @OneToOne
    @NotNull
    private Builder builder;

    @NotNull
    @NotEmpty
    private String plan;

    @NotNull
    private DIRECTION direction;

    @NotNull
    private int xLoc;
    @NotNull
    private int yLoc;
    @NotNull
    private int zLoc;
    @NotNull
    @NotEmpty
    private String world;

    @NotNull
    @OneToOne
    private final StructureChest structureChest;

    @Embedded
    @NotNull
    private final StructureSign structureSign;

    public Structure(Player owner, Location target, DIRECTION direction, String plan) {
        Preconditions.checkNotNull(StructurePlanRegister.getPlan(plan));
        this.owner = owner.getName();
        this.xLoc = target.getBlockX();
        this.yLoc = target.getBlockY();
        this.zLoc = target.getBlockZ();
        this.world = target.getWorld().getName();
        this.plan = plan;
        this.direction = direction;
        this.builder = new Builder(plan, this);
        
        Location chestLocation = builder.placeProgressEntity(this.getLocation(), direction, 1, Material.CHEST); 
        Location signLocation = builder.placeProgressEntity(this.getLocation(), direction, 2, Material.SIGN_POST);
        structureChest = new StructureChest(chestLocation);
        structureSign = new StructureSign(signLocation);
    }

    public long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public StructurePlan getPlan() {
        return StructurePlanRegister.getPlan(plan);
    }

    public final Location getLocation() {
        return new Location(Bukkit.getWorld(world), xLoc, yLoc, zLoc);
    }

    public DIRECTION getDirection() {
        return direction;
    }

}
