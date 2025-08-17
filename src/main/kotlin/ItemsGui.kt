package de.chaos

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin
import org.bukkit.persistence.PersistentDataType

class ItemsGui(private val plugin: Plugin) {

    companion object {
        private const val GUI_SIZE = 54
        private const val ITEMS_PER_PAGE = 45
        private const val SLOT_PREV = 45
        private const val SLOT_INFO = 49
        private const val SLOT_NEXT = 53
        private val KEY_BUTTON = org.bukkit.NamespacedKey("chaos", "items_gui_button")
        private const val BTN_PREV = "prev"
        private const val BTN_NEXT = "next"
    }

    fun open(player: Player) {
        open(player, 0)
    }

    fun open(player: Player, page: Int) {
        val items = CustomItemRegistry.getAll()
        val totalPages = (items.size + ITEMS_PER_PAGE - 1).coerceAtLeast(1) / ITEMS_PER_PAGE
        val currentPage = page.coerceIn(0, totalPages - 1)

        val title = buildTitle(currentPage, totalPages)
        val holder = ItemsGuiHolder(plugin, currentPage, totalPages)
        val inv: Inventory = Bukkit.createInventory(holder, GUI_SIZE, title)

        val start = currentPage * ITEMS_PER_PAGE
        val end = (start + ITEMS_PER_PAGE).coerceAtMost(items.size)
        var slot = 0
        for (i in start until end) {
            val stack = items[i].buildItemStack()
            inv.setItem(slot++, stack)
        }

        inv.setItem(SLOT_PREV, makeButton(Material.ARROW, Component.text("Previous Page"), BTN_PREV))
        inv.setItem(SLOT_INFO, makeInfoItem(currentPage, totalPages))
        inv.setItem(SLOT_NEXT, makeButton(Material.ARROW, Component.text("Next Page"), BTN_NEXT))

        fillBottomRow(inv)

        if (Bukkit.isPrimaryThread()) {
            player.openInventory(inv)
        } else {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                player.openInventory(inv)
            })
        }
    }

    private fun buildTitle(page: Int, total: Int): String {
        return if (total > 1) {
            "Custom Items (Page ${page + 1}/$total)"
        } else {
            "Custom Items"
        }
    }

    private fun makeButton(material: Material, name: Component, btnType: String): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.displayName(name)
        meta.persistentDataContainer.set(KEY_BUTTON, PersistentDataType.STRING, btnType)
        item.itemMeta = meta
        return item
    }

    private fun makeInfoItem(page: Int, total: Int): ItemStack {
        val item = ItemStack(Material.PAPER)
        val meta: ItemMeta = item.itemMeta
        meta.displayName(Component.text("Page ${page + 1} of $total"))
        item.itemMeta = meta
        return item
    }

    private fun fillBottomRow(inv: Inventory) {
        for (i in 45 until 54) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
                    val m = itemMeta
                    m.displayName(Component.text(" "))
                    itemMeta = m
                })
            }
        }
    }
}
