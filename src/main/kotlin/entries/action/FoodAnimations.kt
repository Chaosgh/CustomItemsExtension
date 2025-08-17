package de.chaos.entries.action


enum class FoodAnimations(val animationName: String, val description: String) {
    NONE("none", "No animation, item is consumed instantly"),
    EAT("eat", "Standard eating animation with hand movement"),
    DRINK("drink", "Drinking animation, tilts item upward like a bottle"),
    BLOCK("block", "Blocking animation, used for shields"),
    BOW("bow", "Bow pulling animation"),
    SPEAR("spear", "Trident throwing animation"),
    CROSSBOW("crossbow", "Crossbow loading animation"),
    SPYGLASS("spyglass", "Spyglass looking animation"),
    TOOT_HORN("toot_horn", "Goat horn tooting animation"),
    BRUSH("brush", "Brush animation, used for archaeology");

    companion object {

        fun fromString(name: String): FoodAnimations? {
            return values().find { it.animationName.equals(name, ignoreCase = true) }
        }


        fun getAnimationNames(): List<String> {
            return values().map { it.animationName }
        }
    }

    override fun toString(): String = animationName
}
