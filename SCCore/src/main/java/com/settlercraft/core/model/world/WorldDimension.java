
package com.settlercraft.core.model.world;

import com.avaje.ebean.validation.NotNull;
import com.google.common.base.Preconditions;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.util.WorldUtil;
import javax.persistence.Column;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Chingo
 */
public class WorldDimension {
    
    @NotNull
    @Column(name = "world")
    protected String world;

    @NotNull
    @Column(name = "startX")
    private int startX;

    @NotNull
    @Column(name = "startY")
    private int startY;

    @NotNull
    @Column(name = "startZ")
    private int startZ;

    @NotNull
    @Column(name = "endX")
    private int endX;

    @NotNull
    @Column(name = "endY")
    private int endY;

    @NotNull
    @Column(name = "endZ")
    private int endZ;

    
    /**
     * JPA Constructor.
     */
    protected WorldDimension() {}

    /**
     * Constructor.
     * @param structure The structure
     */
    public WorldDimension(Structure structure) {
        Direction direction = structure.getDirection();
        StructurePlan plan = structure.getPlan();
        Location s = structure.getLocation();
        Location e = getEnd(s, plan, direction);
        
        this.startX = Math.min(s.getBlockX(), e.getBlockX());
        this.startY = Math.min(s.getBlockY(), e.getBlockY());
        this.startZ = Math.min(s.getBlockZ(), e.getBlockZ());
        this.endX = Math.max(s.getBlockX(), e.getBlockX());
        this.endY = Math.max(s.getBlockY(), e.getBlockY());
        this.endZ = Math.max(s.getBlockZ(), e.getBlockZ());
        this.world = structure.getLocation().getWorld().getName();
        
    }
    
    public WorldDimension(Location start, Location end) {
        Preconditions.checkArgument(start.getWorld().getName().equals(end.getWorld().getName()));
        this.startX = Math.min(start.getBlockX(), end.getBlockX());
        this.startY = Math.min(start.getBlockY(), end.getBlockY());
        this.startZ = Math.min(start.getBlockZ(), end.getBlockZ());
        this.endX = Math.max(start.getBlockX(), end.getBlockX());
        this.endY = Math.max(start.getBlockY(), end.getBlockY());
        this.endZ = Math.max(start.getBlockZ(), end.getBlockZ());
        this.world = start.getWorld().getName();
    }
    
    

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStartZ() {
        return startZ;
    }

    public void setStartZ(int startZ) {
        this.startZ = startZ;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public int getEndZ() {
        return endZ;
    }

    public void setEndZ(int endZ) {
        this.endZ = endZ;
    }

    private Location getEnd(Location start, StructurePlan plan, Direction direction) {
        SchematicObject schem = plan.getStructureSchematic();

        // Calculate Building end
        Location target = new Location(start.getWorld(),
                start.getBlockX(), // x Structure End
                start.getBlockY(),
                start.getBlockZ());   // z Structure End

        int[] mods = WorldUtil.getModifiers(direction);
        int xMd = mods[0];
        int zMd = mods[1];
        Location loc;
        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
            loc = target.clone().add((schem.width - 1) * xMd, (schem.layers - 1), (schem.length - 1) * zMd);
        } else {
            loc = target.clone().add((schem.length - 1) * zMd, (schem.layers - 1), (schem.width - 1) * xMd);
        }
        return loc;
    }
    
    public Location getStart() {
        return new Location(Bukkit.getWorld(world), startX, startY, startZ);
    }

    public Location getEnd() {
        return new Location(Bukkit.getWorld(world), endX, endY, endZ);
    }
    
}
