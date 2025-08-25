package de.chaos.item.bowComponents

import com.typewritermc.core.books.pages.Colors
import com.typewritermc.core.extension.annotations.AlgebraicTypeInfo
import com.typewritermc.core.extension.annotations.Help
import com.typewritermc.engine.paper.plugin
import org.bukkit.NamespacedKey
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

@AlgebraicTypeInfo("bow tracer", Colors.BLUE, "fa6-solid:hashtag")
class BowTracer(
    @Help("Scan radius around the arrow to search for targets.") val distanz: Double = 5.0,
    @Help("Allowed entity types to target; empty = all living except players.") val mobs: List<EntityType> = emptyList(),
    @Help("Steering factor per tick (0.0..1.0). Higher = more aggressive turning.") val steer: Double = 0.15,
    @Help("Maximum duration (ticks) the arrow will steer (safety limit).") val maxDuration: Int = 100,
    @Help("Maximum travel distance (blocks) from the start while steering.") val maxRange: Double = 64.0,
    @Help("Cooldown (ticks) between shots per player.") val cooldown: Int = 20
) : BowComponent {

    companion object {
        internal val KEY_TRACER = NamespacedKey("chaos", "bow_tracer")
        private val playerCooldowns = ConcurrentHashMap<UUID, Long>()
        private val activeTrackers = ConcurrentHashMap<UUID, BukkitRunnable>()

        // Global tracking system for MultiShot compatibility
        internal val pendingTracers = ConcurrentHashMap<UUID, TracerConfig>()

        init {
            // Global arrow listener that applies tracer to any arrow shot by players with pending tracers
            object : BukkitRunnable() {
                override fun run() {
                    if (!plugin.isEnabled) return

                    // Find all arrows in all worlds and apply tracer if needed
                    plugin.server.worlds.forEach { world ->
                        world.entities.filterIsInstance<Arrow>().forEach { arrow ->
                            val shooter = arrow.shooter as? Player ?: return@forEach
                            val config = pendingTracers[shooter.uniqueId] ?: return@forEach

                            // Check if arrow was shot recently (extended window for multishot compatibility)
                            if (System.currentTimeMillis() - config.shotTime > 500) return@forEach

                            // Apply tracer if not already applied
                            if (!arrow.persistentDataContainer.has(KEY_TRACER, PersistentDataType.BYTE)) {
                                arrow.persistentDataContainer.set(KEY_TRACER, PersistentDataType.BYTE, 1)
                                startArrowTracking(arrow, shooter, config)
                            }
                        }
                    }

                    // Clean up old pending tracers
                    val now = System.currentTimeMillis()
                    pendingTracers.entries.removeIf { now - it.value.shotTime > 1000 }
                }
            }.runTaskTimer(plugin, 0L, 1L)
        }

        internal fun startArrowTracking(arrow: Arrow, shooter: Player, config: TracerConfig) {
            val world = arrow.world
            val startLoc = arrow.location.clone()
            val mobSet: Set<EntityType>? = config.mobs
                .filter { it.isAlive && it != EntityType.PLAYER }
                .toSet()
                .takeIf { it.isNotEmpty() }

            val data = ArrowTrackerData(
                arrow.uniqueId,
                shooter.uniqueId,
                world.uid,
                System.currentTimeMillis(),
                startLoc.x, startLoc.y, startLoc.z,
                config.maxDuration.coerceIn(20, 200),
                config.maxRange.coerceIn(8.0, 256.0),
                config.steer.coerceIn(0.05, 0.5),
                config.distanz.coerceIn(1.0, 16.0),
                mobSet,
                shooter.eyeLocation.direction.clone().normalize()
            )

            activeTrackers.remove(arrow.uniqueId)?.cancel()
            val runnable = ArrowTracker(arrow, data)
            activeTrackers[arrow.uniqueId] = runnable
            runnable.runTaskTimer(plugin, 1L, 2L)
        }
    }

    override fun build(itemStack: ItemStack): ItemStack = itemStack
    override fun execute(player: Player, itemStack: ItemStack) { }

    override fun onShoot(player: Player, bow: ItemStack, event: EntityShootBowEvent) {
        val arrow = event.projectile as? Arrow ?: return
        val p = plugin
        if (!p.isEnabled) return

        val now = System.currentTimeMillis()
        val last = playerCooldowns[player.uniqueId] ?: 0L
        if (now - last < cooldown.coerceAtLeast(0) * 50L) return
        playerCooldowns[player.uniqueId] = now

        // Store tracer configuration for this player's shot
        pendingTracers[player.uniqueId] = TracerConfig(
            shotTime = now,
            distanz = distanz,
            mobs = mobs,
            steer = steer,
            maxDuration = maxDuration,
            maxRange = maxRange
        )

        // Apply tracer to the original arrow immediately
        arrow.persistentDataContainer.set(KEY_TRACER, PersistentDataType.BYTE, 1)
        startArrowTracking(arrow, player, pendingTracers[player.uniqueId]!!)
    }

    override fun execute(
        player: Player,
        bow: ItemStack,
        damage: Double,
        hitEntity: LivingEntity,
        onDamageDealt: (LivingEntity, Double, Boolean) -> Unit
    ) { }

    internal data class TracerConfig(
        val shotTime: Long,
        val distanz: Double,
        val mobs: List<EntityType>,
        val steer: Double,
        val maxDuration: Int,
        val maxRange: Double
    )

    private data class ArrowTrackerData(
        val arrowId: UUID,
        val shooter: UUID,
        val worldId: UUID,
        val startTimeMs: Long,
        val startX: Double,
        val startY: Double,
        val startZ: Double,
        val maxDurationTicks: Int,
        val maxRange: Double,
        val steer: Double,
        val scanRadius: Double,
        val mobTypes: Set<EntityType>?,
        val shooterForward: Vector
    )

    private class ArrowTracker(private val arrow: Arrow, private val data: ArrowTrackerData) : BukkitRunnable() {
        private var ticks = 0

        override fun run() {
            if (!plugin.isEnabled) return cancelAndCleanup()
            if (!arrow.isValid || arrow.isDead || arrow.isOnGround) return cancelAndCleanup()
            if (arrow.world.uid != data.worldId) return cancelAndCleanup()

            ticks += 2
            if (ticks > data.maxDurationTicks) return cancelAndCleanup()
            if (System.currentTimeMillis() - data.startTimeMs > data.maxDurationTicks * 50L) return cancelAndCleanup()

            val loc = arrow.location
            val dx = loc.x - data.startX
            val dy = loc.y - data.startY
            val dz = loc.z - data.startZ
            if (dx*dx + dy*dy + dz*dz > data.maxRange * data.maxRange) return cancelAndCleanup()

            val nearby = arrow.world.getNearbyEntities(loc, data.scanRadius, data.scanRadius, data.scanRadius)
            val target = nearby.asSequence()
                .filterIsInstance<LivingEntity>()
                .filter { it.isValid && !it.isDead }
                .filter { it.uniqueId != data.shooter }
                .filter { data.mobTypes == null || it.type in data.mobTypes }
                .filter { isInFront(data.shooterForward, arrow.location.toVector(), it.location.toVector()) }
                .minByOrNull { it.location.distanceSquared(loc) }

            if (target == null) return
            if (target.location.distanceSquared(loc) < 1.0) return

            val currentVel = arrow.velocity
            val speed = max(currentVel.length(), 0.12)
            val predictedAim = predictedAimPoint(arrow.location.toVector(), speed, target)
            val desiredDir = predictedAim.subtract(arrow.location.toVector()).normalizeSafe() ?: return
            val currentDir = currentVel.clone().normalizeSafe() ?: return
            arrow.velocity = currentDir.multiply(1.0 - data.steer).add(desiredDir.multiply(data.steer))
                .normalizeSafe()?.multiply(speed) ?: return
        }

        private fun cancelAndCleanup() {
            activeTrackers.remove(data.arrowId)
            if (arrow.isValid) runCatching { arrow.persistentDataContainer.remove(KEY_TRACER) }
            cancel()
        }

        private fun predictedAimPoint(arrowPos: Vector, arrowSpeed: Double, target: LivingEntity): Vector {
            val targetPos = target.location.clone().add(0.0, target.height * 0.4, 0.0).toVector()
            val distance = targetPos.clone().subtract(arrowPos).length()
            val time = min(1.0, max(0.0, distance / max(arrowSpeed, 0.1)))
            return targetPos.add(target.velocity.clone().multiply(time))
        }

        private fun Vector.normalizeSafe(): Vector? = if (this.lengthSquared() < 1e-6) null else this.normalize()

        private fun isInFront(forward: Vector, origin: Vector, target: Vector): Boolean {
            val dirToTarget = target.clone().subtract(origin).normalizeSafe() ?: return false
            return forward.dot(dirToTarget) > 0
        }
    }
}