package de.chaos.item.bowComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.interaction.InteractionContext
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("bow explosive", Colors.BLUE, "fa6-solid:hashtag")
class BowExplosive(
    val radius: Double = 5.0,
    val damage: Double = 2.0,
) : BowComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }


}
