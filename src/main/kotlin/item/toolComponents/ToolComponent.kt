package de.chaos.item.toolComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.BaseComponent
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("tool component", Colors.BLUE, "material-symbols:construction")
sealed interface ToolComponent: BaseComponent {

    fun onBlockBreak(
        player: Player,
        tool: ItemStack,
        block: Block,
        drops: List<ItemStack>
    ): List<ItemStack>? = null


    fun getAdditionalBlocks(
        player: Player,
        tool: ItemStack,
        originalBlock: Block
    ): List<Block> = emptyList()


    fun handleDropsDirectly(
        player: Player,
        tool: ItemStack,
        block: Block,
        drops: List<ItemStack>
    ): Boolean = false


}