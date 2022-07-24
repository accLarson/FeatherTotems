package com.zerek.feathertotems.listeners;

import com.zerek.feathertotems.tasks.SummoningInitiateTask;
import com.zerek.feathertotems.FeatherTotems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Objects;

public class BlockPlaceListener implements Listener {

    private final FeatherTotems plugin;

    public BlockPlaceListener(FeatherTotems plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event){
        //confirm placed block is a player head mounted to the top of the block below it.
        if (event.getBlock().getType() == Material.PLAYER_HEAD) {
            Skull skull = (Skull) event.getBlock().getState();
            //plugin.getServer().broadcast(text(skull.getOwningPlayer().getUniqueId().toString()));
            try {
                //check that the head is of wandering trader variety
                if (Objects.equals(skull.getOwningPlayer().getUniqueId().toString(), "0d4cedf6-2299-35ce-861f-6b742018cbae")){

                    Block blockOne = event.getBlock().getRelative(BlockFace.DOWN,1);
                    Block blockTwo = event.getBlock().getRelative(BlockFace.DOWN,2);

                    //check that wandering trader head is being placed on 2 stacked lapis blocks
                    if (blockOne.getType() == Material.LAPIS_BLOCK && blockTwo.getType() == Material.LAPIS_BLOCK){
                        //clear the build
                        event.getBlock().setType(Material.AIR);
                        blockOne.setType(Material.AIR);
                        blockTwo.setType(Material.AIR);
                        //spawn illusioner
                        Location spawnLoc = new Location(blockTwo.getWorld(), blockTwo.getX() + 0.5, blockTwo.getY(), blockTwo.getZ() + 0.5);
                        spawnLoc.setYaw(event.getPlayer().getLocation().getYaw() + 180);
                        spawnLoc.setPitch(60);
                        new SummoningInitiateTask(plugin, spawnLoc, event.getPlayer()).runTask(plugin);
                    }
                }
            } catch (Exception ignored) {}
        }
    }
}

