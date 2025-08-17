package de.chaos.item.toolComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("telepathy", Colors.BLUE, "fa6-solid:hashtag")
class ToolTelepathy(

) : ToolComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun handleDropsDirectly(
        player: Player,
        tool: ItemStack,
        block: org.bukkit.block.Block,
        drops: List<ItemStack>
    ): Boolean {
        val leftovers = mutableListOf<ItemStack>()
        for (drop in drops) {
            val notFit = player.inventory.addItem(drop).values
            leftovers.addAll(notFit)
        }
        leftovers.forEach { item -> player.world.dropItemNaturally(player.location, item) }
        return true
    }
}
