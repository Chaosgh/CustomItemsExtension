package de.chaos.gui

import de.chaos.items.CustomItemRegistry
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.Plugin

class ItemsGui(private val plugin: Plugin) {

    fun open(player: Player) {
        val items = CustomItemRegistry.getAll()
        val size = ((items.size.coerceAtLeast(9) + 8) / 9) * 9
        val inv: Inventory = Bukkit.createInventory(null, size.coerceIn(9, 54), "Custom Items")

        items.forEachIndexed { index, customItem ->
            if (index >= inv.size) return@forEachIndexed

            val stack = customItem.buildItemStack()
            inv.setItem(index, stack)
        }

        if (Bukkit.isPrimaryThread()) {
            player.openInventory(inv)
        } else {
            Bukkit.getScheduler().runTask(plugin, Runnable { player.openInventory(inv) })
        }
    }
}