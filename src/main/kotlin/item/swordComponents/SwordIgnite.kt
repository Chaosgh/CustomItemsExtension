package de.chaos.item.swordComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("ignite", Colors.BLUE, "fa6-solid:hashtag")
class SwordIgnite(
    val duration: Double = 1.0,
) : SwordComponent {

    override fun build(itemStack: ItemStack): ItemStack {

        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun execute(player: Player, itemStack: ItemStack, damageDealt: Double, hitEntity: LivingEntity) {
        val ticks = (duration * 20).toInt()
        hitEntity.fireTicks = hitEntity.fireTicks.coerceAtLeast(ticks)
    }
}
