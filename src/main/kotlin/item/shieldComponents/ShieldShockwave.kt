package de.chaos.item.shieldComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("shockwave", Colors.BLUE, "fa6-solid:hashtag")
class ShieldShockwave(
    val pushRange: Int = 5,

) : ShieldComponent {
    override fun build(itemStack: ItemStack): ItemStack {
        TODO("Not yet implemented")
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }


}
