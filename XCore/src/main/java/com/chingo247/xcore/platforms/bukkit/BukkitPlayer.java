
package com.chingo247.xcore.platforms.bukkit;

import com.chingo247.xcore.core.IPlayer;
import com.chingo247.xcore.core.IServer;
import com.chingo247.xcore.core.IWorld;
import java.util.UUID;
import org.bukkit.entity.Player;


/**
 *
 * @author Chingo
 */
public class BukkitPlayer implements IPlayer {
    
    private final Player player;
    private final IServer server;

    public BukkitPlayer(Player player, IServer server) {
        this.player = player;
        this.server = server;
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
    
    
   
    
    
    
}
