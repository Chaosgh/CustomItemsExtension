package de.chaos.item.bowComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("bow explosive", Colors.BLUE, "fa6-solid:hashtag")
class BowExplosive(
    val radius: Double = 3.0,
    val damage: Double = 5.0,
    val knockbackStrength: Double = 1.2,
    val upwardForce: Double = 0.5
) : BowComponent {

    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {

    }

    override fun execute(
        player: Player,
        bow: ItemStack,
        damage: Double,
        hitEntity: LivingEntity,
        onDamageDealt: (target: LivingEntity, damage: Double, isCrit: Boolean) -> Unit
    ) {
        createExplosion(player, hitEntity, onDamageDealt)
    }

    override fun executeOnBlockHit(
        player: Player,
        bow: ItemStack,
        location: Location,
        onDamageDealt: (target: LivingEntity, damage: Double, isCrit: Boolean) -> Unit
    ) {
        createExplosion(player, location, onDamageDealt)
    }

    private fun createExplosion(
        player: Player,
        center: Location,
        onDamageDealt: (target: LivingEntity, damage: Double, isCrit: Boolean) -> Unit,
        primaryTarget: LivingEntity? = null
    ) {
        val world = center.world ?: return

        world.spawnParticle(Particle.EXPLOSION, center, 1)
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f)

        val nearbyEntities = world.getNearbyEntities(center, this.radius, this.radius, this.radius)

        for (entity in nearbyEntities) {
            if (entity is LivingEntity && entity != player && entity != primaryTarget) {
                entity.damage(this.damage, player)

                onDamageDealt(entity, this.damage, false)

                val direction = entity.location.toVector().subtract(center.toVector()).normalize()
                direction.setY(direction.getY() / 2 + upwardForce)
                direction.multiply(knockbackStrength)
                entity.velocity = entity.velocity.add(direction)
            }
        }
    }
    private fun createExplosion(player: Player, primaryTarget: LivingEntity, onDamageDealt: (target: LivingEntity, damage: Double, isCrit: Boolean) -> Unit) {
        createExplosion(player, primaryTarget.location, onDamageDealt, primaryTarget)
    }
}