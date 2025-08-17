package de.chaos.item.armorComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

@AlgebraicTypeInfo("shockwave", Colors.BLUE, "fa6-solid:hashtag")
class ArmorShockwave(
    val pushRange: Double = 5.0,
    val activationChance: Double = 0.5,
    val pushStrength: Double = 1.5,
    val upwardForce: Double = 0.4
) : ArmorComponent {

    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }



    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun getFactoryString(): String {
        return ""
    }

    override fun onHit(player: Player, event: EntityDamageByEntityEvent) {
        if (Random.nextDouble() >= activationChance) {
            return
        }

        val nearbyEntities = player.getNearbyEntities(pushRange, pushRange, pushRange)

        for (entity in nearbyEntities) {
            if (entity is LivingEntity && entity != player) {
                val direction = entity.location.toVector().subtract(player.location.toVector())


                if (direction.lengthSquared() < 0.0001) {
                    continue
                }

                direction.normalize()

                direction.setY(upwardForce)
                direction.multiply(pushStrength)

                entity.velocity = entity.velocity.add(direction)
            }
        }
    }
}