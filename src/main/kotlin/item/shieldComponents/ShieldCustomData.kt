package de.chaos.item.shieldComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo


import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack



@AlgebraicTypeInfo("custom data", Colors.BLUE, "fa6-solid:hashtag")




class ShieldCustomData(
    val customData: String = "",

    ) : ShieldComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }

}
