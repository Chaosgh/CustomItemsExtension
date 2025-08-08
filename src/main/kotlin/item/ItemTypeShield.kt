package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.shieldComponents.ShieldComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

@AlgebraicTypeInfo("shield", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeShield(
    val blockChance: Double = 0.25, // 25% chance to block
    val damageReduction: Double = 0.5, // 50% damage reduction on successful block
    val components: List<ShieldComponent> = emptyList(),
) : ItemTypeComponent {

    companion object {
        // Unique keys for storing and retrieving shield data.
        // Replace "chaos" with your plugin's namespace.
        val KEY_BLOCK_CHANCE = NamespacedKey("chaos", "shield_block_chance")
        val KEY_DAMAGE_REDUCTION = NamespacedKey("chaos", "shield_damage_reduction")
    }

    /**
     * custom stats in the Persistent Data Container.
     */
    override fun build(itemStack: ItemStack): ItemStack {
        // Ensure the base item is a shield to apply BannerMeta
        if (itemStack.type != org.bukkit.Material.SHIELD) {
            // Or throw an exception, depending on desired behavior
            return itemStack
        }

        itemStack.editMeta { meta ->

            val lore = mutableListOf<Component>()
            lore.add(Component.empty())
            lore.add(Component.text("Block-Chance: ", NamedTextColor.GRAY)
                .append(Component.text("${(blockChance * 100).toInt()}%", NamedTextColor.WHITE)))
            lore.add(Component.text("Schadensreduktion: ", NamedTextColor.GRAY)
                .append(Component.text("-${(damageReduction * 100).toInt()}%", NamedTextColor.WHITE)))
            lore.add(Component.empty())

            // Descriptions from ShieldComponents could be added here
            // components.forEach { lore.add(it.getDescription()) }

            meta.lore(lore)

            // 2. Store custom shield stats in the Persistent Data Container (PDC)
            val data = meta.persistentDataContainer
            data.set(KEY_BLOCK_CHANCE, PersistentDataType.DOUBLE, blockChance)
            data.set(KEY_DAMAGE_REDUCTION, PersistentDataType.DOUBLE, damageReduction)



            // Hide default info like "Unbreakable"
            meta.isUnbreakable = true
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
        }
        return itemStack
    }

    /**
     * Executes logic associated with this shield, such as an active ability (e.g., a shield bash).
     * The core blocking logic is handled in an event listener.
     */
    override fun execute(player: Player, itemStack: ItemStack) {
        // Delegate to sub-components for active abilities
        components.forEach { component ->
            component.execute(player, itemStack)
        }
        player.sendActionBar(Component.text("Schild-Fähigkeit aktiviert!", NamedTextColor.YELLOW))
    }
}