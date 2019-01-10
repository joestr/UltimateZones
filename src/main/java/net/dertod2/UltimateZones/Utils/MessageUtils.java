package net.dertod2.UltimateZones.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.dertod2.UltimateZones.Classes.Locale;
import net.dertod2.UltimateZones.Classes.RightResult;
import net.dertod2.UltimateZones.Classes.Zones.AbstractZone;
import net.dertod2.UltimateZones.Utils.ChatComponent.Hover;

public class MessageUtils {

    @SuppressWarnings("deprecation")
    public static void sendFailed(AbstractZone abstractZone, Player player, RightResult rightResult) {
        String synonym = abstractZone.hasSynonym() ? abstractZone.getSynonym() : String.valueOf(abstractZone.getId());

        if (rightResult.materialData != null) {
            Locale.json(player, "missingrights.withblock", rightResult.flag.name(), synonym, abstractZone.getId(),
                    abstractZone.getPlayer().getName(),
                    NMSHelper.getMojangIdentifierName(rightResult.materialData.toItemStack()),
                    NMSHelper.getMojangTranslatableName(rightResult.materialData.toItemStack()));
        } else {
            Locale.json(player, "missingrights.withblock", rightResult.flag.name(), synonym, abstractZone.getId(),
                    abstractZone.getPlayer().getName());
        }
    }

    public static void sendNegatedFailed(AbstractZone abstractZone, Player player, RightResult rightResult) {
        String synonym = abstractZone.hasSynonym() ? abstractZone.getSynonym() : String.valueOf(abstractZone.getId());

        Locale.json(player, "missingrights.negated", rightResult.flag.name(), synonym, abstractZone.getId(),
                abstractZone.getPlayer().getName());
    }

    public static ChatComponent createZoneName(AbstractZone abstractZone) {
        ChatComponent chatComponent;
        if (abstractZone.hasSynonym()) {
            chatComponent = new ChatComponent(abstractZone.getSynonym(), ChatColor.GOLD).hover(Hover.ShowText,
                    new ChatComponent("Id: " + abstractZone.getId()));
        } else {
            chatComponent = new ChatComponent(abstractZone.getId(), ChatColor.GOLD);
        }

        return chatComponent;
    }
}