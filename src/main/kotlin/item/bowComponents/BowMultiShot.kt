package de.chaos.item.bowComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.ItemTypeBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@AlgebraicTypeInfo("multishot", Colors.BLUE, "fa6-solid:hashtag")
class BowMultiShot(
    val arrowAmount: Int = 2,
    val spreadAngle: Double = 15.0
) : BowComponent {

    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }


    override fun onShoot(player: Player, bow: ItemStack, event: EntityShootBowEvent) {
        if (arrowAmount < 2) {
            return
        }

        val originalArrow = event.projectile as? Arrow ?: return
        val originalVelocity = originalArrow.velocity

        val spreadRadians = Math.toRadians(spreadAngle)


        val angleStep = spreadRadians / (arrowAmount - 1)

        for (i in 0 until arrowAmount) {
            val newArrow = player.launchProjectile(Arrow::class.java)
            copyArrowProperties(originalArrow, newArrow)


            val rotationAngle = -spreadRadians / 2.0 + i * angleStep

            val newVelocity = originalVelocity.clone()

            newVelocity.rotateAroundY(rotationAngle)

            newArrow.velocity = newVelocity
        }
    }

    private fun copyArrowProperties(from: Arrow, to: Arrow) {
        to.pickupStatus = from.pickupStatus
        to.isCritical = from.isCritical
        to.knockbackStrength = from.knockbackStrength

        val fromPdc = from.persistentDataContainer
        val toPdc = to.persistentDataContainer

        val bowId = fromPdc.get(ItemTypeBow.ENTRY_ID, PersistentDataType.STRING)
        val damage = fromPdc.get(ItemTypeBow.KEY_BASE_DAMAGE, PersistentDataType.DOUBLE)
        val critChance = fromPdc.get(ItemTypeBow.KEY_BASE_CRIT_CHANCE, PersistentDataType.DOUBLE)
        val critMultiplier = fromPdc.get(ItemTypeBow.KEY_BASE_CRIT_MULTIPLIER, PersistentDataType.DOUBLE)

        if (bowId != null && damage != null && critChance != null && critMultiplier != null) {
            toPdc.set(ItemTypeBow.ENTRY_ID, PersistentDataType.STRING, bowId)
            toPdc.set(ItemTypeBow.KEY_BASE_DAMAGE, PersistentDataType.DOUBLE, damage)
            toPdc.set(ItemTypeBow.KEY_BASE_CRIT_CHANCE, PersistentDataType.DOUBLE, critChance)
            toPdc.set(ItemTypeBow.KEY_BASE_CRIT_MULTIPLIER, PersistentDataType.DOUBLE, critMultiplier)
        }
    }

    override fun execute(
        player: Player,
        bow: ItemStack,
        damage: Double,
        hitEntity: LivingEntity,
        onDamageDealt: (LivingEntity, Double, Boolean) -> Unit
    ) {
    }
}