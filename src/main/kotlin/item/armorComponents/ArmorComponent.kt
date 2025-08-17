package de.chaos.item.armorComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.BaseComponent
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent


@AlgebraicTypeInfo("armor", Colors.BLUE, "fa6-solid:hashtag")
sealed interface ArmorComponent : BaseComponent {


    fun getFactoryString(): String

    fun onHit(player: Player, event: EntityDamageByEntityEvent) {
    }

}