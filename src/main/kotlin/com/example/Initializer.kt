package com.example

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * # Initializer:
 *
 * Initializes the Skiko for Minecraft mod.
 *
 * @author fang.
 * @since 09/12/2024
 */
class Initializer : ModInitializer {

	/**
	 * This code runs as soon as Minecraft is in a mod-load-ready state.
	 *
	 * However, some things (like resources) may still be uninitialized.
	 * Proceed with mild caution.
	 */
	override fun onInitialize() {
		// Finishes initialization.
		logger.info("The mod has been initialized successfully.")
	}

	companion object {
		/** The mod's logger. */
		val logger: Logger = LoggerFactory.getLogger("Template Mod")
	}

}
