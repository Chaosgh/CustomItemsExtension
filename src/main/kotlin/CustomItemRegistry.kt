package de.chaos

import de.chaos.item.ItemTypeComponent
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


data class CustomItem(
    val id: String,
    val material: Material,
    val component: ItemTypeComponent,
    val displayName: Component,

    val lore: List<Component>
) {

    fun buildItemStack(): ItemStack {
        val baseStack = ItemStack(material)
        val meta = baseStack.itemMeta

        meta.displayName(displayName)
        meta.lore(lore)
        baseStack.itemMeta = meta



        return component.build(baseStack, id)
    }
}



object CustomItemRegistry {
    private val items: MutableList<CustomItem> = mutableListOf()

    fun register(item: CustomItem) {
        items.removeIf { it.id.equals(item.id, ignoreCase = true) }
        items.add(item)
    }

    fun getAll(): List<CustomItem> = items.toList()

    fun getById(id: String): CustomItem? = items.firstOrNull { it.id.equals(id, ignoreCase = true) }
}