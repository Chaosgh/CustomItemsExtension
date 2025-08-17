package de.chaos.item.itemComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.util.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("custom model data", Colors.BLUE, "fa6-solid:hashtag")
class ItemCustomData(
    val customModelData: Int = 0
) : ItemComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        return ItemBuilder(itemStack).setCustomModelData(customModelData).build()
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }
}