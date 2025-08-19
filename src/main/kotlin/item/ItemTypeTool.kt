package de.chaos.item

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.extension.annotations.Help
import de.chaos.data.ToolRule
import de.chaos.enums.ToolType
import de.chaos.item.itemComponents.ItemComponent
import de.chaos.item.toolComponents.ToolComponent
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@AlgebraicTypeInfo("tool", Colors.BLUE, "material-symbols:construction")
class ItemTypeTool(
    @Help("The default mining speed of this tool.") val breakSpeed: Double = 1.0,
    @Help("The maximum durability (max_damage) of the item.") val durability: Int = 59,
    @Help("The type of the tool (e.g., PICKAXE), which may define a default block tag.") val toolType: ToolType = ToolType.PICKAXE,
    @Help("The amount of durability lost each time a block is broken.") val damagePerBlock: Int = 1,
    @Help("If true, the tool can break blocks in creative mode.") val canDestroyBlocksInCreative: Boolean = true,
    @Help("A list of rules for how this tool interacts with specific blocks.") val rules: List<ToolRule> = emptyList(),
    @Help("Tool components for additional functionality.") val components: List<ToolComponent> = emptyList(),
    @Help("Item components for additional functionality.") val itemComponent: List<ItemComponent> = emptyList()
) : ItemTypeComponent {

    override fun build(itemStack: ItemStack, id: String): ItemStack {
        val oldMeta = itemStack.itemMeta ?: return itemStack
        val displayName = oldMeta.displayName()
        val lore = oldMeta.lore()
        val customModelData = if (oldMeta.hasCustomModelData()) oldMeta.customModelData else null

        val materialKey = itemStack.type.key.toString()
        val toolJson = buildToolJson(materialKey, itemStack.type)
        val newItem = org.bukkit.Bukkit.getItemFactory().createItemStack(toolJson)
        val newMeta = newItem.itemMeta

        val pdc = newMeta.persistentDataContainer
        pdc.set(ENTRY_ID, org.bukkit.persistence.PersistentDataType.STRING, id)

        newMeta.displayName(displayName)
        newMeta.lore(lore)
        newMeta.setCustomModelData(customModelData)
        newItem.itemMeta = newMeta

        val afterTypeSpecific = components.fold(newItem) { acc, c -> c.build(acc) }
        return itemComponent.fold(afterTypeSpecific) { acc, c -> c.build(acc) }
    }

    private fun buildToolJson(materialKey: String, originalMaterial: org.bukkit.Material): String {
        val validBreakSpeed = breakSpeed.coerceAtLeast(0.1)

        val effectiveRules = rules.ifEmpty {
            if (toolType.tag.isNotBlank()) {
                listOf(
                    ToolRule(
                        blocks = emptyList(),
                        speed = validBreakSpeed,
                        correctForDrops = true
                    )
                )
            } else emptyList()
        }

        val rulesJson = buildString {
            append("[")
            effectiveRules.forEachIndexed { index, rule ->
                if (rule.blocks.isEmpty() && toolType.tag.isBlank()) return@forEachIndexed

                if (index > 0) append(",")
                append("{")

                val blockIdentifier = if (rule.blocks.isNotEmpty()) {
                    if (rule.blocks.size == 1) {
                        "\"minecraft:${rule.blocks.first().name.lowercase()}\""
                    } else {
                        rule.blocks.joinToString(prefix = "[", postfix = "]") {
                            "\"minecraft:${it.name.lowercase()}\""
                        }
                    }
                } else {
                    "\"${toolType.tag}\""
                }

                append("blocks:$blockIdentifier")

                rule.speed?.let {
                    val validRuleSpeed = it.coerceAtLeast(0.1)
                    append(",speed:$validRuleSpeed")
                }
                rule.correctForDrops?.let { append(",correct_for_drops:$it") }

                append("}")
            }
            append("]")
        }

        return buildString {
            append("$materialKey[")
            append("max_stack_size=1,")
            append("max_damage=$durability,")
            append("damage=0,")
            append("tool={")
            append("default_mining_speed:$validBreakSpeed,")
            append("damage_per_block:$damagePerBlock,")
            append("can_destroy_blocks_in_creative:$canDestroyBlocksInCreative,")
            append("rules:$rulesJson")
            append("}")
            append("]")
        }
    }

    override fun execute(player: Player, itemStack: ItemStack) {
        components.forEach { component -> component.execute(player, itemStack) }
    }

    companion object {
        val ENTRY_ID = org.bukkit.NamespacedKey("chaos", "entry_id")

        fun getToolID(itemStack: ItemStack): String? {
            val meta = itemStack.itemMeta ?: return null
            return meta.persistentDataContainer.get(ENTRY_ID, org.bukkit.persistence.PersistentDataType.STRING)
        }


    }
}