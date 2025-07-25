package de.chaos

import com.typewritermc.core.extension.Initializable
import com.typewritermc.core.extension.annotations.Singleton
import com.typewritermc.engine.paper.logger
import com.typewritermc.engine.paper.plugin
import org.bukkit.plugin.java.JavaPlugin


@Singleton
object Initializer : Initializable {

    private val javaPlugin: JavaPlugin = plugin

    override suspend fun initialize() {
        logger.info("TransientTWAddon initialisiert.")



    }

    override suspend fun shutdown() {


    }
}