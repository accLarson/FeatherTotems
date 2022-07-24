package com.zerek.feathertotems.tasks;

import com.zerek.feathertotems.FeatherTotems;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FightInitiateTask extends BukkitRunnable {

    private final FeatherTotems plugin;
    private final Illusioner illusioner;
    private final Location loc;
    private final BossBar bossBar;
    private List<Vex> vexes;
    private final Player summoner;

    public FightInitiateTask(FeatherTotems plugin, Illusioner illusioner, Location loc, BossBar bossBar, List<Vex> vexes, Player summoner) {
        this.plugin = plugin;
        this.illusioner = illusioner;
        this.loc = loc;
        this.bossBar = bossBar;
        this.vexes = vexes;
        this.summoner = summoner;
    }

    @Override
    public void run() {
        illusioner.setInvisible(false);
        illusioner.setInvulnerable(false);
        illusioner.setAI(true);
        new FightTask(plugin,illusioner,vexes,summoner, bossBar).runTaskTimer(plugin,20L, 20L);
    }
}
