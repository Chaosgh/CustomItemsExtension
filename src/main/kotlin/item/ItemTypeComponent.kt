package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("item type", Colors.BLUE, "fa6-solid:hashtag")
sealed interface ItemTypeComponent {

    /**
     * Wendet die spezifischen Eigenschaften dieser Komponente auf einen gegebenen ItemStack an.
     * Dies umfasst typischerweise das Setzen von DisplayName, Lore und anderen Metadaten.
     *
     * @param itemStack Der Basis-ItemStack, der modifiziert werden soll.
     * @return Der modifizierte ItemStack.
     */
    fun build(itemStack: ItemStack): ItemStack

    /**
     * Führt die Logik dieses Item-Typs aus, wenn das Item benutzt wird.
     *
     * @param player Der Spieler, der die Aktion ausführt.
     * @param itemStack Das Item, das die Aktion ausgelöst hat.
     */
    fun execute(player: Player, itemStack: ItemStack)

}