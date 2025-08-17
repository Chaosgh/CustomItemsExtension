package de.chaos.item.toolComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("vein miner", Colors.BLUE, "fa6-solid:hashtag")
class ToolVeinMiner(
    val radius: Int = 5,
) : ToolComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun getAdditionalBlocks(
        player: Player,
        tool: ItemStack,
        originalBlock: Block
    ): List<Block> {
        val world = originalBlock.world
        val type = originalBlock.type

        val ores = setOf(
            Material.COAL_ORE,
            Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE,
            Material.DEEPSLATE_IRON_ORE,
            Material.COPPER_ORE,
            Material.DEEPSLATE_COPPER_ORE,
            Material.GOLD_ORE,
            Material.DEEPSLATE_GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.DEEPSLATE_REDSTONE_ORE,
            Material.EMERALD_ORE,
            Material.DEEPSLATE_EMERALD_ORE,
            Material.LAPIS_ORE,
            Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE,
            Material.DEEPSLATE_DIAMOND_ORE,
            Material.NETHER_QUARTZ_ORE,
            Material.NETHER_GOLD_ORE,
            Material.ANCIENT_DEBRIS
        )

        if (type !in ores) return emptyList()

        val visited = mutableSetOf<Block>()
        val queue: ArrayDeque<Block> = ArrayDeque()
        queue.add(originalBlock)
        visited.add(originalBlock)

        val result = mutableListOf<Block>()
        val maxCount = radius.coerceAtLeast(1)

        fun neighbors(b: Block): List<Block> {
            val loc = b.location
            val dx = intArrayOf(1, -1, 0, 0, 0, 0)
            val dy = intArrayOf(0, 0, 1, -1, 0, 0)
            val dz = intArrayOf(0, 0, 0, 0, 1, -1)
            val res = mutableListOf<Block>()
            for (i in 0..5) {
                val nb = world.getBlockAt(loc.blockX + dx[i], loc.blockY + dy[i], loc.blockZ + dz[i])
                if (nb.type == type) res.add(nb)
            }
            return res
        }

        while (queue.isNotEmpty() && result.size < maxCount) {
            val current = queue.removeFirst()
            if (current != originalBlock) {
                result.add(current)
            }
            for (nb in neighbors(current)) {
                if (nb !in visited) {
                    visited.add(nb)
                    queue.add(nb)
                }
            }
        }

        return result
    }
}
