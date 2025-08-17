package de.chaos.item.consumableComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("warp", Colors.BLUE, "fa6-solid:location-arrow")
data class ConsumableWarp(
    val radius: Double = 5.0,
) : ConsumableComponent, ConsumableComponentHolder {

    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun getConsumeEffectJson(): String {
        return "{type:\"minecraft:teleport_randomly\",diameter:$radius}"
    }

    override fun getComponent(): ConsumableComponent = this
}