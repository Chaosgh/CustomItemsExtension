package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.swordComponents.SwordComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

@AlgebraicTypeInfo("sword", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeSword(
    val baseDamage: Double = 5.0,
    val attackSpeed: Double = -2.4, // Standard-Schwert-Geschwindigkeit ist 1.6 (Basis 4.0 + Modifier -2.4)
    val critChance: Double = 0.1,   // 10% Chance
    val critMultiplier: Double = 1.5, // 50% mehr Schaden
    val components: List<SwordComponent> = emptyList(),
) : ItemTypeComponent {

    companion object {
        // Eindeutige Schlüssel zum Speichern der Crit-Daten. Ersetzen Sie "chaos" durch Ihren Plugin-Namen.
        val KEY_CRIT_CHANCE = NamespacedKey("chaos", "sword_crit_chance")
        val KEY_CRIT_MULTIPLIER = NamespacedKey("chaos", "sword_crit_multiplier")
    }

    /**
     * Baut den ItemStack für das Schwert. Setzt Standard-Attribute für Schaden/Geschwindigkeit
     * und speichert benutzerdefinierte Werte wie die Crit-Chance im Persistent Data Container.
     */
    override fun build(itemStack: ItemStack): ItemStack {
        itemStack.editMeta { meta ->

            val lore = mutableListOf<Component>()
            lore.add(Component.empty())
            lore.add(Component.text("Wenn in Haupthand:", NamedTextColor.GRAY))
            lore.add(Component.text(" ${baseDamage.toInt()} Angriffsschaden", NamedTextColor.DARK_GREEN))
            lore.add(Component.text(" ${4.0 + attackSpeed} Angriffsgeschwindigkeit", NamedTextColor.DARK_GREEN))
            lore.add(Component.empty())
            lore.add(Component.text("Crit-Chance: ", NamedTextColor.GRAY).append(Component.text("${(critChance * 100).toInt()}%", NamedTextColor.AQUA)))
            lore.add(Component.text("Crit-Schaden: ", NamedTextColor.GRAY).append(Component.text("+${((critMultiplier - 1.0) * 100).toInt()}%", NamedTextColor.AQUA)))
            lore.add(Component.empty())

            // Hier könnten Beschreibungen von SwordComponent-Instanzen folgen
            // components.forEach { lore.add(it.getDescription()) }

            meta.lore(lore)

            // 2. Standard-Attribute für den Kampf setzen
            val damageModifier = AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "generic.attack_damage", baseDamage - 1.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
            val speedModifier = AttributeModifier(UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA4"), "generic.attack_speed", attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, damageModifier)
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, speedModifier)

            // 3. Benutzerdefinierte Crit-Werte im PDC speichern
            val data = meta.persistentDataContainer
            data.set(KEY_CRIT_CHANCE, PersistentDataType.DOUBLE, critChance)
            data.set(KEY_CRIT_MULTIPLIER, PersistentDataType.DOUBLE, critMultiplier)

            // 4. Item-Flags für ein sauberes Aussehen
            meta.isUnbreakable = true
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
        }
        return itemStack
    }

    /**
     * Führt eine aktive Fähigkeit des Schwertes aus (z.B. per Rechtsklick).
     * Die passive Crit-Logik wird über Events gesteuert.
     */
    override fun execute(player: Player, itemStack: ItemStack) {
        // Delegiert die Ausführung an Sub-Komponenten für spezielle aktive Fähigkeiten
        components.forEach { component ->
            component.execute(player, itemStack)
        }
        player.sendActionBar(Component.text("Schwert-Fähigkeit eingesetzt!", NamedTextColor.RED))
    }
}