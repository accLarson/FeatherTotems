package com.zerek.feathertotems.tasks;

import com.zerek.feathertotems.FeatherTotems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static org.bukkit.block.BlockFace.UP;

public class FightTask extends BukkitRunnable {

    private final FeatherTotems plugin;
    private final Illusioner illusioner;
    private List<Vex> vexes;
    private final Player summoner;
    private final BossBar bossBar;

    private final Random rand = new Random();

    private int timer = 16;
    private int vexTimer = 150;

    public FightTask(FeatherTotems plugin, Illusioner illusioner, List<Vex> vexes, Player summoner, BossBar bossBar) {
        this.plugin = plugin;
        this.illusioner = illusioner;
        this.vexes = vexes;
        this.summoner = summoner;
        this.bossBar = bossBar;
    }

    @Override
    public void run() {
        this.timer = this.timer - 1;
        this.vexTimer = this.vexTimer - 1;
        illusioner.setSpell(Spellcaster.Spell.NONE);
        vexes.removeIf(Vex::isDead);

        //end battle condition
        if (illusioner.isDead()) {
            new EndInitiateTask(plugin, vexes, bossBar).runTask(plugin);
            this.cancel();
            return;
        }

        //Update Boss Bar
        List<Entity> nearby = illusioner.getNearbyEntities(128.0, 128.0, 128.0).stream().filter(e -> e instanceof Player).collect(Collectors.toList());
        bossBar.getPlayers().stream().filter(p -> !nearby.contains(p)).forEach(bossBar::removePlayer);
        nearby.stream().filter(p -> !bossBar.getPlayers().contains(p)).forEach(p -> bossBar.addPlayer((Player) p));
        bossBar.setTitle(illusioner.getName());
        bossBar.setProgress(illusioner.getHealth() / illusioner.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        // Teleport illusioner
        if (timer == 0 || illusioner.getTarget() == null) {
            if (!teleportIllusioner()) {
                bossBar.getPlayers().forEach(p -> p.sendActionBar(text("The illusioner has escaped to the void!")));
                illusioner.remove();
            }
            //reset timer
            this.timer = 16;
        }


        //top up vexes
        if (vexTimer == 0) {
            if (vexes.size() < 5) {
                Location spawnLocation = illusioner.getLocation();
                spawnLocation.setY(spawnLocation.getY() + 3);
                while (vexes.size() < 10) {
                    illusioner.setSpell(Spellcaster.Spell.SUMMON_VEX);
                    Vex vex = (Vex) illusioner.getWorld().spawnEntity(spawnLocation, EntityType.VEX);
                    vexes.add(vex);
                    plugin.getServer().playSound(Sound.sound(Key.key("minecraft:block.beacon.activate"), Sound.Source.HOSTILE, 4f, 1.5f), illusioner.getLocation().getX(),illusioner.getLocation().getY(),illusioner.getLocation().getZ());
                }
            }
            this.vexTimer = 150;
        }
    }
    private boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    private boolean teleportIllusioner(){
        int attempts = 0;
        List<Player> fighters = bossBar.getPlayers().stream().filter(p -> p.getGameMode().toString().equals("SURVIVAL")).filter(p -> !isVanished(p)).collect(Collectors.toList());
        if (fighters.size() == 0) return false;

        while (attempts < 32) {
            Player target = fighters.get(rand.nextInt(fighters.size()));
            int playerX = target.getLocation().getBlockX();
            int playerY = target.getLocation().getBlockY();
            int playerZ = target.getLocation().getBlockZ();
            int teleportX = playerX + rand.nextInt(24) - 12;
            int teleportZ = playerZ + rand.nextInt(24) - 12;
            int teleportY = playerY;
            int i = 1;
            Block b = illusioner.getWorld().getBlockAt(teleportX, teleportY, teleportZ);
            boolean isValidBlock = false;
            while (i <= 21 && !isValidBlock) {
                if(i == 21 && (illusioner.getWorld().getHighestBlockYAt(teleportX,teleportZ) < target.getLocation().getY())) {
                    teleportY = illusioner.getWorld().getHighestBlockYAt(teleportX,teleportZ);
                    b = illusioner.getWorld().getBlockAt(teleportX, teleportY, teleportZ);
                }
                if (!(b.getType().isSolid()) || !(b.getRelative(UP,1).getType().isAir()) || !(b.getRelative(UP,2).getType().isAir())){
                    if (i % 2 == 0) teleportY += i;
                    else teleportY -= i;
                    b = illusioner.getWorld().getBlockAt(teleportX, teleportY, teleportZ);
                    i += 1;
                } else isValidBlock = true;
            }

            if (isValidBlock){
                Location newLoc = new Location(illusioner.getWorld(), teleportX + 0.5, teleportY + 1.5, teleportZ + 0.5);
                if (newLoc.distance(illusioner.getLocation()) <= 128) {
                    newLoc.setYaw(target.getLocation().getYaw() + 180);
                    illusioner.teleport(newLoc);
                    Location loc = illusioner.getLocation();
                    plugin.getServer().playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.HOSTILE, 1f, 0.6f), loc.getX(), loc.getY(), loc.getZ());
                    target.playSound(Sound.sound(Key.key("minecraft:entity.enderman.teleport"), Sound.Source.HOSTILE, 1f, 0.6f));
                    return true;
                }
            }
            attempts += 1;
        }
        return false;
    }
}