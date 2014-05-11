/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.listeners;

import com.sc.api.structure.construction.builder.SCStructureBuilder;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.world.Direction;
import com.sc.api.structure.persistence.StructurePlanService;
import com.sc.api.structure.util.WorldEditUtil;
import static com.sc.api.structure.util.WorldEditUtil.getLocalSession;
import com.sc.api.structure.util.WorldUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class StructurePlanListener implements Listener {

    private final JavaPlugin settlerCraft;

    public StructurePlanListener(JavaPlugin settlerCraft) {
        this.settlerCraft = settlerCraft;
    }

    /**
     * Places a structure on player's target location
     *
     * @param pie The playerInteractEvent
     */
    @EventHandler
    public void onPlayerPlaceStructure(PlayerInteractEvent pie) {
        if (pie.getItem() == null || pie.getItem().getType() != Material.PAPER) {
            return;
        }
        StructurePlanService service = new StructurePlanService();
        StructurePlan plan = service.getPlan(pie.getItem().getItemMeta().getDisplayName());
        Player player = pie.getPlayer();
        if (plan != null
                && pie.getClickedBlock() != null
                && pie.getClickedBlock().getType() != Material.AIR) {

            pie.setCancelled(true); // default action would break a block
            System.out.println("plan! " + plan.getDisplayName());
            LocalSession session = getLocalSession(pie.getPlayer());

            System.out.println(session.hasCUISupport());
            if (session.hasCUISupport()) {
                try {
                    handleCUIPlayerSelect(player, pie.getClickedBlock().getLocation(), plan, pie.getAction());
                }
                catch (IncompleteRegionException ex) {
                    Logger.getLogger(StructurePlanListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                handleSimplePlayerSelect(player, pie.getClickedBlock().getLocation(), plan, pie.getAction());
            }

        }
    }

    private void handleSimplePlayerSelect(Player player, org.bukkit.Location target, StructurePlan plan, Action action) {
        if (action == Action.LEFT_CLICK_BLOCK) {
            LocalWorld world = WorldEditUtil.getLocalWorld(player);
            int x = target.getBlockX();
            int y = target.getBlockY();
            int z = target.getBlockZ();
            Location location = new Location(world, WorldEditUtil.getBlockWorldVector(world, x, y, z));
            if (SCStructureBuilder.overlaps(location, WorldUtil.getDirection(player), plan)) {
                player.sendMessage("Structure overlaps another structure");
            } else {
                player.sendMessage(ChatColor.YELLOW + " Placing structure");
                SCStructureBuilder.placeStructure(player, location, WorldUtil.getDirection(player), plan);
            }
        }
    }

    private void handleCUIPlayerSelect(Player player, org.bukkit.Location target, StructurePlan plan, Action action) throws IncompleteRegionException {
        LocalWorld world = WorldEditUtil.getLocalWorld(player);
        int x = target.getBlockX();
        int y = target.getBlockY();
        int z = target.getBlockZ();
        Location location = new Location(world, WorldEditUtil.getBlockWorldVector(world, x, y, z));
        LocalSession session = WorldEditUtil.getLocalSession(player);
        Direction direction = WorldUtil.getDirection(player);

        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!session.getRegionSelector(world).isDefined()) {
                SCStructureBuilder.selectStructureArea(player, WorldUtil.getDirection(player), location, plan);
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            } else {
                CuboidRegion oldRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                SCStructureBuilder.selectStructureArea(player, WorldUtil.getDirection(player), location, plan);
                CuboidRegion newRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                if (oldRegion.getPos1().equals(newRegion.getPos1()) && oldRegion.getPos2().equals(newRegion.getPos2())) {
                    SCStructureBuilder.placeStructure(player, location, direction, plan);
                    session.getRegionSelector(world).clear();
                    session.dispatchCUISelection(WorldEditUtil.getLocalPlayer(player));
                } else {
                    SCStructureBuilder.selectStructureArea(player, WorldUtil.getDirection(player), location, plan);
                    player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                    player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
                }
            }
        } else {
            if (session.getRegionSelector(world).isDefined()) {
                session.getRegionSelector(world).clear();
                session.dispatchCUISelection(WorldEditUtil.getLocalPlayer(player));
                player.sendMessage("Cleared selection");
            }
        }

    }
}
