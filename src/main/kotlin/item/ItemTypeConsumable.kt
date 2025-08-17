package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.utils.Sound
import de.chaos.entries.action.FoodAnimations
import de.chaos.item.itemComponents.ItemComponent
import de.chaos.item.consumableComponents.ConsumableComponent
import de.chaos.item.consumableComponents.ConsumableComponentHolder
import de.chaos.item.consumableComponents.ConsumableEffect
import de.chaos.util.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.FoodComponent
@AlgebraicTypeInfo("consumable", Colors.BLUE, "lucide-lab:apple-core")
class ItemTypeConsumable(
    @Help("how many hunger bars the item fills if its eaten")
    val foodLevel: Int = 4,
    @Help("how much saturation the item fills if its eaten")
    val saturation: Float = 5.0f,
    @Help("the time that it takes to consume the item")
    val consumeTime: Float = 0.5f,
    @Help("If you can eat the item even if your hungerbar is full")
    val canAlwaysEat: Boolean = false,
    @Help("the animation that plays while eating")
    val animation: FoodAnimations = FoodAnimations.EAT,
    @Help("The sound that plays while eaten")
    val sound: Var<Sound> = ConstVar(Sound.EMPTY),
    val hasConsumeParticles: Boolean = true,
    val componentHolders: List<ConsumableComponentHolder> = emptyList(),
    @Help("item components for additional functionality")
    val itemComponent: List<ItemComponent> = emptyList()
) : ItemTypeComponent {

    private val components: List<ConsumableComponent>
        get() = componentHolders.map { it.getComponent() }

    override fun build(itemStack: ItemStack, id: String): ItemStack {
        val oldMeta: ItemMeta = itemStack.itemMeta ?: return itemStack

        val displayName = oldMeta.displayName()
        val lore = oldMeta.lore()
        val customModelData = if (oldMeta.hasCustomModelData()) oldMeta.customModelData else null
        val material = itemStack.type

        val consumableJson = buildConsumableJson(material.key.toString())

        val newItem = Bukkit.getItemFactory().createItemStack(consumableJson)
        val newMeta = newItem.itemMeta

        val food: FoodComponent = newMeta.food
        food.nutrition = foodLevel
        food.saturation = saturation
        food.setCanAlwaysEat(canAlwaysEat)
        newMeta.setFood(food)

        newMeta.displayName(displayName)
        newMeta.lore(lore)
        newMeta.setCustomModelData(customModelData)

        newItem.itemMeta = newMeta
        ItemBuilder(newItem).setCustomModelData(customModelData)



        val afterTypeSpecific = components.fold(newItem) { acc, c -> c.build(acc) }
        return itemComponent.fold(afterTypeSpecific) { acc, c -> c.build(acc) }
    }

    private fun buildConsumableJson(materialKey: String): String {
        val effectComponents = components.filterIsInstance<ConsumableEffect>()
        val otherEffectComponents = components.filter {
            it !is ConsumableEffect && it.getConsumeEffectJson() != null
        }

        val shouldIncludeSound = when (sound) {
            is ConstVar<Sound> -> sound.value != Sound.EMPTY
            else -> true
        }

        val soundString = if (shouldIncludeSound && sound is ConstVar<Sound>) {
            sound.value.soundId.namespacedKey?.toString()
        } else if (shouldIncludeSound) {
            "entity.generic.eat"
        } else {
            null
        }

        return buildString {
            append("$materialKey[consumable={")
            append("consume_seconds:$consumeTime,")
            append("animation:\"${animation.animationName}\",")

            soundString?.let {
                append("sound:\"$it\",")
            }

            append("has_consume_particles:$hasConsumeParticles")

            val allEffects = mutableListOf<String>()

            if (effectComponents.isNotEmpty()) {
                val potionEffectsJson = buildString {
                    append("{type:\"minecraft:apply_effects\",effects:[")
                    effectComponents.forEachIndexed { index, effect ->
                        if (index > 0) append(",")
                        append(effect.getConsumeEffectJson())
                    }
                    append("]}")
                }
                allEffects.add(potionEffectsJson)
            }

            otherEffectComponents.forEach { component ->
                component.getConsumeEffectJson()?.let { effectJson ->
                    allEffects.add(effectJson)
                }
            }

            if (allEffects.isNotEmpty()) {
                append(",on_consume_effects:[")
                allEffects.forEachIndexed { index, effect ->
                    if (index > 0) append(",")
                    append(effect)
                }
                append("]")
            }

            append("}]")
        }
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        components.forEach { component ->
            component.execute(player, itemStack)
        }
    }
}