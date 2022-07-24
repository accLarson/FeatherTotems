package com.zerek.feathertotems.listeners;

import com.zerek.feathertotems.FeatherTotems;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;

public class EntityResurrectListener implements Listener {

    private final FeatherTotems plugin;
    private final String totemPopMessage, totemPopHasNameMessage, totemPopMurderMessage, totemPopHasNameMurderMessage;

    public EntityResurrectListener(FeatherTotems plugin) {
        this.plugin = plugin;
        this.totemPopMessage = this.plugin.getConfig().getString("messages.totem-pop");
        this.totemPopHasNameMessage = this.plugin.getConfig().getString("messages.totem-pop-has-name");
        this.totemPopMurderMessage = this.plugin.getConfig().getString("messages.totem-pop-murder");
        this.totemPopHasNameMurderMessage = this.plugin.getConfig().getString("messages.totem-pop-has-name-murder");
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent event) {
        if (!event.isCancelled()) {
            if (event.getEntity().getType() == EntityType.PLAYER) {
                Player resurrected = (Player) event.getEntity();

                // Set value for isMurder.
                boolean isMurder = resurrected.getKiller() != null;

                // Check which hand is holding the totem and set the totem.
                ItemStack totem = null;
                if (resurrected.getEquipment().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING) totem = resurrected.getEquipment().getItemInMainHand();
                else if (resurrected.getEquipment().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING) totem = resurrected.getEquipment().getItemInOffHand();

                // Set value for isNamed.
                boolean isNamed = totem.getItemMeta().hasDisplayName();

                if (isMurder)

                    if (isNamed) plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(totemPopHasNameMurderMessage,
                                Placeholder.unparsed("resurrected", resurrected.getName()),
                                Placeholder.unparsed("totem", PlainTextComponentSerializer.plainText().serialize(totem.getItemMeta().displayName())),
                                Placeholder.unparsed("killer", resurrected.getKiller().getName())));

                    else plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(totemPopMurderMessage,
                            Placeholder.unparsed("resurrected", resurrected.getName()),
                            Placeholder.unparsed("killer", resurrected.getKiller().getName())));

                else

                    if (isNamed) plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(totemPopHasNameMessage,
                            Placeholder.unparsed("resurrected", resurrected.getName()),
                            Placeholder.unparsed("totem", PlainTextComponentSerializer.plainText().serialize(totem.getItemMeta().displayName()))));

                    else plugin.getServer().broadcast(MiniMessage.miniMessage().deserialize(totemPopMessage,
                            Placeholder.unparsed("resurrected", resurrected.getName())));

            }
        }
    }
}