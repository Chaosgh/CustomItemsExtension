package de.chaos.item.consumableComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import de.chaos.item.BaseComponent

@AlgebraicTypeInfo("consumable_component_holder", Colors.BLUE, "fa6-solid:puzzle-piece")
sealed interface ConsumableComponentHolder : BaseComponent {
    fun getComponent(): ConsumableComponent
}