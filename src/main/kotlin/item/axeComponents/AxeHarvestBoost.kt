package de.chaos.item.axeComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("harvest boost", Colors.BLUE, "fa6-solid:hashtag")
class AxeHarvestBoost(
    val harvestBoostChance: Double = 5.0,
    val harvestBoostAmount: Int = 2,
) : AxeComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }


}
