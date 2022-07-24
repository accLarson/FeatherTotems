package com.zerek.feathertotems;

import com.zerek.feathertotems.listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class FeatherTotems extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this),this);
        getServer().getPluginManager().registerEvents(new CreatureSpawnListener(this),this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(),this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this),this);
        getServer().getPluginManager().registerEvents(new EntityTargetLivingEntityListener(),this);
        getServer().getPluginManager().registerEvents(new EntitySpellCastListener(),this);
        getServer().getPluginManager().registerEvents(new EntityResurrectListener(this),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
