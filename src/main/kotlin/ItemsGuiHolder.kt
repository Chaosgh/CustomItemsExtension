package de.chaos

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.Plugin

class ItemsGuiHolder(
    val plugin: Plugin,
    val page: Int,
    val totalPages: Int
) : InventoryHolder {

    override fun getInventory(): Inventory {
        // Dummy-Inventory, wird nie wirklich benutzt
        return Bukkit.createInventory(null, 9)
    }
}
