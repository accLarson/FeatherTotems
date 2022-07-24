package com.zerek.feathertotems.listeners;

import org.bukkit.entity.Illusioner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class EntityDamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Illusioner){
            if (event instanceof EntityDamageByBlockEvent) event.setCancelled(true);
            else if (Arrays.asList("ENTITY_EXPLOSION", "WITHER","FALL").contains(event.getCause().toString())) event.setCancelled(true);
        }
    }
}
