package de.chaos.item

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface BaseComponent {
    fun build(itemStack: ItemStack): ItemStack
    fun execute(player: Player, itemStack: ItemStack)
}