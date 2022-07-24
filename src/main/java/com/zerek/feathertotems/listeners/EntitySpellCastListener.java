package com.zerek.feathertotems.listeners;

import org.bukkit.entity.Illusioner;
import org.bukkit.entity.Spellcaster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpellCastEvent;

public class EntitySpellCastListener implements Listener {

    @EventHandler
    public void onEntitySpellCast(EntitySpellCastEvent event){
        if (event.getEntity() instanceof Illusioner && event.getSpell() == Spellcaster.Spell.BLINDNESS) event.setCancelled(true);
    }
}
