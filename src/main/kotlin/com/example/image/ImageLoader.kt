package com.example.image

import org.jetbrains.skia.Image

/**
 * # ImageLoader:
 *
 * Provides utilities for loading images.
 *
 * @author fang.
 * @since 10/12/2024
 */
object ImageLoader {

    /** The path to your images asset folder. */
    private const val IMAGE_PATH = "/assets/template-mod/textures/"

    /**
     * Creates an image from the specified file.
     *
     * @param image The file's path.
     * @return A Skia image from the specified file.
     */
    fun getImage(image: String): Image {
        val path = "$IMAGE_PATH$image"
        val inputStream = ImageLoader::class.java.getResourceAsStream(path)
            ?: throw IllegalArgumentException("\"$image\" file not found in path $path.")

        // Returns the image from the file's bytes.
        return Image.makeFromEncoded(inputStream.readAllBytes())
    }

}