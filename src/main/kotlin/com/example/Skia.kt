package com.example

import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferRenderer
import org.jetbrains.skia.*
import org.jetbrains.skia.BackendRenderTarget.Companion.makeGL
import org.jetbrains.skia.DirectContext.Companion.makeGL
import org.jetbrains.skia.Surface.Companion.makeFromBackendRenderTarget
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL14
import org.lwjgl.opengl.GL33

/**
 * # Skia:
 *
 * Handles Skia rendering and OpenGL states.
 *
 * @author fang.
 * @since 09/12/2024
 */
object Skia {

    /** The skia context. */
    private val context: DirectContext by lazy { makeGL() }

    /** The skia backend render target. */
    private var renderTarget: BackendRenderTarget? = null

    /** The skia surface. */
    private var surface: Surface? = null

    /** The surface canvas. */
    private var canvas: Canvas? = null

    /** The save count used in restoration. */
    private var saveCount: Int = 0

    /**
     * Updates the surface's window and height according to the current window's size.
     *
     * Should be called on window resize.
     */
    fun onResize(width: Int, height: Int) {
        // Closes the render target.
        renderTarget?.close().also { renderTarget = null }

        // Closes the surface.
        surface?.close().also { surface = null }

        // And re-creates them.
        renderTarget = makeGL(
            width,
            height,
            0,
            8,
            MinecraftClient.getInstance().framebuffer.fbo,
            FramebufferFormat.GR_GL_RGBA8
        )

        surface = makeFromBackendRenderTarget(
            context,
            renderTarget!!,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.Companion.sRGB,
            SurfaceProps(PixelGeometry.RGB_H)
        )

        // Updates the canvas.
        canvas = surface!!.canvas
    }

    /**
     * Begins the rendering process, should be called before starting to render.
     */
    fun begin() {
        RenderSystem.assertOnRenderThread()

        // Resets the changes made by Minecraft.
        RenderSystem.pixelStore(GL11.GL_UNPACK_ROW_LENGTH, 0)
        RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0)
        RenderSystem.pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0)
        RenderSystem.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 4)
        context.resetAll()
        saveCount = canvas!!.save()
    }

    /**
     * Finalizes the rendering process, should be called after rendering.
     */
    fun finish() {
        RenderSystem.assertOnRenderThread()

        // Restores the previously saved state and flushes.
        canvas!!.restoreToCount(saveCount)
        context.flush()

        // Forcefully resets the changes made by Skia.
        BufferRenderer.reset()
        RenderSystem.bindTexture(GL11.GL_ZERO)
        GL11.glBindTexture(GlConst.GL_TEXTURE_2D, GL11.GL_ZERO)
        GL33.glBindSampler(GL11.GL_ZERO, GL11.GL_ZERO)

        RenderSystem.colorMask(true, true, true, true)
        GL11.glColorMask(true, true, true, true)

        RenderSystem.depthMask(true)
        GL11.glDepthMask(true)

        RenderSystem.disableBlend()
        GL11.glDisable(GL11.GL_BLEND)

        RenderSystem.blendEquation(GL14.GL_FUNC_ADD)
        GL14.glBlendEquation(GL14.GL_FUNC_ADD)

        RenderSystem.disableScissor()
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glDisable(GL11.GL_STENCIL_TEST)

        RenderSystem.disableDepthTest()
        GL11.glDisable(GL11.GL_DEPTH_TEST)

        RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.SRC_ALPHA,
            GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SrcFactor.ZERO,
            GlStateManager.DstFactor.ONE
        )
        GL14.glBlendFuncSeparate(
            GL14.GL_SRC_ALPHA,
            GL14.GL_ONE_MINUS_SRC_ALPHA,
            GL14.GL_ZERO,
            GL14.GL_ONE
        )
    }

}
