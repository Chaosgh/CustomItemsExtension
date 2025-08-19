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
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

@AlgebraicTypeInfo("multishot", Colors.BLUE, "fa6-solid:hashtag")
class BowMultiShot(
    private val arrowAmount: Int = 2,
    private val spreadAngle: Double = 15.0
) : BowComponent {

    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun onShoot(player: Player, bow: ItemStack, event: EntityShootBowEvent) {
        if (arrowAmount < 2) return

        val originalArrow = event.projectile as? Arrow ?: return
        val originalVelocity = originalArrow.velocity.clone()

        val spreadRadians = Math.toRadians(spreadAngle)
        val angleStep = spreadRadians / (arrowAmount - 1)

        for (i in 0 until arrowAmount) {
            if (i == arrowAmount / 2) continue

            val rotationAngle = -spreadRadians / 2.0 + i * angleStep
            val newVelocity = rotateAroundY(originalVelocity.clone(), rotationAngle)

            val newArrow = player.launchProjectile(Arrow::class.java)
            newArrow.velocity = newVelocity
            newArrow.teleport(originalArrow.location)

            copyArrowProperties(originalArrow, newArrow)
        }
    }

    private fun copyArrowProperties(from: Arrow, to: Arrow) {
        to.pickupStatus = from.pickupStatus
        to.isCritical = from.isCritical
        to.knockbackStrength = from.knockbackStrength

        val fromPdc = from.persistentDataContainer
        val toPdc = to.persistentDataContainer

        val bowId = fromPdc.get(ItemTypeBow.ENTRY_ID, PersistentDataType.STRING)
        if (bowId != null) {
            toPdc.set(ItemTypeBow.ENTRY_ID, PersistentDataType.STRING, bowId)
        }
    }

    private fun rotateAroundY(vector: Vector, angle: Double): Vector {
        val cos = cos(angle)
        val sin = sin(angle)
        val x = vector.x * cos - vector.z * sin
        val z = vector.x * sin + vector.z * cos
        return Vector(x, vector.y, z)
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
