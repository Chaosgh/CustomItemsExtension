package de.chaos.item.itemComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.util.ItemBuilder
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("tooltip background", Colors.BLUE, "fa6-solid:hashtag")
class ItemTooltipBackground(
    val namespace: String = "",
    val key: String = ""
) : ItemComponent {
    override fun build(itemStack: ItemStack): ItemStack {


        return ItemBuilder(itemStack).setCustomToolTipStyle(NamespacedKey(namespace, key)).build()
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }
}