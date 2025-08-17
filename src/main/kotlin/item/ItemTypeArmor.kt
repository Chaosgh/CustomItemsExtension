package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.engine.paper.entry.entries.ConstVar
import com.typewritermc.engine.paper.entry.entries.Var
import com.typewritermc.engine.paper.utils.Sound
import de.chaos.enums.ArmorType
import de.chaos.item.armorComponents.ArmorComponent
import de.chaos.item.itemComponents.ItemComponent
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

@AlgebraicTypeInfo("armor", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeArmor(
    val armorType: ArmorType = ArmorType.HELMET,
    val armorValue: Double = 1.0,
    val armorToughness: Double = 0.0,
    val durability: Int = 55,
    @Help("Asset ID for armor appearance (e.g. minecraft:diamond, minecraft:leather)") val assetId: String = "",
    @Help("Sound played when equipping the armor") val sound: Var<Sound> = ConstVar(Sound.EMPTY),
    @Help("Armor components for additional functionality") val components: List<ArmorComponent> = emptyList(),
    @Help("item components for additional functionality") val itemComponent: List<ItemComponent> = emptyList()
) : ItemTypeComponent {

    override fun build(itemStack: ItemStack, id: String): ItemStack {
        val oldMeta: ItemMeta = itemStack.itemMeta ?: return itemStack
        val displayName = oldMeta.displayName()
        val lore = oldMeta.lore()
        val customModelData = if (oldMeta.hasCustomModelData()) oldMeta.customModelData else null

        val materialKey = itemStack.type.key.toString()
        val slot = armorSlotOf(armorType)
        val armorJson = buildArmorJson(materialKey, slot)
        val newItem = Bukkit.getItemFactory().createItemStack(armorJson)
        val newMeta = newItem.itemMeta

        val pdc = newMeta.persistentDataContainer
        pdc.set(ENTRY_ID, PersistentDataType.STRING, id)

        newMeta.displayName(displayName)
        newMeta.lore(lore)
        newMeta.setCustomModelData(customModelData)
        newItem.itemMeta = newMeta

        val afterTypeSpecific = components.fold(newItem) { acc, c -> c.build(acc) }
        return itemComponent.fold(afterTypeSpecific) { acc, c -> c.build(acc) }
    }

    private fun armorSlotOf(type: ArmorType): String {
        return when (type) {
            ArmorType.HELMET -> "head"
            ArmorType.CHESTPLATE -> "chest"
            ArmorType.LEGGINGS -> "legs"
            ArmorType.BOOTS -> "feet"
        }
    }

    private fun buildArmorJson(materialKey: String, slot: String): String {
        val effectiveAssetId = assetId.ifBlank { "minecraft:diamond" }

        val soundString = if (sound is ConstVar<Sound> && sound.value != Sound.EMPTY) {
            sound.value.soundId.namespacedKey?.toString()
        } else null

        return buildString {
            append("$materialKey[")
            append("max_stack_size=1,")
            append("max_damage=$durability,")
            append("damage=0,")
            append("equippable={slot:\"$slot\",asset_id:\"$effectiveAssetId\"")
            if (soundString != null) {
                append(",equip_sound:\"$soundString\"")
            }
            append(",dispensable:true},")
            append("attribute_modifiers=[")
            append("{type:\"minecraft:armor\",amount:$armorValue,operation:\"add_value\",slot:\"$slot\",id:\"chaos:${slot}_armor_value\"},")
            append("{type:\"minecraft:armor_toughness\",amount:$armorToughness,operation:\"add_value\",slot:\"$slot\",id:\"chaos:${slot}_armor_toughness\"}")
            append("]")

            val componentStrings = components.mapNotNull { it.getFactoryString().takeIf { str -> str.isNotBlank() } }
            if (componentStrings.isNotEmpty()) {
                append(",")
                append(componentStrings.joinToString(","))
            }

            append("]")
        }
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        components.forEach { component ->
            component.execute(player, itemStack)
        }
    }

    companion object {
        val ENTRY_ID = NamespacedKey("chaos", "armor_entry_id")


        fun getArmorID(itemStack: ItemStack): String? {
            val meta = itemStack.itemMeta ?: return null
            return meta.persistentDataContainer.get(ENTRY_ID, PersistentDataType.STRING)
        }
    }
}