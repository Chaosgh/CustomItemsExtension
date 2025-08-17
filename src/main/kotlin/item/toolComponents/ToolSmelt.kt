package de.chaos.item.toolComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("smelt", Colors.BLUE, "fa6-solid:hashtag")
class ToolSmelt : ToolComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun onBlockBreak(
        player: Player,
        tool: ItemStack,
        block: org.bukkit.block.Block,
        drops: List<ItemStack>
    ): List<ItemStack>? {
        fun smeltedFor(material: Material): Pair<Material, Float>? = when (material) {
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE, Material.RAW_IRON -> Material.IRON_INGOT to 0.7f
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE, Material.RAW_GOLD -> Material.GOLD_INGOT to 1.0f
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE, Material.RAW_COPPER -> Material.COPPER_INGOT to 0.7f
            Material.ANCIENT_DEBRIS -> Material.NETHERITE_SCRAP to 2.0f
            Material.SAND -> Material.GLASS to 0.1f
            Material.COBBLESTONE -> Material.STONE to 0.1f
            Material.WET_SPONGE -> Material.SPONGE to 0.15f
            Material.CACTUS -> Material.GREEN_DYE to 1.0f
            else -> null
        }

        var totalXp = 0f
        val out = drops.map { stack ->
            val mapped = smeltedFor(stack.type)
            if (mapped != null) {
                val (result, xp) = mapped
                totalXp += xp * stack.amount
                ItemStack(result, stack.amount)
            } else stack
        }

        if (totalXp > 0f) {
            player.giveExp(totalXp.toInt())
        }

        return out
    }
}
