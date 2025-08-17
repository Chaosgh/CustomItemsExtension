package de.chaos.entries.action

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.entries.Ref
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.engine.paper.entry.Criteria
import com.typewritermc.engine.paper.entry.Modifier
import com.typewritermc.engine.paper.entry.TriggerableEntry
import com.typewritermc.engine.paper.entry.entries.ActionEntry
import com.typewritermc.engine.paper.entry.entries.ActionTrigger
import com.typewritermc.engine.paper.logger
import de.chaos.ItemsGui
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

@Entry("open_item_gui", "opens the gui with all the custom items", Colors.RED, "pixel:bullet-list")
class OpenItemsGuiAction(
    override val id: String = "",
    override val name: String = "",
    override val criteria: List<Criteria> = emptyList(),
    override val modifiers: List<Modifier> = emptyList(),
    override val triggers: List<Ref<TriggerableEntry>> = emptyList(),
) : ActionEntry {
    override fun ActionTrigger.execute() {
        val player: Player = player ?: return


        val typewriterPlugin: Plugin? = Bukkit.getPluginManager().getPlugin("Typewriter")

        if (typewriterPlugin == null || !typewriterPlugin.isEnabled) {
            logger.severe("Typewriter plugin could not be found or is disabled. Cannot open GUI.")
            return
        }

        val itemsGui = ItemsGui(typewriterPlugin)

        itemsGui.open(player)
    }
}