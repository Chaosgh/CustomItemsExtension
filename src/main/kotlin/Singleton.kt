
package de.chaos

import com.typewritermc.core.entries.Query
import com.typewritermc.core.extension.Initializable
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.logger
import com.typewritermc.engine.paper.plugin
import de.chaos.entries.statics.CustomItemEntry
import de.chaos.listeners.*

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin

@Singleton
object Initializer : Initializable {
    private val javaPlugin: JavaPlugin = plugin
    private val miniMessage = MiniMessage.miniMessage()

    private lateinit var swordListener: SwordDamageListener
    private lateinit var bowListener: BowDamageListener
    private lateinit var armorListener: ArmorHitListener
    private lateinit var toolListener: ToolBreakListener
    private lateinit var itemsGuiListener: ItemsGuiListener

    override suspend fun initialize() {
        // === Listener registrieren ===
        swordListener = SwordDamageListener.register(javaPlugin)
        bowListener = BowDamageListener.register(javaPlugin)
        armorListener = ArmorHitListener.register(javaPlugin)
        toolListener = ToolBreakListener.register(javaPlugin)
        itemsGuiListener = ItemsGuiListener.register(javaPlugin)

        // === Custom Items aus Entries laden/registrieren ===
        val itemEntries = Query.find<CustomItemEntry>().sortedBy { it.id }
        itemEntries.forEach { entry ->
            val itemComponent = entry.customItem ?: return@forEach
            if (entry.displayName.isBlank()) return@forEach

            // DisplayName: behält Farbe, aber ohne Italic
            val displayNameComponent = miniMessage.deserialize(entry.displayName)
                .decoration(TextDecoration.ITALIC, false)

            // Lore: grau + ohne Italic (Zeilenumbruch -> Liste)
            val lore: List<Component> = entry.lore
                .split("\n")
                .filter { it.isNotBlank() }
                .map { line ->
                    miniMessage.deserialize("<gray>$line</gray>")
                        .decoration(TextDecoration.ITALIC, false)
                }

            val material: Material = when (entry.item) {
                is ConstVar -> entry.item.value
                else -> Material.STONE
            }

            val customItem = CustomItem(
                id = entry.id,
                material = material,
                component = itemComponent,
                displayName = displayNameComponent,
                lore = lore
            )
            CustomItemRegistry.register(customItem)
        }

        // Direkt Items bauen (Fehler abfangen)
        CustomItemRegistry.getAll().forEach { ci ->
            try {
                ci.buildItemStack()
            } catch (e: Exception) {
                logger.warning("§7[§bCustomItemExtension§7] §c[Init] Build failed for '${ci.id}': ${e.message}")
            }
        }
        logger.info("§7[§bCustomItemExtension§7] §aRegistered ${CustomItemRegistry.getAll().size} CustomItem entries from Typewriter.")



    }

    override suspend fun shutdown() {
        HandlerList.unregisterAll(swordListener)
        HandlerList.unregisterAll(bowListener)
        HandlerList.unregisterAll(armorListener)
        HandlerList.unregisterAll(toolListener)
        HandlerList.unregisterAll(itemsGuiListener)

    }
}
