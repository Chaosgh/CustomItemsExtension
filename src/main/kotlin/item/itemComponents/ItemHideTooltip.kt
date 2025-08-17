package de.chaos.item.itemComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.util.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("hide tooltip", Colors.BLUE, "fa6-solid:hashtag")
class ItemHideTooltip(
) : ItemComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        return ItemBuilder(itemStack).hideTooltips(true).build()
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }
}