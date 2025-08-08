package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.consumableComponents.ConsumableComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.FoodComponent

@AlgebraicTypeInfo("consumable", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeConsumable(
    val foodLevel: Int = 4,         // Wie viele Hungerkeulen wiederhergestellt werden
    val saturation: Float = 5.0f,   // Wie viel Sättigung wiederhergestellt wird
    val canAlawaysEat: Boolean = false,
    val components: List<ConsumableComponent> = emptyList()
) : ItemTypeComponent {

    override fun build(itemStack: ItemStack): ItemStack {
        val oldMeta: ItemMeta = itemStack.itemMeta ?: return itemStack

        // Display Name und andere wichtige Eigenschaften vom alten Item speichern
        val displayName = oldMeta.displayName()
        val lore = oldMeta.lore()
        val customModelData = if (oldMeta.hasCustomModelData()) oldMeta.customModelData else null
        val material = itemStack.type

        // Neues essbares Item erstellen
        val newItem = Bukkit.getItemFactory().createItemStack("${material.key}[consumable={}]")
        val newMeta = newItem.itemMeta

        // FoodComponent konfigurieren
        val food: FoodComponent = newMeta.food
        food.setNutrition(foodLevel)
        food.setSaturation(saturation)
        food.setCanAlwaysEat(true)
        newMeta.setFood(food)

        // Alte Eigenschaften übertragen
        newMeta.displayName(displayName)
        newMeta.lore(lore)
        newMeta.setCustomModelData(customModelData)

        newItem.itemMeta = newMeta
        return newItem
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        // Hier kannst du die ConsumableComponents ausführen
        components.forEach { component ->
            component.execute(player, itemStack)
        }
    }
}