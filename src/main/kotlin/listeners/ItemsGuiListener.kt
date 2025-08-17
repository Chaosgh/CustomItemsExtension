package de.chaos.listeners

import de.chaos.ItemsGui
import de.chaos.ItemsGuiHolder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class ItemsGuiListener(private val plugin: Plugin) : Listener {

    private val KEY_BUTTON = NamespacedKey("chaos", "items_gui_button")
    private val BTN_PREV = "prev"
    private val BTN_NEXT = "next"

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val top = event.view.topInventory
        val holder = top.holder
        if (holder !is ItemsGuiHolder) return

        event.isCancelled = true

        val raw = event.rawSlot
        val topSize = top.size

        if (raw >= topSize) return

        val currentItem = event.currentItem ?: return
        if (currentItem.type == Material.AIR) return

        // Navigation prüfen
        val meta = currentItem.itemMeta
        if (meta != null) {
            val pdc = meta.persistentDataContainer
            val btn = pdc.get(KEY_BUTTON, PersistentDataType.STRING)
            if (btn == BTN_PREV) {
                val newPage = (holder.page - 1).coerceAtLeast(0)
                ItemsGui(holder.plugin).open(event.whoClicked as Player, newPage)
                return
            } else if (btn == BTN_NEXT) {
                val newPage = (holder.page + 1).coerceAtMost(holder.totalPages - 1)
                ItemsGui(holder.plugin).open(event.whoClicked as Player, newPage)
                return
            }
        }

        if (raw in 0..44) {
            when (event.click) {
                ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT -> {
                    val player = event.whoClicked as? Player ?: return
                    val toGive = currentItem.clone()
                    player.inventory.addItem(toGive)
                    player.updateInventory()
                }
                else -> {
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        val top = event.view.topInventory
        if (top.holder !is ItemsGuiHolder) return
        event.isCancelled = true
    }

    companion object {
        fun register(plugin: Plugin): ItemsGuiListener {
            val listener = ItemsGuiListener(plugin)
            org.bukkit.Bukkit.getPluginManager().registerEvents(listener, plugin)
            return listener
        }
    }
}