package de.chaos.item.swordComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.BaseComponent
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("sword", Colors.BLUE, "fa6-solid:hashtag")
sealed interface SwordComponent : BaseComponent {

    fun execute(player: Player, itemStack: ItemStack, damageDealt: Double, hitEntity: LivingEntity) {
    }
}
