package net.js.random.mixin;

import com.mojang.authlib.GameProfile;
import net.js.random.OfflineSkinClient;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerInfo.class, remap = false)
public abstract class PlayerInfoMixin {
    @Shadow(remap = false)
    @Final
    private GameProfile profile;

    @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true, remap = false)
    private void offlineSkins$useFileSkin(CallbackInfoReturnable<PlayerSkin> callback) {
        PlayerSkin skin = OfflineSkinClient.getSkin(this.profile);
        if (skin != null) {
            callback.setReturnValue(skin);
        }
    }
}
