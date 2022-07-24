package com.zerek.feathertotems.tasks;

import com.zerek.feathertotems.FeatherTotems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class SummoningInitiateTask extends BukkitRunnable {

    private final FeatherTotems plugin;
    private final Location loc;
    private final Player summoner;

    public SummoningInitiateTask(FeatherTotems plugin, Location loc, Player summoner) {
        this.plugin = plugin;
        this.loc = loc;
        this.summoner = summoner;
    }

    @Override
    public void run() {

        //create illusioner
        Illusioner illusioner = (Illusioner) loc.getWorld().spawnEntity(loc, EntityType.ILLUSIONER);
        illusioner.getEquipment().setHelmet(new ItemStack(Material.AIR));
        ItemStack bow = new ItemStack(Material.BOW,1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE,1);
        bow.addUnsafeEnchantment(Enchantment.DURABILITY,10);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE,128);
        illusioner.getEquipment().setItemInMainHand(bow);
        illusioner.getEquipment().setItemInMainHandDropChance(0);
        illusioner.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(640.0);
        illusioner.setHealth(640.0);
        illusioner.setRemoveWhenFarAway(true);
        illusioner.setSilent(true);
        illusioner.setAI(false);
        illusioner.setInvulnerable(true);
        illusioner.setInvisible(true);

        //create BossBar
        BossBar bossBar = plugin.getServer().createBossBar(
                illusioner.getName(),
                BarColor.BLUE,
                BarStyle.SOLID);
        bossBar.setProgress(0);
        List<Entity> nearby = illusioner.getNearbyEntities(128.0, 128.0, 128.0).stream().filter(e -> e instanceof Player).collect(Collectors.toList());
        for (Entity p : nearby) bossBar.addPlayer((Player) p);

        //call repeating task for summoning process
        new SummoningTask(plugin,illusioner,bossBar,summoner, loc).runTaskTimer(plugin,2,2);

        //initial effects
        loc.getWorld().spawnParticle(Particle.END_ROD,loc,100);
        summoner.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,125,100));

        //server-wide sounds
        plugin.getServer().playSound(Sound.sound(Key.key("minecraft:entity.evoker.prepare_wololo"), Sound.Source.HOSTILE,1f,0.1f));
        plugin.getServer().playSound(Sound.sound(Key.key("minecraft:entity.wither.spawn"), Sound.Source.HOSTILE,1f,0.5f));
    }
}
