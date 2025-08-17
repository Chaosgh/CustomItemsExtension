package de.chaos.data

import org.bukkit.Material

data class ToolRule(
    val blocks: List<Material> = emptyList(),
    val speed: Double? = null,
    val correctForDrops: Boolean? = null
)