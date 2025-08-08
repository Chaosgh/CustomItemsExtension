package de.chaos.item.axeComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("foraging", Colors.BLUE, "fa6-solid:hashtag")
class AxeForaging(
    val foragingRadius: Int = 5,

    ) : AxeComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }


}
