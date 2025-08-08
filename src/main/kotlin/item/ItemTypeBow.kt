package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.bowComponents.BowComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

@AlgebraicTypeInfo("bow", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeBow(
    val damageMultiplier: Double = 1.0, // Multiplikator für den Basisschaden
    val drawSpeedModifier: Double = 1.0, // z.B. 1.2 für 20% schnelleres Ziehen
    val components: List<BowComponent> = emptyList()
) : ItemTypeComponent {

    companion object {
        // Eindeutige Schlüssel zum Speichern und Abrufen der Daten vom Item.
        // Ersetzen Sie "chaos" durch Ihren eigenen Plugin-Namen.
        val KEY_DAMAGE = NamespacedKey("chaos", "bow_damage_multiplier")
        val KEY_DRAW_SPEED = NamespacedKey("chaos", "bow_draw_speed_modifier")
    }

    /**
     * Baut den Bogen-ItemStack, indem es Name, Lore und die benutzerdefinierten
     * Bogen-Eigenschaften im Persistent Data Container des Items speichert.
     */
    override fun build(itemStack: ItemStack): ItemStack {
        itemStack.editMeta { meta ->

            val lore = mutableListOf<Component>()
            lore.add(Component.empty())
            lore.add(Component.text("Schadensmultiplikator: ", NamedTextColor.GRAY).append(Component.text("${damageMultiplier}x", NamedTextColor.BLUE)))
            lore.add(Component.text("Zieh-Geschwindigkeit: ", NamedTextColor.GRAY).append(Component.text("${drawSpeedModifier}x", NamedTextColor.BLUE)))
            lore.add(Component.empty())

            // Hier könnten Beschreibungen der BowComponent-Instanzen hinzugefügt werden
            // components.forEach { lore.addAll(it.getLore()) }

            meta.lore(lore)

            // 2. Benutzerdefinierte Daten im Persistent Data Container (PDC) speichern
            val dataContainer = meta.persistentDataContainer
            dataContainer.set(KEY_DAMAGE, PersistentDataType.DOUBLE, damageMultiplier)
            dataContainer.set(KEY_DRAW_SPEED, PersistentDataType.DOUBLE, drawSpeedModifier)

            // 3. Dem Item einen visuellen Glanz verleihen, ohne eine echte Verzauberung anzuzeigen
            meta.addEnchant(Enchantment.INFINITY, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

            meta.isUnbreakable = true
        }

        return itemStack
    }

    /**
     * Führt die Logik aus, die direkt mit diesem Bogen verbunden ist.
     * Dies delegiert die Ausführung an die untergeordneten BowComponent-Instanzen.
     * Die eigentliche Bogen-Funktionalität (Schaden, etc.) wird über Events gesteuert.
     */
    override fun execute(player: Player, itemStack: ItemStack) {
        components.forEach { component ->
            component.execute(player, itemStack)
        }
    }
}