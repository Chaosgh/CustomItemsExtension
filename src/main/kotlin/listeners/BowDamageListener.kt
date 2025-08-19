package de.chaos.listeners

import com.typewritermc.core.entries.Query
import de.chaos.entries.statics.CustomItemEntry
import de.chaos.item.ItemTypeBow
import de.chaos.item.bowComponents.BowComponent
import de.chaos.logic.DamageIndicator
import org.bukkit.Bukkit
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import kotlin.random.Random

class BowDamageListener(private val plugin: Plugin) : Listener {

    private val damageIndicator = DamageIndicator(plugin)

    @EventHandler
    fun onBowShoot(event: EntityShootBowEvent) {
        val player = event.entity as? Player ?: return
        val arrow = event.projectile as? Arrow ?: return
        val bow = event.bow ?: return

        val bowId = ItemTypeBow.getBowID(bow) ?: return

        val entry = Query.findById<CustomItemEntry>(bowId) ?: return
        val customItem = entry.customItem as? ItemTypeBow ?: return

        // Only store entry id on the arrow now
        val pdc = arrow.persistentDataContainer
        pdc.set(ItemTypeBow.ENTRY_ID, PersistentDataType.STRING, bowId)

        customItem.components.forEach { component ->
            component.onShoot(player, bow, event)
        }
    }


    @EventHandler
    fun onProjectileHit(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return
        val hitBlock = event.hitBlock ?: return
        val player = arrow.shooter as? Player ?: return

        val bowId = arrow.persistentDataContainer.get(ItemTypeBow.ENTRY_ID, PersistentDataType.STRING) ?: return
        val entry = Query.findById<CustomItemEntry>(bowId) ?: return
        val customItem = entry.customItem as? ItemTypeBow ?: return

        val bowItem = player.inventory.itemInMainHand.takeIf { ItemTypeBow.getBowID(it) == bowId }
            ?: player.inventory.itemInOffHand.takeIf { ItemTypeBow.getBowID(it) == bowId }
            ?: return

        customItem.components.forEach { component ->
            component.executeOnBlockHit(player, bowItem, hitBlock.location) { target, damage, isCrit ->
                damageIndicator.showDamage(
                    location = target.location,
                    damage = damage,
                    isCrit = isCrit
                )
            }
        }

        arrow.remove()
    }


    @EventHandler
    fun onEntityDamageByArrow(event: EntityDamageByEntityEvent) {
        val arrow = event.damager as? Arrow ?: return
        val player = arrow.shooter as? Player ?: return
        val hitEntity = event.entity as? LivingEntity ?: return

        val arrowPdc = arrow.persistentDataContainer
        val bowId = arrowPdc.get(ItemTypeBow.ENTRY_ID, PersistentDataType.STRING) ?: return

        val entry = Query.findById<CustomItemEntry>(bowId)
        if (entry == null) {
            player.sendMessage("§cUngültige Bogen-Eintrag-ID: $bowId")
            return
        }

        val customItem = entry.customItem as? ItemTypeBow ?: return

        // Compute stats from the entry’s item definition (not from PDC)
        val isCrit = Random.nextDouble(0.0, 1.0) < customItem.basecritChance
        val damage = if (isCrit) customItem.baseDamage * customItem.basecritMultiplier else customItem.baseDamage

        // Execute components if player still holds corresponding bow (optional check preserved)
        val bowItem = player.inventory.itemInMainHand.takeIf { ItemTypeBow.getBowID(it) == bowId }
            ?: player.inventory.itemInOffHand.takeIf { ItemTypeBow.getBowID(it) == bowId }

        if (bowItem != null) {
            customItem.components.forEach { component: BowComponent ->
                component.execute(player, bowItem, damage, hitEntity) { target, dmg, crit ->
                    damageIndicator.showDamage(
                        location = target.location,
                        damage = dmg,
                        isCrit = crit
                    )
                }
            }
        }

        event.damage = damage

        damageIndicator.showDamage(
            location = hitEntity.location,
            damage = damage,
            isCrit = isCrit
        )
    }

    companion object {
        fun register(plugin: Plugin): BowDamageListener {
            val listener = BowDamageListener(plugin)
            Bukkit.getPluginManager().registerEvents(listener, plugin)
            return listener
        }
    }
}