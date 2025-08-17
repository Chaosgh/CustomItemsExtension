package de.chaos.item.bowComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.BaseComponent
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("bow", Colors.BLUE, "fa6-solid:hashtag")
sealed interface BowComponent : BaseComponent {


    fun execute(
        player: Player,
        bow: ItemStack,
        damage: Double,
        hitEntity: LivingEntity,
        onDamageDealt: (target: LivingEntity, damage: Double, isCrit: Boolean) -> Unit = { _, _, _ -> }
    )


    fun executeOnBlockHit(
        player: Player,
        bow: ItemStack,
        location: Location,
        onDamageDealt: (target: LivingEntity, damage: Double, isCrit: Boolean) -> Unit = { _, _, _ -> }
    ) {
    }

    fun onShoot(player: Player, bow: ItemStack, event: EntityShootBowEvent) {
    }
}