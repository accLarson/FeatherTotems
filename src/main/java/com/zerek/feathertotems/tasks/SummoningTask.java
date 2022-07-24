package com.zerek.feathertotems.tasks;

import com.zerek.feathertotems.FeatherTotems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class SummoningTask extends BukkitRunnable {

    private final FeatherTotems plugin;
    private final Illusioner illusioner;
    private BossBar bossBar;
    private final Player summoner;
    private final Location loc;

    public SummoningTask(FeatherTotems plugin, Illusioner illusioner, BossBar bossBar, Player summoner, Location loc) {
        this.plugin = plugin;
        this.illusioner = illusioner;
        this.bossBar = bossBar;
        this.summoner = summoner;
        this.loc = loc;
    }

    int timer = 100;
    double progress = 0.0;

    Particle.DustOptions blueDust = new Particle.DustOptions(Color.fromRGB(75, 175, 255), 1.0F);
    Particle.DustOptions blackDust = new Particle.DustOptions(Color.fromRGB(40, 40, 40), 2.0F);
    List<Vex> vexes = new ArrayList<>();

    @Override
    public void run() {
        this.timer = this.timer - 1;

        //end of task condition
        if (timer == 0) {
            new FightInitiateTask(plugin, illusioner, loc, bossBar, vexes, summoner).runTask(plugin);
            this.cancel();
            return;
        }


        //boss bar
        this.progress = progress + 1;
        bossBar.setProgress(progress / 100);
        List<Entity> nearby = illusioner.getNearbyEntities(128.0, 128.0, 128.0).stream().filter(e -> e instanceof Player).collect(Collectors.toList());
        bossBar.getPlayers().stream().filter(p -> !nearby.contains(p)).forEach(p -> bossBar.removePlayer(p));
        nearby.stream().filter(p -> !bossBar.getPlayers().contains(p)).forEach(p -> bossBar.addPlayer((Player) p));

        //action bar
        bossBar.getPlayers().forEach(p -> p.sendActionBar(text("The illusioner will escape to the void if left alone or trapped!")));

        //Vex
        Location spawnLocation = illusioner.getLocation();
        spawnLocation.setY(spawnLocation.getY() + 3);
        if (timer == 35){
            illusioner.setSpell(Spellcaster.Spell.SUMMON_VEX);
            plugin.getServer().playSound(Sound.sound(Key.key("minecraft:entity.player.breath"), Sound.Source.HOSTILE, 4f, 0.4f), illusioner.getLocation().getX(),illusioner.getLocation().getY(),illusioner.getLocation().getZ());
        }

        if (timer == 25) {
            plugin.getServer().playSound(Sound.sound(Key.key("minecraft:block.beacon.activate"), Sound.Source.HOSTILE, 4f, 1.5f), illusioner.getLocation().getX(),illusioner.getLocation().getY(),illusioner.getLocation().getZ());
            while (vexes.size() < 10) {
                Vex vex = (Vex) illusioner.getWorld().spawnEntity(spawnLocation, EntityType.VEX);
                vex.setTarget(summoner);
                vexes.add(vex);
            }
        }

        //head tilt and float
        Location newLocation = illusioner.getLocation();
        newLocation.setY(newLocation.getY() + 1f / (timer + 5f));
        newLocation.setPitch(-40 + timer);
        illusioner.teleport(newLocation);

        //particles
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 5, 2, 2, 2, blueDust);
        loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 15, 0.5, 0.5, 0.5, blackDust);
        loc.getWorld().spawnParticle(Particle.REVERSE_PORTAL, loc, 1, 1, 1, 1);
    }
}
