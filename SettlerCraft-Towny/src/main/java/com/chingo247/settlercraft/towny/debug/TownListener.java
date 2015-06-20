/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.towny.debug;

import com.chingo247.settlercraft.towny.plugin.SettlerCraftTowny;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.NewTownEvent;
import com.palmergames.bukkit.towny.event.TownClaimEvent;
import com.palmergames.bukkit.towny.object.Coord;
import static com.palmergames.bukkit.towny.object.Coord.parseCoord;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.sk89q.worldedit.Vector2D;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Chingo
 */
public class TownListener implements Listener {

    @EventHandler
    public void onTownCreate(NewTownEvent townEvent) {

        Town t = townEvent.getTown();
        List<TownBlock> tbs = t.getTownBlocks();

        for (TownBlock tb : tbs) {
            paintBlock(tb);
        }
    }

    @EventHandler
    public void onPlotClaim(TownClaimEvent event) {
        paintBlock(event.getTownBlock());
    }

    private void paintBlock(final TownBlock block) {
        System.out.println("Paint block!");

        World w = Bukkit.getWorld(block.getWorld().getName());

        int blockSize = TownySettings.getTownBlockSize();
        Vector2D min = SettlerCraftTowny.translate(block.getWorldCoord());
        Vector2D max = min.add(blockSize, blockSize);

        System.out.println("min: " + min.getX() + ", " + min.getZ());
        System.out.println("max: " + max.getX() + ", " + max.getZ());

        System.out.println("Painting: " + w.getName());
        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                Block b = w.getHighestBlockAt(x, z);
                Block below = b.getRelative(BlockFace.DOWN);
                below.setType(Material.REDSTONE_BLOCK);
            }
        }

    }

    public static void main(String[] args) {
        Coord coord = parseCoord(16, 32);
        System.out.println(coord.getX() + " " + coord.getZ());
    }

}
