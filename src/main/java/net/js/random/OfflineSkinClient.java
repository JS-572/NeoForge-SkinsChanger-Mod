package net.js.random;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OfflineSkinClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineSkinClient.class);
    private static final Map<String, Identifier> SKINS = new HashMap<>();

    private OfflineSkinClient() {
    }

    public static void receive(SkinPayload payload) {
        String username = payload.username().toLowerCase(Locale.ROOT);
        Identifier textureId = Identifier.fromNamespaceAndPath(RandomnessMod.MOD_ID, "skins/" + username);
        NativeImage image;
        try {
            image = NativeImage.read(payload.image());
        } catch (IOException exception) {
            LOGGER.warn("Could not decode the offline skin sent for {}", payload.username(), exception);
            return;
        }

        if (image.getWidth() != 64 || image.getHeight() != 64) {
            image.close();
            LOGGER.warn("Ignoring offline skin sent for {} because it is not 64x64 pixels", payload.username());
            return;
        }

        var textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.release(textureId);
        DynamicTexture texture = new DynamicTexture(() -> "Offline skin for " + payload.username(), image);
        textureManager.register(textureId, texture);
        SKINS.put(username, textureId);
    }

    public static PlayerSkin getSkin(GameProfile profile) {
        Identifier textureId = SKINS.get(profile.name().toLowerCase(Locale.ROOT));
        if (textureId == null) {
            return null;
        }

        PlayerSkin defaultSkin = DefaultPlayerSkin.get(profile);
        return PlayerSkin.insecure(
            new ClientAsset.ResourceTexture(textureId, textureId),
            null,
            null,
            defaultSkin.model()
        );
    }

    public static void clear() {
        var textureManager = Minecraft.getInstance().getTextureManager();
        for (Identifier textureId : SKINS.values()) {
            textureManager.release(textureId);
        }
        SKINS.clear();
    }
}
