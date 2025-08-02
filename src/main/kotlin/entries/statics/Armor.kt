package de.chaos.entries.statics

import com.typewritermc.core.books.pages.Colors.PURPLE
import com.typewritermc.core.extension.annotations.Entry
import com.typewritermc.core.extension.annotations.Tags
import com.typewritermc.engine.paper.entry.StaticEntry
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.utils.item.Item

@Entry("blockdrops","loot from custom block",PURPLE,"icons8:cancel")
@Tags("drops")
class Armor(
    override val id: String = "",
    override val name: String = "",
    val item: Var<Item> = ConstVar(Item.Empty),
) : StaticEntry