package com.example.font

import com.example.Initializer
import org.jetbrains.skia.Data.Companion.makeFromBytes
import org.jetbrains.skia.Font
import org.jetbrains.skia.FontMgr.Companion.default
import org.jetbrains.skia.Typeface
import java.io.IOException

/**
 * # FontLoader:
 *
 * Provides utilities for loading fonts and managing font caching.
 *
 * @author fang.
 * @since 10/12/2024
 */
object FontLoader {

    /** The path to your fonts asset folder. */
    private const val FONT_PATH = "/assets/template-mod/fonts/"

    /** The font cache. */
    private val fontCache: MutableMap<Pair<String, Float>, Font> = HashMap()

    /** The typeface cache. */
    private val typefaceCache: MutableMap<String, Typeface> = HashMap()

    /**
     * Retrieves or creates a font with the specified name and size.
     *
     * @param font The name of the font file. (e.g. "Roboto-Regular.ttf")
     * @param size The font's size (must be > 1).
     * @return The requested font, or an exception if loading fails.
     */
    fun getFont(font: String, size: Float): Font {
        require(size > 0) { "Font size must be greater than zero." }

        // If the font isn't cached, create it, otherwise, return it.
        return fontCache.computeIfAbsent(font to size) { Font(getTypeface(font), size) }
    }

    /**
     * Retrieves or creates a typeface with the specified name.
     *
     * @param font The name of the font file (e.g. "Roboto-Regular.ttf").
     * @return The requested typeface, or an exception if loading fails.
     */
    private fun getTypeface(font: String): Typeface {
        // If the typeface isn't cached, create it, otherwise, return it.
        return typefaceCache.computeIfAbsent(font) { loadTypeface(it) }
    }

    /**
     * Loads a typeface from the specified font file.
     *
     * @param font The name of the font file (e.g., "Roboto-Regular.ttf").
     * @return The loaded typeface.
     * @throws IllegalArgumentException If the font file can't be found.
     * @throws IOException If the font file cannot be read or parsed. Likely corrupted.
     */
    private fun loadTypeface(font: String): Typeface {
        val path = "$FONT_PATH$font"
        val inputStream = FontLoader::class.java.getResourceAsStream(path)
            ?: throw IllegalArgumentException("File \"$font\" not found in path $path.")

        return try {
            inputStream.use {
                makeFromBytes(it.readAllBytes()).use { data -> default.makeFromData(data)!! }
            }
        } catch (e: IOException) {
            Initializer.logger.error("Error loading font \"$font\": ${e.message}", e)
            throw e
        }
    }

}
