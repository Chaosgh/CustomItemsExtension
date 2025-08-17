package de.chaos.listeners

import com.typewritermc.core.entries.Query
import de.chaos.entries.statics.CustomItemEntry
import de.chaos.item.ItemTypeArmor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.plugin.Plugin

class ArmorHitListener(private val plugin: Plugin) : Listener {

    @EventHandler
    fun onPlayerHit(event: EntityDamageByEntityEvent) {
        val player = event.entity as? Player ?: return

        for (armorPiece in player.inventory.armorContents) {
            if (armorPiece == null || armorPiece.type == Material.AIR) {
                continue
            }

            val armorId = ItemTypeArmor.getArmorID(armorPiece) ?: continue

            val entry = Query.findById<CustomItemEntry>(armorId) ?: continue
            val customArmor = entry.customItem as? ItemTypeArmor ?: continue

            customArmor.components.forEach { component ->
                component.onHit(player, event)
            }
        }
    }

    companion object {
        fun register(plugin: Plugin): ArmorHitListener {
            val listener = ArmorHitListener(plugin)
            Bukkit.getPluginManager().registerEvents(listener, plugin)
            return listener
        }
    }
}