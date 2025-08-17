package de.chaos.item.armorComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack


@AlgebraicTypeInfo("elytra flight", Colors.BLUE, "fa6-solid:hashtag")

class ArmorElytraFlight(

) : ArmorComponent {

    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }


    override fun getFactoryString(): String {

        return "glider={}"
    }


}
