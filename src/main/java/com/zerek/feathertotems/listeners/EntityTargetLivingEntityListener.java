package com.zerek.feathertotems.listeners;

import org.bukkit.entity.Illusioner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityTargetLivingEntityListener implements Listener {

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event){
        if (event.getTarget() instanceof Illusioner) event.setCancelled(true);
    }
}
