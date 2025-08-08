package de.chaos.item.axeComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.interaction.InteractionContext


import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


@AlgebraicTypeInfo("custom data", Colors.BLUE, "fa6-solid:hashtag")


class AxeCleave(
    val cleaveChance: Double = 0.2,
    val cleaveRadius: Double = 0.2,
) : AxeComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }


}
