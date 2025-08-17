package de.chaos.logic

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.EntityType
import org.bukkit.entity.TextDisplay
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import kotlin.random.Random

class DamageIndicator(private val plugin: Plugin) {

    /**
     * Zeigt einen animierten Schadens-Indikator über einem Mob
     */
    fun showDamage(
        location: Location,
        damage: Double,
        isCrit: Boolean
    ) {
        val world = location.world ?: return

        // Position leicht über dem Mob mit zufälliger X/Z Verschiebung
        val displayLocation = location.clone().add(
            Random.nextDouble(-0.5, 0.5), // X offset
            2.2, // Y offset (über dem Mob)
            Random.nextDouble(-0.5, 0.5)  // Z offset
        )

        // Text Display Entity erstellen
        val textDisplay = world.spawnEntity(displayLocation, EntityType.TEXT_DISPLAY) as TextDisplay

        // Schaden formatieren
        val damageText = String.format("%.1f", damage)
        val displayText = if (isCrit) {
            "§6§l✦ §c§l$damageText §6§l✦"
        } else {
            "§f$damageText"
        }

        // Text konfigurieren
        textDisplay.text = displayText
        textDisplay.billboard = Display.Billboard.CENTER
        textDisplay.isSeeThrough = false
        textDisplay.backgroundColor = Color.fromARGB(0, 0, 0, 0) // Transparent

        // Größe basierend auf Crit
        val scale = if (isCrit) 1.2f else 0.8f
        val initialTransform = Transformation(
            Vector3f(0f, 0f, 0f), // Translation
            AxisAngle4f(0f, 0f, 0f, 1f), // Rotation
            Vector3f(scale, scale, scale), // Scale
            AxisAngle4f(0f, 0f, 0f, 1f)  // Right rotation
        )
        textDisplay.transformation = initialTransform

        // Animation starten
        animateIndicator(textDisplay, isCrit, damage)
    }

    private fun animateIndicator(
        textDisplay: TextDisplay,
        isCrit: Boolean,
        originalDamage: Double
    ) {
        var ticksElapsed = 0
        val totalTicks = if (isCrit) 60 else 40 // Crit bleibt länger

        object : BukkitRunnable() {
            override fun run() {
                if (!textDisplay.isValid || ticksElapsed >= totalTicks) {
                    textDisplay.remove()
                    cancel()
                    return
                }

                val progress = ticksElapsed.toFloat() / totalTicks.toFloat()

                // Aufwärtsbewegung
                val yOffset = if (isCrit) {
                    0.8f * progress // Crit steigt höher
                } else {
                    0.4f * progress
                }

                // Fade out gegen Ende
                val alpha = if (progress > 0.7f) {
                    ((1.0f - progress) / 0.3f).coerceIn(0f, 1f)
                } else {
                    1.0f
                }

                // Scale animation für Crits
                val currentScale = if (isCrit) {
                    val scaleProgress = (ticksElapsed.toFloat() / 15f).coerceIn(0f, 1f)
                    if (ticksElapsed < 15) {
                        // Grow phase
                        0.8f + (0.6f * scaleProgress) // 0.8 to 1.4
                    } else {
                        // Shrink phase
                        1.4f - (0.2f * (progress - 0.25f).coerceIn(0f, 1f))
                    }
                } else {
                    0.8f - (0.2f * progress) // Normal damage shrinks slowly
                }

                // Transformation updaten
                val newTransform = Transformation(
                    Vector3f(0f, yOffset, 0f), // Translation (upward movement)
                    AxisAngle4f(0f, 0f, 0f, 1f), // Rotation
                    Vector3f(currentScale, currentScale, currentScale), // Scale
                    AxisAngle4f(0f, 0f, 0f, 1f)  // Right rotation
                )

                textDisplay.transformation = newTransform

                // Text fadeout durch leeren Text simulieren
                if (alpha < 0.3f) {
                    textDisplay.text = ""
                }

                ticksElapsed++
            }
        }.runTaskTimer(plugin, 0L, 1L)
    }
}