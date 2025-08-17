package de.chaos.listeners

import com.typewritermc.core.entries.Query
import de.chaos.entries.statics.CustomItemEntry
import de.chaos.item.ItemTypeTool
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.plugin.Plugin

class ToolBreakListener(private val plugin: Plugin) : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val tool = player.inventory.itemInMainHand
        if (!tool.hasItemMeta()) return

        val toolId = ItemTypeTool.getToolID(tool) ?: return
        val entry = Query.findById<CustomItemEntry>(toolId) ?: return
        val customTool = entry.customItem as? ItemTypeTool ?: return

        val originalXP = event.expToDrop

        var drops = block.getDrops(tool).toList()

        val additionalBlocks = customTool.components.flatMap { it.getAdditionalBlocks(player, tool, block) }.toMutableSet()

        customTool.components.forEach { component ->
            component.onBlockBreak(player, tool, block, drops)?.let { drops = it }
        }

        event.isDropItems = false

        val handledDirectly = customTool.components.any { component ->
            component.handleDropsDirectly(player, tool, block, drops)
        }

        if (!handledDirectly) {
            drops.forEach { item -> block.world.dropItemNaturally(block.location, item) }
        }

        if (originalXP > 0) {
            dropExperience(block, originalXP)
        }

        for (extra in additionalBlocks) {
            if (!canBreakBlock(player, extra)) continue
            var extraDrops = extra.getDrops(tool).toList()

            val extraXP = getExperienceFromBlock(extra)

            customTool.components.forEach { component ->
                component.onBlockBreak(player, tool, extra, extraDrops)?.let { extraDrops = it }
            }

            val extraHandled = customTool.components.any { component ->
                component.handleDropsDirectly(player, tool, extra, extraDrops)
            }

            extra.type = Material.AIR

            if (!extraHandled) {
                extraDrops.forEach { item -> extra.world.dropItemNaturally(extra.location, item) }
            }

            if (extraXP > 0) {
                dropExperience(extra, extraXP)
            }

            damageTool(player, tool, customTool)
        }

        damageTool(player, tool, customTool)

        customTool.components.forEach { component -> component.execute(player, tool) }
    }

    private fun dropExperience(block: Block, amount: Int) {
        if (amount <= 0) return
        block.world.spawn(block.location.add(0.5, 0.5, 0.5), ExperienceOrb::class.java) { orb ->
            orb.experience = amount
        }
    }

    private fun getExperienceFromBlock(block: Block): Int {
        return when (block.type) {
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE -> (1..3).random()
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE -> (3..7).random()
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE -> (3..7).random()
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE -> (2..5).random()
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE -> (1..5).random()
            Material.NETHER_QUARTZ_ORE -> (2..5).random()
            Material.SPAWNER -> (15..43).random()

            Material.SCULK, Material.SCULK_CATALYST -> 1
            Material.SCULK_SHRIEKER -> 5

            else -> 0
        }
    }

    private fun canBreakBlock(player: Player, block: Block): Boolean {
        return when (player.gameMode) {
            GameMode.CREATIVE -> true
            GameMode.SURVIVAL, GameMode.ADVENTURE -> true
            else -> false
        }
    }

    private fun damageTool(player: Player, tool: ItemStack, customTool: ItemTypeTool) {
        if (player.gameMode == GameMode.CREATIVE) return
        val meta = tool.itemMeta ?: return
        if (meta is Damageable) {
            val newDamage = meta.damage + customTool.damagePerBlock
            if (newDamage >= customTool.durability) {
                player.inventory.setItemInMainHand(ItemStack(Material.AIR))
                player.world.playSound(player.location, org.bukkit.Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f)
            } else {
                meta.damage = newDamage
                tool.itemMeta = meta
            }
        }
    }

    companion object {
        fun register(plugin: Plugin): ToolBreakListener {
            val listener = ToolBreakListener(plugin)
            Bukkit.getPluginManager().registerEvents(listener, plugin)
            return listener
        }
    }
}