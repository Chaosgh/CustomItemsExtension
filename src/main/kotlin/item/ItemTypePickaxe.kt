package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.pickaxeComponents.PickaxeComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@AlgebraicTypeInfo("pickaxe", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypePickaxe(
    val efficiencyLevel: Int = 5,
    val fortuneLevel: Int = 0,
    val customDurability: Int = 1500,
    val components: List<PickaxeComponent> = emptyList(),
) : ItemTypeComponent {

    companion object {
        // Schlüssel zum Speichern der Haltbarkeit. Ersetzen Sie "chaos" durch Ihren Plugin-Namen.
        val KEY_DURABILITY = NamespacedKey("chaos", "pickaxe_durability")
        val KEY_MAX_DURABILITY = NamespacedKey("chaos", "pickaxe_max_durability")
    }

    /**
     * Baut den ItemStack der Spitzhacke. Es werden Verzauberungen für Effizienz/Glück
     * und benutzerdefinierte Daten für die Haltbarkeit hinzugefügt.
     */
    override fun build(itemStack: ItemStack): ItemStack {
        itemStack.editMeta { meta ->

            val lore = mutableListOf<Component>()
            lore.add(Component.empty())
            if (efficiencyLevel > 0) {
                lore.add(Component.text("Effizienz $efficiencyLevel", NamedTextColor.GRAY))
            }
            if (fortuneLevel > 0) {
                lore.add(Component.text("Glück $fortuneLevel", NamedTextColor.GRAY))
            }
            lore.add(Component.text("Haltbarkeit: ", NamedTextColor.GRAY)
                .append(Component.text("$customDurability / $customDurability", NamedTextColor.WHITE))
            )
            lore.add(Component.empty())

            // Hier könnten Beschreibungen von PickaxeComponent-Instanzen folgen
            // components.forEach { lore.add(it.getDescription()) }

            meta.lore(lore)

            // 2. Verzauberungen für Standard-Funktionen hinzufügen
            if (efficiencyLevel > 0) {
                meta.addEnchant(Enchantment.EFFICIENCY, efficiencyLevel, true)
            }
            if (fortuneLevel > 0) {
                meta.addEnchant(Enchantment.LOOTING, fortuneLevel, true)
            }

            // 3. Benutzerdefinierte Haltbarkeit im Persistent Data Container speichern
            val data = meta.persistentDataContainer
            data.set(KEY_DURABILITY, PersistentDataType.INTEGER, customDurability)
            data.set(KEY_MAX_DURABILITY, PersistentDataType.INTEGER, customDurability)

            // 4. Verhindern, dass Minecrafts eigenes Haltbarkeitssystem greift
            meta.isUnbreakable = true
            // Versteckt die "Unzerstörbar" und Verzauberungs-Texte in der Lore
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
        }
        return itemStack
    }

    /**
     * Führt eine Aktion aus, die direkt mit dieser Spitzhacke verbunden ist.
     * Dies könnte eine Rechtsklick-Fähigkeit sein (z.B. "Bereichs-Abbau aktivieren").
     */
    override fun execute(player: Player, itemStack: ItemStack) {
        // Delegiert die Ausführung an Sub-Komponenten für spezielle Fähigkeiten
        components.forEach { component ->
            component.execute(player, itemStack)
        }
        player.sendActionBar(Component.text("Spitzhacken-Fähigkeit genutzt!", NamedTextColor.AQUA))
    }
}