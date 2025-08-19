package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.extension.annotations.Help
import de.chaos.data.BowStats
import de.chaos.item.bowComponents.BowComponent
import de.chaos.item.itemComponents.ItemComponent
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@AlgebraicTypeInfo("bow", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeBow(
    @Help("Base damage of the bow.") val baseDamage: Double = 5.0,
    @Help("Base critical hit chance (0.0 to 1.0).") val basecritChance: Double = 0.1,
    @Help("Base critical hit damage multiplier.") val basecritMultiplier: Double = 1.5,
    @Help("The maximum durability (max_damage) of the bow.") val durability: Int = 384,
    @Help("Bow components for additional functionality.") val components: List<BowComponent> = emptyList(),
    @Help("Item components for additional functionality.") val itemComponent: List<ItemComponent> = emptyList()
) : ItemTypeComponent {

    override fun build(itemStack: ItemStack, id: String): ItemStack {
        val oldMeta = itemStack.itemMeta ?: return itemStack
        val displayName = oldMeta.displayName()
        val lore = oldMeta.lore()
        val customModelData = if (oldMeta.hasCustomModelData()) oldMeta.customModelData else null

        val materialKey = itemStack.type.key.toString()
        val bowJson = buildBowJson(materialKey)
        val newItem = org.bukkit.Bukkit.getItemFactory().createItemStack(bowJson)
        val newMeta = newItem.itemMeta

        val pdc = newMeta.persistentDataContainer
        // Removed writing damage/crit stats to PDC
        pdc.set(ENTRY_ID, PersistentDataType.STRING, id)

        newMeta.displayName(displayName)
        newMeta.lore(lore)
        newMeta.setCustomModelData(customModelData)
        newItem.itemMeta = newMeta

        val afterTypeSpecific = components.fold(newItem) { acc, c -> c.build(acc) }
        return itemComponent.fold(afterTypeSpecific) { acc, c -> c.build(acc) }
    }

    private fun buildBowJson(materialKey: String): String {
        return buildString {
            append("$materialKey[")
            append("max_stack_size=1,")
            append("max_damage=$durability,")
            append("damage=0")
            append("]")
        }
    }

    companion object {
        // Removed damage-related keys and getBowStats, keep only entry id
        val ENTRY_ID = NamespacedKey("chaos", "entry_id")

        fun getBowID(itemStack: ItemStack): String? {
            val meta = itemStack.itemMeta ?: return null
            return meta.persistentDataContainer.get(ENTRY_ID, PersistentDataType.STRING)
        }
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        components.forEach { component ->
            component.execute(player, itemStack)
        }
    }
}