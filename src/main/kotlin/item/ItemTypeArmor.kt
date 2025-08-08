package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.enums.ArmorType
import de.chaos.item.armorComponents.ArmorComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.attribute.Attribute // <-- WICHTIGER IMPORT
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot // <-- WICHTIGER IMPORT
import org.bukkit.inventory.ItemStack
import java.util.*

@AlgebraicTypeInfo("armor", Colors.BLUE, "fa6-solid:hashtag")
class ItemTypeArmor(
    val type: ArmorType = ArmorType.HELMET, // Ihr Enum, z.B. ArmorType.HELMET
    val armorValue: Double = 1.0,
    val components: List<ArmorComponent> = emptyList()
) : ItemTypeComponent {

    override fun build(itemStack: ItemStack): ItemStack {
        // Der passende EquipmentSlot wird hier basierend auf Ihrem Enum ermittelt
        val equipmentSlot = when (type) {
            ArmorType.HELMET -> EquipmentSlot.HEAD
            ArmorType.CHESTPLATE -> EquipmentSlot.CHEST
            ArmorType.LEGGINGS -> EquipmentSlot.LEGS
            ArmorType.BOOTS -> EquipmentSlot.FEET
            // Fügen Sie hier weitere Typen hinzu, falls Ihr Enum mehr hat
        }

        itemStack.editMeta { meta ->

            val lore = mutableListOf<Component>()
            lore.add(Component.empty()) // Leerzeile für besseres Layout
            lore.add(
                Component.text("Wenn ausgerüstet in Slot: ", NamedTextColor.GRAY)
                    .append(Component.text(equipmentSlot.name.lowercase(), NamedTextColor.BLUE))
            )
            lore.add(Component.empty())

            // TODO: Fügen Sie hier Logik hinzu, um Beschreibungen von Ihren 'ArmorComponent'-Klassen zu erhalten.
            // Beispiel:
            // components.forEach { component ->
            //     lore.add(component.getLoreDescription())
            // }

            meta.lore(lore)

            // Attribute-Modifier für den Rüstungswert hinzufügen
            val modifier = AttributeModifier(
                UUID.randomUUID(), // Einzigartige ID für diesen Modifier
                "generic.armor",   // Der Name des Minecraft-Attributs für Rüstung
                armorValue,        // Der Wert, der hinzugefügt wird
                AttributeModifier.Operation.ADD_NUMBER,
                equipmentSlot      // Der Bonus ist nur aktiv, wenn das Item im richtigen Slot ist
            )

            // Das Attribut zum Item hinzufügen
            meta.addAttributeModifier(Attribute.ARMOR, modifier)

            // Weitere nützliche Eigenschaften
            meta.isUnbreakable = true
        }
        return itemStack
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        // Delegiert die Ausführung an die untergeordneten Komponenten
        components.forEach { component ->
            component.execute(player, itemStack)
        }
    }
}