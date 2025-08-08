package de.chaos.item.consumableComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import java.time.Duration

@AlgebraicTypeInfo("effect", Colors.BLUE, "fa6-solid:hashtag")
class ConsumableEffect(
    val potionEffect: Var<PotionEffectType> = ConstVar(PotionEffectType.SPEED),
    val duration: Var<Duration> = ConstVar(Duration.ofSeconds(10)),
) : ConsumableComponent {
    override fun build(itemStack: ItemStack): ItemStack {


        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        TODO("Not yet implemented")
    }


}
