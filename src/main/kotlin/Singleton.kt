package de.chaos

import com.typewritermc.core.entries.Query
import com.typewritermc.core.extension.Initializable
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.logger
import de.chaos.entries.statics.CustomItemEntry
import de.chaos.item.ItemTypeComponent // Sicherstellen, dass dieser Import korrekt ist
import de.chaos.items.CustomItem
import de.chaos.items.CustomItemRegistry
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material

@Singleton
object Initializer : Initializable {

    private val miniMessage = MiniMessage.miniMessage()

    override suspend fun initialize() {
        logger.info("§7[§bCustomItemExtension§7] §fInitializing…")

        val entries = Query.find<CustomItemEntry>().sortedBy { it.id }

        entries.forEach { entry ->
            // NEU: Holen der ItemTypeComponent aus dem Entry.
            val itemComponent = entry.customItem
            if (itemComponent == null) {
                logger.warning("§c[CustomItem] Entry '${entry.id}' hat keine gültige 'customItem'-Komponente definiert und wird übersprungen.")
                return@forEach
            }

            // 1. Hole den displayName String aus dem Entry.
            val displayNameString = entry.displayName
            if (displayNameString.isBlank()) {
                logger.warning("§c[CustomItem] Entry '${entry.id}' hat keinen 'displayName' definiert und wird übersprungen.")
                return@forEach
            }

            // 2. Parse den String mit MiniMessage zu einem Component.
            val displayNameComponent = miniMessage.deserialize(displayNameString)
                .decoration(TextDecoration.ITALIC, false)
            // 3. Holen Sie sich das Material aus dem Entry.
            val material = when (val matVar = (entry.item as? ConstVar)?.value?.material) {
                is ConstVar -> matVar.value
                else -> {
                    logger.warning("§c[CustomItem] Konnte Material für '${entry.id}' nicht bestimmen. Verwende STEIN als Fallback.")
                    Material.STONE
                }
            }

            // 4. Erstellen und registrieren Sie das CustomItem mit allen vier Argumenten.
            val customItem = CustomItem(
                id = entry.id,
                material = material,
                component = itemComponent, // Das fehlende Argument wurde hinzugefügt
                displayName = displayNameComponent
            )
            CustomItemRegistry.register(customItem)
        }

        logger.info("§7[§bCustomItemExtension§7] §aRegistered ${CustomItemRegistry.getAll().size} CustomItem entries from Typewriter.")
    }

    override suspend fun shutdown() {
        // No-op
    }
}