package de.chaos.item.bowComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import org.bukkit.NamespacedKey

@AlgebraicTypeInfo("multishot", Colors.BLUE, "fa6-solid:hashtag")
class BowMultiShot(
    private val arrowAmount: Int = 3,
    private val spreadAngle: Double = 15.0
) : BowComponent {

    private val keyMultiShot = NamespacedKey("chaos", "multi_shot")

    override fun build(itemStack: ItemStack): ItemStack = itemStack
    override fun execute(player: Player, itemStack: ItemStack) {}

    override fun onShoot(player: Player, bow: ItemStack, event: EntityShootBowEvent) {
        if (arrowAmount < 2) return

        val originalArrow = event.projectile as? Arrow ?: return
        val originalVelocity = originalArrow.velocity.clone()
        val spreadRadians = Math.toRadians(spreadAngle)
        val angleStep = spreadRadians / (arrowAmount - 1)

        // Keep the original in the middle
        val middleIndex = arrowAmount / 2

        for (i in 0 until arrowAmount) {
            if (i == middleIndex) continue // leave the original as-is

            val rotationAngle = -spreadRadians / 2.0 + i * angleStep
            val newVelocity = rotateAroundY(originalVelocity.clone(), rotationAngle)

            val newArrow = player.launchProjectile(Arrow::class.java)
            newArrow.teleport(originalArrow.location)
            newArrow.velocity = newVelocity
            newArrow.shooter = player

            // Mark as multishot
            newArrow.persistentDataContainer.set(keyMultiShot, PersistentDataType.BYTE, 1)

            copyArrowProperties(originalArrow, newArrow)
            // Copy all metadata INCLUDING tracer flag
            copyAllPdc(originalArrow.persistentDataContainer, newArrow.persistentDataContainer)

            // NEW: If original arrow has tracer flag, ensure new arrows get tracked immediately
            if (originalArrow.persistentDataContainer.has(BowTracer.KEY_TRACER, PersistentDataType.BYTE)) {
                // Access the pending tracer config and apply it to the new arrow
                BowTracer.pendingTracers[player.uniqueId]?.let { config ->
                    BowTracer.startArrowTracking(newArrow, player, config)
                }
            }
        }
    }

    private fun copyArrowProperties(from: Arrow, to: Arrow) {
        to.pickupStatus = from.pickupStatus
        to.isCritical = from.isCritical
        to.knockbackStrength = from.knockbackStrength
        to.pierceLevel = from.pierceLevel
        to.fireTicks = from.fireTicks
        runCatching { to.damage = from.damage }

        // Safely copy tipped arrow data
        runCatching {
            from.basePotionData?.let { to.basePotionData = it }
            from.color?.let { to.color = it }
        }

        runCatching { to.isShotFromCrossbow = from.isShotFromCrossbow }
        from.scoreboardTags.forEach { to.addScoreboardTag(it) }
    }

    private fun copyAllPdc(
        from: org.bukkit.persistence.PersistentDataContainer,
        to: org.bukkit.persistence.PersistentDataContainer
    ) {
        val types: List<PersistentDataType<*, *>> = listOf(
            PersistentDataType.STRING,
            PersistentDataType.BYTE,
            PersistentDataType.SHORT,
            PersistentDataType.INTEGER,
            PersistentDataType.LONG,
            PersistentDataType.FLOAT,
            PersistentDataType.DOUBLE,
            PersistentDataType.BYTE_ARRAY,
            PersistentDataType.INTEGER_ARRAY,
            PersistentDataType.LONG_ARRAY,
            PersistentDataType.TAG_CONTAINER,
            PersistentDataType.TAG_CONTAINER_ARRAY
        )
        for (key in from.keys) {
            for (type in types) {
                @Suppress("UNCHECKED_CAST")
                val typed = type as PersistentDataType<Any, Any>
                if (from.has(key, typed)) {
                    from.get(key, typed)?.let { to.set(key, typed, it) }
                    break
                }
            }
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
    ) {}
}