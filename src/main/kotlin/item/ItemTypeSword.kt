package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.extension.annotations.Help
import de.chaos.data.SwordStats
import de.chaos.item.itemComponents.ItemComponent
import de.chaos.item.swordComponents.SwordComponent
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

@AlgebraicTypeInfo("sword", Colors.BLUE, "material-symbols:swords-rounded")
class ItemTypeSword(
    @Help("Base damage of the sword") val baseDamage: Double = 5.0,
    @Help("Base attack speed of the sword") val attackSpeed: Double = 1.6,
    @Help("Base critical hit chance (0.0 to 1.0)") val basecritChance: Double = 0.1,
    @Help("Base critical hit damage multiplier") val basecritMultiplier: Double = 1.5,
    val durability: Int = 250,
    @Help("Sword components for additional functionality") val components: List<SwordComponent> = emptyList(),
    @Help("item components for additional functionality") val itemComponent: List<ItemComponent> = emptyList()

) : ItemTypeComponent {

    override fun build(itemStack: ItemStack, id: String): ItemStack {
        val oldMeta: ItemMeta = itemStack.itemMeta ?: return itemStack

        val oldDisplayName = oldMeta.displayName()
        val oldLore = oldMeta.lore() ?: emptyList()

        val swordJson = buildSwordJson(itemStack.type.key.toString())
        val newItem = Bukkit.getItemFactory().createItemStack(swordJson)
        val newMeta = newItem.itemMeta

        val pdc = newMeta.persistentDataContainer
        pdc.set(KEY_BASE_DAMAGE, PersistentDataType.DOUBLE, baseDamage)
        pdc.set(KEY_BASE_CRIT_CHANCE, PersistentDataType.DOUBLE, basecritChance)
        pdc.set(KEY_BASE_CRIT_MULTIPLIER, PersistentDataType.DOUBLE, basecritMultiplier)
        pdc.set(ENTRY_ID, PersistentDataType.STRING, id)

        newMeta.displayName(oldDisplayName)
        newMeta.lore(oldLore)

        newItem.itemMeta = newMeta

        val afterTypeSpecific = components.fold(newItem) { acc, c -> c.build(acc) }
        return itemComponent.fold(afterTypeSpecific) { acc, c -> c.build(acc) }
    }


    private fun buildSwordJson(materialKey: String): String {
        return buildString {
            append("$materialKey[")
            append("weapon={},")
            append("max_stack_size=1,")
            append("max_damage=$durability,")
            append("damage=0,")
            append("attribute_modifiers=[")
            append("{type:\"minecraft:attack_speed\",amount:$attackSpeed,operation:\"add_value\",slot:\"mainhand\",id:\"chaos:sword_attack_speed\"}")
            append("]")
            append("]")
        }
    }

    companion object {
        val KEY_BASE_DAMAGE = NamespacedKey("chaos", "sword_base_damage")
        val KEY_BASE_CRIT_CHANCE = NamespacedKey("chaos", "sword_base_crit_chance")
        val KEY_BASE_CRIT_MULTIPLIER = NamespacedKey("chaos", "sword_base_crit_multiplier")
        val ENTRY_ID = NamespacedKey("chaos", "entry_id")


        fun getSwordStats(itemStack: ItemStack): SwordStats? {
            val meta = itemStack.itemMeta ?: return null
            val pdc = meta.persistentDataContainer

            val baseDamage = pdc.get(KEY_BASE_DAMAGE, PersistentDataType.DOUBLE) ?: return null
            val critChance = pdc.get(KEY_BASE_CRIT_CHANCE, PersistentDataType.DOUBLE) ?: return null
            val critMultiplier = pdc.get(KEY_BASE_CRIT_MULTIPLIER, PersistentDataType.DOUBLE) ?: return null

            return SwordStats(baseDamage, critChance, critMultiplier)
        }


        fun getSwordID(itemStack: ItemStack): String? {
            val meta = itemStack.itemMeta ?: return null
            val pdc = meta.persistentDataContainer
            return pdc.get(ENTRY_ID, PersistentDataType.STRING)
        }
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        components.forEach { component -> component.execute(player, itemStack) }
    }
}




