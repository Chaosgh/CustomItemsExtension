package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("item type", Colors.BLUE, "fa6-solid:hashtag")
sealed interface ItemTypeComponent {


    fun build(itemStack: ItemStack,id: String): ItemStack


    fun execute(player: Player, itemStack: ItemStack)

}