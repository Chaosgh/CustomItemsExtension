package de.chaos.item.armorComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


@AlgebraicTypeInfo("reflect", Colors.BLUE, "fa6-solid:hashtag")

class ArmorReflect(
    val reflectChance: Double = 1.0,
    val reflectPercentage: Double = 1.0,

    ) : ArmorComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }


}
