package de.chaos.listeners

import com.typewritermc.core.entries.Query
import de.chaos.entries.statics.CustomItemEntry
import de.chaos.item.ItemTypeSword
import de.chaos.item.swordComponents.SwordComponent
import de.chaos.logic.DamageIndicator
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import kotlin.random.Random

class SwordDamageListener(private val plugin: Plugin) : Listener {

    private val damageIndicator = DamageIndicator(plugin)

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return
        val hitEntity = event.entity as? LivingEntity ?: return
        val item: ItemStack = player.inventory.itemInMainHand
        if (!item.hasItemMeta()) return

        val swordId = ItemTypeSword.getSwordID(item)
        if (swordId == null) {
            return
        }
        val entry = Query.findById<CustomItemEntry>(swordId)
        if (entry == null) {
            player.sendMessage("§cUngültige Schwert-Eintrag-ID: $swordId")
            return
        }

        // Fetch stats from the entry’s item definition (not from PDC)
        val customItem = entry.customItem as? ItemTypeSword ?: return

        val isCrit = Random.nextDouble(0.0, 1.0) < customItem.basecritChance
        var damage = customItem.baseDamage
        if (isCrit) {
            damage *= customItem.basecritMultiplier
        }

        // Execute components
        customItem.components.forEach { component: SwordComponent ->
            component.execute(player, item, damage, hitEntity)
        }

        event.damage = damage

        val targetEntity = event.entity
        if (targetEntity is LivingEntity) {
            damageIndicator.showDamage(
                location = targetEntity.location,
                damage = damage,
                isCrit = isCrit
            )
        }
    }

    companion object {
        fun register(plugin: Plugin): SwordDamageListener {
            val listener = SwordDamageListener(plugin)
            Bukkit.getPluginManager().registerEvents(listener, plugin)
            return listener
        }
    }
}
