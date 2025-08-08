package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.axeComponents.AxeComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.UUID

@AlgebraicTypeInfo("axe", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeAxe(
    val baseDamage: Double = 7.0,
    val attackSpeed: Double = -3.0, // Typischer Axt-Wert, -4.0 ist Basiswert
    val components: List<AxeComponent> = emptyList()
) : ItemTypeComponent {

    /**
     * Baut den ItemStack für diese Axt, indem Name, Lore und Attribute gesetzt werden.
     * Dies ist der moderne Weg, um den Angriffsschaden eines Items zu definieren.
     */
    @Suppress("DEPRECATION")
    override fun build(itemStack: ItemStack): ItemStack {
        itemStack.editMeta { meta ->

            val lore = mutableListOf<Component>()
            lore.add(Component.empty())
            lore.add(Component.text("Wenn in Haupthand:", NamedTextColor.GRAY))
            lore.add(
                Component.text(" ${baseDamage.toInt()} Angriffsschaden", NamedTextColor.DARK_GREEN)
            )
            lore.add(
                Component.text(" ${1.0 + attackSpeed} Angriffsgeschwindigkeit", NamedTextColor.DARK_GREEN) // Besserer Wert als direktes attackSpeed
            )
            lore.add(Component.empty())

            // Hier könnten die Beschreibungen der Sub-Komponenten (AxeComponent) hinzugefügt werden
            // Beispiel:
            // components.forEach { component ->
            //    lore.addAll(component.getLoreLines())
            // }

            meta.lore(lore)

            // 2. Attribute für Schaden und Geschwindigkeit hinzufügen
            val damageModifier = AttributeModifier(
                UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), // Feste UUID für Schadens-Attribut
                "generic.attack_damage",
                baseDamage - 1.0, // Minecraft addiert standardmäßig 1 Schaden, daher abziehen
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND // Nur aktiv, wenn in der Hand gehalten
            )

            val speedModifier = AttributeModifier(
                UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA4"), // Feste UUID für Geschwindigkeits-Attribut
                "generic.attack_speed",
                attackSpeed,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
            )

            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, damageModifier)
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, speedModifier)

            meta.isUnbreakable = true
        }

        return itemStack
    }

    /**
     * Führt die Logik aus, die mit dieser Axt verbunden ist.
     * Delegiert die Ausführung an die untergeordneten AxeComponent-Instanzen.
     * Dies könnte zum Beispiel beim Schlagen einer Entity oder beim Abbauen eines Blocks aufgerufen werden.
     */
    override fun execute(player: Player, itemStack: ItemStack) {
        // Die Logik der Sub-Komponenten ausführen
        components.forEach { component ->
            component.execute(player, itemStack)
        }
        // Beispiel-Feedback für den Spieler
        player.sendActionBar(Component.text("Axt-Fähigkeit genutzt!", NamedTextColor.GOLD))
    }
}