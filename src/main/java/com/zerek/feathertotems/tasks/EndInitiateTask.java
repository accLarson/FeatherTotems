package com.zerek.feathertotems.tasks;

import com.zerek.feathertotems.FeatherTotems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Vex;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

public class EndInitiateTask extends BukkitRunnable {

    private List<Vex> vexes;

    private final FeatherTotems plugin;
    private final BossBar bossBar;

    public EndInitiateTask(FeatherTotems plugin, List<Vex> vexes, BossBar bossBar) {
        this.vexes = vexes;
        this.plugin = plugin;
        this.bossBar = bossBar;
    }

    @Override
    public void run() {
        bossBar.setProgress(0);
        if (!vexes.isEmpty()){
            Vex v = vexes.get(0);
            v.getWorld().spawnParticle(Particle.FLASH,v.getLocation(),20,0.125,0.125,0.125);
            Particle.DustOptions blackDust = new Particle.DustOptions(Color.fromRGB(40, 40, 40), 2.0F);
            v.getWorld().spawnParticle(Particle.REDSTONE,v.getLocation(),8,blackDust);
            Location loc = v.getLocation();
            plugin.getServer().playSound(Sound.sound(Key.key("minecraft:block.beacon.deactivate"), Sound.Source.HOSTILE,2f,1.5f), loc.getX(),loc.getY(),loc.getZ());

            vexes.remove(v);
            v.remove();

            Random rand = new Random();
            new EndInitiateTask(plugin,vexes, bossBar).runTaskLater(plugin,rand.nextInt(20));
        }
        else {
            bossBar.removeAll();
            this.cancel();
        }
    }
}
