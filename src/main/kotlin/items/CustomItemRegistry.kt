package de.chaos.items

import de.chaos.item.ItemTypeComponent // Sicherstellen, dass dieser Import korrekt ist
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Repräsentiert ein vollständig definiertes CustomItem, das seine Logik (Component)
 * und sein Aussehen (Material, displayName) kennt.
 */
data class CustomItem(
    val id: String,
    val material: Material,
    val component: ItemTypeComponent,
    val displayName: Component
) {
    /**
     * Baut einen vollständig funktionalen ItemStack.
     * 1. Erstellt einen Basis-Stack mit dem richtigen Material.
     * 2. Wendet den displayName an.
     * 3. Delegiert die weitere, spezifische Logik an die zugehörige Komponente.
     */
    fun buildItemStack(): ItemStack {
        val baseStack = ItemStack(material)
        val meta = baseStack.itemMeta

        // Schritt 1: Wende den DisplayName, der im CustomItem gespeichert ist, an.
        meta.displayName(displayName)
        baseStack.itemMeta = meta

        // Schritt 2: Gib den Stack mit dem bereits gesetzten Namen an die Komponente weiter.
        // Die Komponente kann nun weitere Änderungen vornehmen (z.B. Lore, Enchants hinzufügen).
        return component.build(baseStack)
    }
}

/**
 * Eine zentrale Registry, die alle erstellten CustomItem-Objekte verwaltet.
 */
object CustomItemRegistry {
    private val items: MutableList<CustomItem> = mutableListOf()

    fun register(item: CustomItem) {
        items.removeIf { it.id.equals(item.id, ignoreCase = true) }
        items.add(item)
    }

    fun getAll(): List<CustomItem> = items.toList()

    fun getById(id: String): CustomItem? = items.firstOrNull { it.id.equals(id, ignoreCase = true) }
}