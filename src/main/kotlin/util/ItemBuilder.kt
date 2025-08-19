package de.chaos.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class ItemBuilder(material: Material) {
    private var itemStack: ItemStack = ItemStack(material)
    private var itemMeta: ItemMeta = itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(material)!!

    constructor(itemStack: ItemStack) : this(itemStack.type) {
        this.itemStack = itemStack.clone()
        this.itemMeta = this.itemStack.itemMeta ?: Bukkit.getItemFactory().getItemMeta(itemStack.type)!!
    }


    fun setCustomModelData(data: Int): ItemBuilder {
        val modelDataComponent = itemMeta.customModelDataComponent
        modelDataComponent.floats = listOf(data.toFloat())
        itemMeta.setCustomModelDataComponent(modelDataComponent)
        return this
    }


    fun setCustomModelData(data: Int?): ItemBuilder {
        val modelDataComponent = itemMeta.customModelDataComponent

        if (data == null) {
            modelDataComponent.floats = emptyList()
        } else {
            modelDataComponent.floats = listOf(data.toFloat())
        }
        itemMeta.setCustomModelDataComponent(modelDataComponent)
        return this
    }


    fun setCustomToolTipStyle(key: NamespacedKey): ItemBuilder {
        itemMeta.tooltipStyle = key
        return this
    }

    fun hideTooltips(hide: Boolean): ItemBuilder {
        itemMeta.isHideTooltip = hide
        return this
    }


    fun setEnchantable(enchantable: Int): ItemBuilder {
        itemMeta.setEnchantable(enchantable)
        return this
    }


    fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}