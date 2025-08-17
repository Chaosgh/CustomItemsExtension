package de.chaos.item.swordComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("drain", Colors.BLUE, "fa6-solid:hashtag")

class SwordDrain(
    val drainPercentage: Double = 5.0,

    ) : SwordComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {

    }

    override fun execute(player: Player, itemStack: ItemStack, damageDealt: Double, hitEntity: LivingEntity) {
        val healAmount = damageDealt * (drainPercentage / 100.0)

        val newHealth = (player.health + healAmount).coerceAtMost(player.maxHealth)

        player.health = newHealth
        player.sendActionBar("§a+ ${"%.2f".format(healAmount)} health!")
    }

}
