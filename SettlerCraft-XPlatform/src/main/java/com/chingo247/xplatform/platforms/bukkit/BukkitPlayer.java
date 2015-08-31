
package com.chingo247.xplatform.platforms.bukkit;

import com.chingo247.xplatform.core.AInventory;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.xplatform.core.IServer;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.base.Preconditions;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;


/**
 *
 * @author Chingo
 */
public class BukkitPlayer implements IPlayer {
    
    private final Player player;
    private final IServer server;

    public BukkitPlayer(Player player) {
        Preconditions.checkNotNull(player, "Player was null!");
        this.player = player;
        this.server = new BukkitServer(Bukkit.getServer());
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public IWorld getWorld() {
        return server.getWorld(player.getWorld().getUID());
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendMessage(String... message) {
        player.sendMessage(message);
    }

    @Override
    public float getYaw() {
        return player.getLocation().getYaw();
    }

    @Override
    public int getLevel() {
        return player.getLevel();
    }

    @Override
    public int getExperience() {
        return player.getTotalExperience();
    }

    @Override
    public boolean isOP() {
        return player.isOp();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public void updateInventory() {
        player.updateInventory();
    }

   

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    public AInventory getInventory() {
        return new BukkitInventory(player.getInventory());
    }

    @Override
    public  void openInventory(AInventory inventory) {
        Preconditions.checkArgument(inventory instanceof BukkitInventory);
        BukkitInventory binv = (BukkitInventory) inventory;
        player.openInventory(binv.getInventory());
    }

    @Override
    public ILocation getLocation() {
        Location l = player.getLocation();
        return new BukkitLocation(new BukkitWorld(player.getWorld()), l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    

   
  

    

    
    
    
    
   
    
    
    
}
