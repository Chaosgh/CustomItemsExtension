package de.chaos.item.consumableComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType

@AlgebraicTypeInfo("effect", Colors.BLUE, "fa6-solid:hashtag")
data class ConsumableEffect(
    val potionEffect: Var<PotionEffectType> = ConstVar(PotionEffectType.SPEED),
    val duration: Int = 10,
    val amplifier: Int = 1,
    val ambient: Boolean = false,
    val showParticles: Boolean = true,
    val showIcon: Boolean = true
) : ConsumableComponent, ConsumableComponentHolder {

    override fun build(itemStack: ItemStack): ItemStack {
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
    }

    override fun getConsumeEffectJson(): String {
        val effectType = if (potionEffect is ConstVar) {
            potionEffect.value.key.toString()
        } else {
            "minecraft:speed" // fallback idk if needed
        }

        val ticks = duration * 20

        return buildString {
            append("{")
            append("id:\"$effectType\",")
            append("duration:$ticks,")
            append("amplifier:$amplifier,")
            append("ambient:$ambient,")
            append("show_particles:$showParticles,")
            append("show_icon:$showIcon")
            append("}")
        }
    }

    override fun getComponent(): ConsumableComponent = this
}