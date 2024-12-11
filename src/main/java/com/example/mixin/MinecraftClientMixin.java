package com.example.mixin;

import com.example.Skia;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * <h1>MinecraftClientMixin:</h1>
 *
 * <p>
 * Mixes into the {@link MinecraftClient} class.
 * </p>
 *
 * @author fang.
 * @version 1.0.0
 * @since 09/12/2024
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /** The Minecraft window. */
    @Shadow
    @Final
    private Window window;

    /**
     * Mixes into the {@link MinecraftClient#onResolutionChanged() onResolutionChanged()} method and
     * calls the {@link Skia#onResize(int, int) Skia.onResize()} function.
     *
     * @param info The {@link CallbackInfo} which is required when injecting into a method.
     */
    @Inject(at = @At("TAIL"), method = "onResolutionChanged")
    private void onResolutionChanged(CallbackInfo info) {
        // Hooks Skia's onResize() function.
        Skia.INSTANCE.onResize(this.window.getFramebufferWidth(), this.window.getFramebufferHeight());
    }

}
