package de.chaos.entries.statics

import com.typewritermc.core.books.pages.Colors.PURPLE
import com.typewritermc.core.extension.annotations.Colored
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.MultiLine
import com.typewritermc.engine.paper.entry.StaticEntry
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import de.chaos.item.ItemTypeComponent
import org.bukkit.Material

@Entry("custom_item", "builds Custom Items", PURPLE, "mdi:package-variant")
class CustomItemEntry(
    override val id: String = "",
    override val name: String = "",
    val displayName: String = "",
    @Colored
    @MultiLine
    val lore: String = "",
    val item: Var<Material> = ConstVar(Material.AIR),
    val customItem: ItemTypeComponent? = null
) : StaticEntry
