package de.chaos

import com.typewritermc.core.entries.Query
import com.typewritermc.core.extension.Initializable
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.logger
import com.typewritermc.engine.paper.plugin
import de.chaos.entries.statics.CustomItemEntry
import de.chaos.listeners.ArmorHitListener
import de.chaos.listeners.BowDamageListener
import de.chaos.listeners.SwordDamageListener
import de.chaos.listeners.ToolBreakListener
import de.chaos.listeners.ItemsGuiListener
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

        swordListener = SwordDamageListener.register(javaPlugin)
        bowListener = BowDamageListener.register(javaPlugin)
        armorListener = ArmorHitListener.register(javaPlugin)
        toolListener = ToolBreakListener.register(javaPlugin)
        itemsGuiListener = ItemsGuiListener.register(javaPlugin)


        val entries = Query.find<CustomItemEntry>().sortedBy { it.id }

        entries.forEach { entry ->
            val itemComponent = entry.customItem
            if (itemComponent == null) {
                return@forEach
            }

            val displayNameString = entry.displayName
            if (displayNameString.isBlank()) {
                return@forEach
            }

            val displayNameComponent = miniMessage.deserialize(displayNameString)
                .decoration(TextDecoration.ITALIC, false)

            val material = when (val matVar = (entry.item as? ConstVar)?.value?.material) {
                is ConstVar -> matVar.value
                else -> {
                    Material.STONE
                }
            }

            val customItem = CustomItem(
                id = entry.id,
                material = material,
                component = itemComponent,
                displayName = displayNameComponent
            )
            CustomItemRegistry.register(customItem)
        }

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